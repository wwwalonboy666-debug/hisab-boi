package com.example.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.AppPreferenceEntity
import com.example.data.entity.BudgetEntity
import com.example.data.entity.DebtEntity
import com.example.data.entity.DebtPaymentEntity
import com.example.data.entity.SavingsGoalEntity
import com.example.data.entity.TransactionEntity
import com.example.data.repository.HisabBoiRepository
import com.example.model.AppLanguage
import com.example.model.Category
import com.example.model.CategoryRegistry
import com.example.model.DebtFilter
import com.example.model.DebtStatus
import com.example.model.DebtType
import com.example.model.DebtViewMode
import com.example.model.DebtWithPayments
import com.example.model.PersonDebtSummary
import com.example.model.ThemeMode
import com.example.model.TransactionType
import com.example.receiver.DebtReminderScheduler
import com.example.data.BackupPreferenceManager
import com.example.data.GoogleDriveBackupService
import com.example.data.UserProfileManager
import com.example.model.AutoBackupFrequency
import com.example.model.BackupUiState
import com.example.model.GoogleAccountInfo
import com.example.model.UserProfile
import com.example.receiver.AutoBackupScheduler
import com.example.util.DateUtils
import com.example.util.Strings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class CategorySpend(
    val category: Category,
    val amount: Double,
    val percentage: Float
)

data class MonthBudgetState(
    val budget: BudgetEntity?,
    val budgetAmount: Double,
    val usedAmount: Double,
    val remainingAmount: Double,
    val progress: Float,
    val isNearLimit: Boolean, // >= 80%
    val isOverBudget: Boolean // >= 100%
)

data class ReportState(
    val totalIncome: Double,
    val totalExpense: Double,
    val netBalance: Double,
    val categorySpends: List<CategorySpend>,
    val topCategory: Category?,
    val topCategoryAmount: Double,
    val previousMonthExpense: Double?,
    val expenseDifferencePercentage: Float?, // positive means increased, negative means decreased
    val incomeSpentPercentage: Float?
)

private data class MonthExpenseContext(
    val transactions: List<TransactionEntity>,
    val income: Double,
    val expense: Double,
    val customCats: List<com.example.data.entity.CustomCategoryEntity>,
    val priorRange: Pair<Long, Long>
)

class HisabBoiViewModel(
    private val repository: HisabBoiRepository,
    private val appContext: Context? = null
) : ViewModel() {

    private val backupPrefManager: BackupPreferenceManager? = appContext?.let { BackupPreferenceManager(it) }
    private val driveService: GoogleDriveBackupService? = if (appContext != null && backupPrefManager != null) {
        GoogleDriveBackupService(appContext, backupPrefManager)
    } else null
    private val userProfileManager: UserProfileManager? = appContext?.let { UserProfileManager(it) }

    val userProfile: StateFlow<UserProfile> = userProfileManager?.profile
        ?: MutableStateFlow(UserProfile()).asStateFlow()

    // Backup UI State
    private val _backupUiState = MutableStateFlow(
        BackupUiState(
            account = backupPrefManager?.connectedAccount?.value,
            lastBackupTime = backupPrefManager?.lastBackupTime?.value,
            autoBackupFrequency = backupPrefManager?.autoBackupFrequency?.value ?: AutoBackupFrequency.OFF
        )
    )
    val backupUiState: StateFlow<BackupUiState> = _backupUiState.asStateFlow()

    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonthIndex = calendar.get(Calendar.MONTH) // 0-based

    // Selected Month & Year
    private val _selectedYear = MutableStateFlow(currentYear)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonthIndex = MutableStateFlow(currentMonthIndex)
    val selectedMonthIndex: StateFlow<Int> = _selectedMonthIndex.asStateFlow()

    // Transient UI Message Feedback
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage = _userMessage.asSharedFlow()

    // Preferences
    val preferences: StateFlow<AppPreferenceEntity> = repository.preferences
        .map { it ?: AppPreferenceEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppPreferenceEntity()
        )

    val currentLanguage: StateFlow<AppLanguage> = preferences.map { pref ->
        if (pref.language.equals("en", ignoreCase = true)) AppLanguage.EN else AppLanguage.BN
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.BN)

    val currentThemeMode: StateFlow<ThemeMode> = preferences.map { pref ->
        when (pref.themeMode) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val strings: StateFlow<Strings> = currentLanguage.map { Strings(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Strings(AppLanguage.BN))

    val userNameDisplay: StateFlow<String> = combine(
        userProfile,
        backupUiState,
        currentLanguage
    ) { profile, bState, lang ->
        val localName = profile.name.trim()
        if (localName.isNotEmpty()) {
            localName
        } else {
            val googleName = bState.account?.displayName?.trim()
            if (!googleName.isNullOrEmpty()) {
                googleName
            } else {
                if (lang == AppLanguage.BN) "গেস্ট ইউজার" else "Guest User"
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "গেস্ট ইউজার")

    // Transactions
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = repository.getRecentTransactions(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall Balance Summary (All-time)
    val overallIncome: StateFlow<Double> = repository.totalIncome
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val overallExpense: StateFlow<Double> = repository.totalExpense
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val overallBalance: StateFlow<Double> = combine(overallIncome, overallExpense) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Month-keyed transactions
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedMonthTransactions: StateFlow<List<TransactionEntity>> =
        combine(_selectedYear, _selectedMonthIndex) { year, month ->
            DateUtils.getMonthRangeMillis(year, month)
        }.flatMapLatest { (startMillis, endMillis) ->
            repository.getTransactionsForMonth(startMillis, endMillis)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly Totals
    val selectedMonthIncome: StateFlow<Double> = selectedMonthTransactions.map { list ->
        list.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val selectedMonthExpense: StateFlow<Double> = selectedMonthTransactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Budget for Selected Month
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedMonthBudget: StateFlow<BudgetEntity?> =
        combine(_selectedYear, _selectedMonthIndex) { year, month ->
            DateUtils.toMonthKey(year, month)
        }.flatMapLatest { monthKey ->
            repository.getBudgetForMonth(monthKey)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val budgetState: StateFlow<MonthBudgetState> =
        combine(selectedMonthBudget, selectedMonthExpense) { budget, expense ->
            val budgetAmount = budget?.amount ?: 0.0
            val used = expense
            val remaining = (budgetAmount - used).coerceAtLeast(0.0)
            val progress = if (budgetAmount > 0) (used / budgetAmount).toFloat() else 0f
            MonthBudgetState(
                budget = budget,
                budgetAmount = budgetAmount,
                usedAmount = used,
                remainingAmount = remaining,
                progress = progress,
                isNearLimit = progress >= 0.80f && progress < 1.0f,
                isOverBudget = progress >= 1.0f
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MonthBudgetState(null, 0.0, 0.0, 0.0, 0f, false, false)
        )

    // Savings Goals
    val allSavingsGoals: StateFlow<List<SavingsGoalEntity>> = repository.allSavingsGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Custom Categories
    val customCategories: StateFlow<List<com.example.data.entity.CustomCategoryEntity>> =
        repository.allCustomCategories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCustomCategories: StateFlow<List<com.example.data.entity.CustomCategoryEntity>> =
        repository.activeCustomCategories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Debts & Repayments
    val allDebtsWithPayments: StateFlow<List<DebtWithPayments>> = combine(
        repository.allDebts,
        repository.allDebtPayments
    ) { debts, payments ->
        val paymentsByDebt = payments.groupBy { it.debtId }
        debts.map { debt ->
            DebtWithPayments(
                debt = debt,
                payments = paymentsByDebt[debt.id] ?: emptyList()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalLentRemaining: StateFlow<Double> = allDebtsWithPayments.map { list ->
        list.filter { it.debt.type == DebtType.LENT.name && !it.isFullyPaid }
            .sumOf { it.remainingAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalBorrowedRemaining: StateFlow<Double> = allDebtsWithPayments.map { list ->
        list.filter { it.debt.type == DebtType.BORROWED.name && !it.isFullyPaid }
            .sumOf { it.remainingAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Debt Search & Filter States
    val debtSearchQuery = MutableStateFlow("")
    val debtFilter = MutableStateFlow(DebtFilter.ALL)
    val debtFilterMode = debtFilter
    val debtViewMode = MutableStateFlow(DebtViewMode.LIST)

    val filteredDebts: StateFlow<List<DebtWithPayments>> = combine(
        allDebtsWithPayments,
        debtSearchQuery,
        debtFilter
    ) { debts, query, filter ->
        val trimmed = query.trim()
        debts.filter { item ->
            val matchesSearch = trimmed.isEmpty() ||
                item.debt.personName.contains(trimmed, ignoreCase = true) ||
                item.debt.note.contains(trimmed, ignoreCase = true)

            val matchesFilter = when (filter) {
                DebtFilter.ALL -> true
                DebtFilter.LENT -> item.debt.type == DebtType.LENT.name
                DebtFilter.BORROWED -> item.debt.type == DebtType.BORROWED.name
                DebtFilter.ACTIVE -> item.computedStatus == DebtStatus.ACTIVE
                DebtFilter.PAID -> item.computedStatus == DebtStatus.PAID
                DebtFilter.OVERDUE -> item.computedStatus == DebtStatus.OVERDUE
            }

            matchesSearch && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personDebtSummaries: StateFlow<List<PersonDebtSummary>> = combine(
        allDebtsWithPayments,
        debtSearchQuery
    ) { debts, query ->
        val trimmed = query.trim()
        val filtered = if (trimmed.isEmpty()) debts else debts.filter {
            it.debt.personName.contains(trimmed, ignoreCase = true)
        }
        filtered.groupBy { it.debt.personName }
            .map { (name, personDebts) ->
                PersonDebtSummary(name, personDebts)
            }
            .sortedByDescending { it.activeCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Filter State for Transactions Screen
    val searchQuery = MutableStateFlow("")
    val filterType = MutableStateFlow("ALL") // "ALL", "INCOME", "EXPENSE"
    val selectedCategoryKey = MutableStateFlow<String?>(null)

    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        selectedMonthTransactions,
        searchQuery,
        filterType,
        selectedCategoryKey,
        customCategories
    ) { transactions, query, type, catKey, customCats ->
        transactions.filter { tx ->
            val matchesType = when (type) {
                "INCOME" -> tx.type == TransactionType.INCOME.name
                "EXPENSE" -> tx.type == TransactionType.EXPENSE.name
                else -> true
            }
            val matchesCategory = catKey == null || tx.categoryKey == catKey
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val cat = CategoryRegistry.getCategory(tx.categoryKey, customCats)
                tx.note.contains(query, ignoreCase = true) ||
                        cat.nameBn.contains(query, ignoreCase = true) ||
                        cat.nameEn.contains(query, ignoreCase = true)
            }
            matchesType && matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reports State
    @OptIn(ExperimentalCoroutinesApi::class)
    val reportState: StateFlow<ReportState> = combine(
        selectedMonthTransactions,
        combine(selectedMonthIncome, selectedMonthExpense) { inc, exp -> Pair(inc, exp) },
        customCategories,
        combine(_selectedYear, _selectedMonthIndex) { y, m -> Pair(y, m) }
    ) { transactions, totals, customCats, yearMonth ->
        val (income, expense) = totals
        val (year, month) = yearMonth
        val priorCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            add(Calendar.MONTH, -1)
        }
        val priorYear = priorCal.get(Calendar.YEAR)
        val priorMonth = priorCal.get(Calendar.MONTH)
        val priorRange = DateUtils.getMonthRangeMillis(priorYear, priorMonth)

        MonthExpenseContext(transactions, income, expense, customCats, priorRange)
    }.flatMapLatest { ctx ->
        repository.getMonthlyExpense(ctx.priorRange.first, ctx.priorRange.second).map { priorExp ->
            // Category breakdowns
            val expenseTxList = ctx.transactions.filter { it.type == TransactionType.EXPENSE.name }
            val categoryGrouped = expenseTxList.groupBy { it.categoryKey }
            val spends = categoryGrouped.map { (catKey, list) ->
                val amount = list.sumOf { it.amount }
                val percentage = if (ctx.expense > 0) ((amount / ctx.expense) * 100).toFloat() else 0f
                CategorySpend(
                    category = CategoryRegistry.getCategory(catKey, ctx.customCats),
                    amount = amount,
                    percentage = percentage
                )
            }.sortedByDescending { it.amount }

            val topSpend = spends.firstOrNull()
            val net = ctx.income - ctx.expense

            val diffPercentage = if (priorExp != null && priorExp > 0 && ctx.expense > 0) {
                (((ctx.expense - priorExp) / priorExp) * 100).toFloat()
            } else null

            val incSpentPct = if (ctx.income > 0) {
                ((ctx.expense / ctx.income) * 100).toFloat()
            } else null

            ReportState(
                totalIncome = ctx.income,
                totalExpense = ctx.expense,
                netBalance = net,
                categorySpends = spends,
                topCategory = topSpend?.category,
                topCategoryAmount = topSpend?.amount ?: 0.0,
                previousMonthExpense = priorExp,
                expenseDifferencePercentage = diffPercentage,
                incomeSpentPercentage = incSpentPct
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ReportState(0.0, 0.0, 0.0, emptyList(), null, 0.0, null, null, null)
    )

    init {
        viewModelScope.launch {
            repository.ensurePreferencesInitialized()
        }
    }

    // Navigation & Month change
    fun nextMonth() {
        val currentM = _selectedMonthIndex.value
        val currentY = _selectedYear.value
        if (currentM == 11) {
            _selectedYear.value = currentY + 1
            _selectedMonthIndex.value = 0
        } else {
            _selectedMonthIndex.value = currentM + 1
        }
    }

    fun previousMonth() {
        val currentM = _selectedMonthIndex.value
        val currentY = _selectedYear.value
        if (currentM == 0) {
            _selectedYear.value = currentY - 1
            _selectedMonthIndex.value = 11
        } else {
            _selectedMonthIndex.value = currentM - 1
        }
    }

    fun resetToCurrentMonth() {
        val now = Calendar.getInstance()
        _selectedYear.value = now.get(Calendar.YEAR)
        _selectedMonthIndex.value = now.get(Calendar.MONTH)
    }

    // Actions: Transactions
    fun addTransaction(
        type: TransactionType,
        amount: Double,
        categoryKey: String,
        dateMillis: Long,
        note: String
    ) {
        if (amount <= 0) return
        viewModelScope.launch {
            val tx = TransactionEntity(
                type = type.name,
                amount = amount,
                categoryKey = categoryKey,
                dateMillis = dateMillis,
                note = note.trim()
            )
            repository.insertTransaction(tx)
            _userMessage.emit(strings.value.savedSuccessToast)
        }
    }

    fun updateTransaction(
        id: Long,
        type: TransactionType,
        amount: Double,
        categoryKey: String,
        dateMillis: Long,
        note: String
    ) {
        if (amount <= 0) return
        viewModelScope.launch {
            val tx = TransactionEntity(
                id = id,
                type = type.name,
                amount = amount,
                categoryKey = categoryKey,
                dateMillis = dateMillis,
                note = note.trim(),
                updatedAt = System.currentTimeMillis()
            )
            repository.updateTransaction(tx)
            _userMessage.emit(strings.value.updatedSuccessToast)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _userMessage.emit(strings.value.deletedSuccessToast)
        }
    }

    // Actions: Budget
    fun setBudget(amount: Double) {
        if (amount <= 0) return
        viewModelScope.launch {
            val monthKey = DateUtils.toMonthKey(_selectedYear.value, _selectedMonthIndex.value)
            repository.setBudget(monthKey, amount)
            _userMessage.emit(strings.value.savedSuccessToast)
        }
    }

    fun deleteBudget() {
        viewModelScope.launch {
            val monthKey = DateUtils.toMonthKey(_selectedYear.value, _selectedMonthIndex.value)
            repository.deleteBudgetForMonth(monthKey)
            _userMessage.emit(strings.value.deletedSuccessToast)
        }
    }

    // Actions: Savings
    fun createSavingsGoal(name: String, targetAmount: Double, initialSaved: Double = 0.0) {
        if (name.isBlank() || targetAmount <= 0) return
        viewModelScope.launch {
            repository.insertSavingsGoal(name.trim(), targetAmount, initialSaved)
            _userMessage.emit(strings.value.savedSuccessToast)
        }
    }

    fun addSavings(goalId: Long, amount: Double) {
        if (amount <= 0) return
        viewModelScope.launch {
            repository.addSavings(goalId, amount)
            _userMessage.emit(strings.value.savedSuccessToast)
        }
    }

    fun updateSavingsGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.updateSavingsGoal(goal)
            _userMessage.emit(strings.value.updatedSuccessToast)
        }
    }

    fun deleteSavingsGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteSavingsGoalById(id)
            _userMessage.emit(strings.value.deletedSuccessToast)
        }
    }

    // Actions: Preferences & Language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            repository.setLanguage(language.code)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(mode.code)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingCompleted(true)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
        }
    }

    // Profile Management
    fun saveUserProfile(name: String, photoUri: Uri?) {
        if (appContext != null && userProfileManager != null) {
            userProfileManager.saveProfile(name, photoUri, appContext)
        }
    }

    fun updateUserProfileName(name: String) {
        userProfileManager?.updateName(name)
    }

    fun updateUserProfilePhoto(photoUri: Uri) {
        if (appContext != null && userProfileManager != null) {
            userProfileManager.updatePhoto(photoUri, appContext)
        }
    }

    fun removeUserProfilePhoto() {
        if (appContext != null && userProfileManager != null) {
            userProfileManager.removePhoto(appContext)
        }
    }

    // Custom Category Management
    fun createCustomCategory(
        nameBn: String,
        nameEn: String,
        type: TransactionType,
        iconName: String,
        colorHex: String = "#4CAF50",
        onCreated: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val key = "CUSTOM_${System.currentTimeMillis()}"
            val insertedKey = repository.insertCustomCategory(
                nameBn = nameBn,
                nameEn = nameEn.ifBlank { nameBn },
                type = type.name,
                iconName = iconName,
                colorHex = colorHex,
                key = key
            )
            _userMessage.emit(strings.value.categorySaveSuccess)
            onCreated(insertedKey)
        }
    }

    fun updateCustomCategory(category: com.example.data.entity.CustomCategoryEntity) {
        viewModelScope.launch {
            repository.updateCustomCategory(category)
            _userMessage.emit(strings.value.categorySaveSuccess)
        }
    }

    suspend fun getCategoryTransactionCount(key: String): Int {
        return repository.getTransactionCountForCategory(key)
    }

    fun archiveCustomCategory(category: com.example.data.entity.CustomCategoryEntity) {
        viewModelScope.launch {
            repository.setCustomCategoryArchived(category.id, true)
            _userMessage.emit(strings.value.categoryArchivedSuccess)
        }
    }

    fun unarchiveCustomCategory(category: com.example.data.entity.CustomCategoryEntity) {
        viewModelScope.launch {
            repository.setCustomCategoryArchived(category.id, false)
            _userMessage.emit(strings.value.categoryUnarchivedSuccess)
        }
    }

    fun deleteCustomCategory(category: com.example.data.entity.CustomCategoryEntity) {
        viewModelScope.launch {
            repository.deleteCustomCategory(category)
            _userMessage.emit(strings.value.categoryDeleteSuccess)
        }
    }

    fun getCategory(key: String): Category {
        return CategoryRegistry.getCategory(key, customCategories.value)
    }

    // Debt Manager Actions
    fun addDebt(
        personName: String,
        type: DebtType,
        amount: Double,
        createdDate: Long,
        dueDate: Long?,
        note: String,
        context: Context? = null
    ) {
        viewModelScope.launch {
            val debt = DebtEntity(
                personName = personName.trim(),
                type = type.name,
                originalAmount = amount,
                createdDate = createdDate,
                dueDate = dueDate,
                note = note.trim(),
                status = "ACTIVE"
            )
            val newId = repository.insertDebt(debt)
            if (context != null && dueDate != null) {
                val pref = preferences.value
                DebtReminderScheduler.scheduleReminder(
                    context = context,
                    debt = debt.copy(id = newId),
                    daysBefore = pref.debtReminderDaysBefore,
                    isEnabled = pref.debtReminderEnabled
                )
            }
            _userMessage.emit(strings.value.debtSavedSuccess)
        }
    }

    fun updateDebt(
        debtId: Long,
        personName: String,
        type: DebtType,
        amount: Double,
        createdDate: Long,
        dueDate: Long?,
        note: String,
        context: Context? = null
    ) {
        viewModelScope.launch {
            val existing = repository.getDebtWithPaymentsDirect(debtId) ?: return@launch
            if (amount < existing.totalPaid - 0.001) {
                _userMessage.emit(strings.value.originalAmountLessThanPaidError)
                return@launch
            }
            val isNowPaid = existing.totalPaid >= amount - 0.001
            val status = if (isNowPaid) {
                "PAID"
            } else if (dueDate != null && dueDate < System.currentTimeMillis()) {
                "OVERDUE"
            } else {
                "ACTIVE"
            }
            val updated = existing.debt.copy(
                personName = personName.trim(),
                type = type.name,
                originalAmount = amount,
                createdDate = createdDate,
                dueDate = dueDate,
                note = note.trim(),
                status = status,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateDebt(updated)
            if (context != null) {
                val pref = preferences.value
                if (isNowPaid || dueDate == null) {
                    DebtReminderScheduler.cancelReminder(context, debtId)
                } else {
                    DebtReminderScheduler.scheduleReminder(
                        context = context,
                        debt = updated,
                        daysBefore = pref.debtReminderDaysBefore,
                        isEnabled = pref.debtReminderEnabled
                    )
                }
            }
            _userMessage.emit(strings.value.debtUpdatedSuccess)
        }
    }

    fun deleteDebt(debt: DebtEntity, context: Context? = null) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
            if (context != null) {
                DebtReminderScheduler.cancelReminder(context, debt.id)
            }
            _userMessage.emit(strings.value.debtDeletedSuccess)
        }
    }

    fun addRepayment(
        debtId: Long,
        amount: Double,
        date: Long,
        note: String,
        context: Context? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val existing = repository.getDebtWithPaymentsDirect(debtId)
            if (existing == null) {
                onComplete(false)
                return@launch
            }
            if (amount > existing.remainingAmount + 0.001) {
                _userMessage.emit(strings.value.overpaymentError)
                onComplete(false)
                return@launch
            }
            val payment = DebtPaymentEntity(
                debtId = debtId,
                amount = amount,
                paymentDate = date,
                note = note.trim()
            )
            repository.insertDebtPayment(payment)
            val updated = repository.getDebtWithPaymentsDirect(debtId)
            if (updated != null && updated.isFullyPaid && context != null) {
                DebtReminderScheduler.cancelReminder(context, debtId)
            }
            _userMessage.emit(strings.value.repaymentSavedSuccess)
            onComplete(true)
        }
    }

    fun deleteRepayment(payment: DebtPaymentEntity) {
        viewModelScope.launch {
            repository.deleteDebtPayment(payment)
        }
    }

    fun setDebtReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDebtReminderEnabled(enabled)
        }
    }

    fun setDebtReminderDaysBefore(days: Int) {
        viewModelScope.launch {
            repository.setDebtReminderDaysBefore(days)
        }
    }

    fun setDebtSearchQuery(query: String) {
        debtSearchQuery.value = query
    }

    fun setDebtFilterMode(mode: DebtFilter) {
        debtFilter.value = mode
    }

    fun setDebtViewMode(mode: DebtViewMode) {
        debtViewMode.value = mode
    }

    // ==========================================
    // BACKUP & RESTORE ACTIONS
    // ==========================================

    fun connectGoogleAccount(account: GoogleAccountInfo, context: Context? = null) {
        val targetPref = backupPrefManager ?: context?.let { BackupPreferenceManager(it) }
        targetPref?.saveAccount(account)
        _backupUiState.update {
            it.copy(account = account, errorMessage = null)
        }
        checkExistingCloudBackup(account.email, context)
    }

    fun checkExistingCloudBackup(userEmail: String, context: Context? = null) {
        val targetPref = backupPrefManager ?: context?.let { BackupPreferenceManager(it) }
        val targetService = driveService ?: if (context != null && targetPref != null) GoogleDriveBackupService(context, targetPref) else null
        viewModelScope.launch {
            try {
                val exists = targetService?.checkCloudBackupExists(userEmail) ?: false
                if (exists) {
                    _backupUiState.update { it.copy(cloudBackupExists = true) }
                }
            } catch (_: Exception) {}
        }
    }

    fun disconnectGoogleAccount(context: Context? = null) {
        val targetPref = backupPrefManager ?: context?.let { BackupPreferenceManager(it) }
        val targetCtx = appContext ?: context
        if (targetCtx != null) {
            AutoBackupScheduler.updateSchedule(targetCtx, AutoBackupFrequency.OFF)
        }
        targetPref?.saveAccount(null)
        _backupUiState.update {
            it.copy(
                account = null,
                autoBackupFrequency = AutoBackupFrequency.OFF,
                lastStatusMessage = null,
                errorMessage = null
            )
        }
    }

    fun setAutoBackupFrequency(freq: AutoBackupFrequency, context: Context? = null) {
        val targetPref = backupPrefManager ?: context?.let { BackupPreferenceManager(it) }
        val targetCtx = appContext ?: context
        if (targetCtx != null) {
            AutoBackupScheduler.updateSchedule(targetCtx, freq)
        }
        targetPref?.setAutoBackupFrequency(freq)
        _backupUiState.update { it.copy(autoBackupFrequency = freq) }
    }

    fun triggerBackupNow(context: Context? = null, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        val targetPref = backupPrefManager ?: context?.let { BackupPreferenceManager(it) }
        val targetService = driveService ?: if (context != null && targetPref != null) GoogleDriveBackupService(context, targetPref) else null
        val account = _backupUiState.value.account

        if (account == null) {
            _backupUiState.update { it.copy(errorMessage = "কোনো গুগল অ্যাকাউন্ট সংযুক্ত নেই।") }
            onResult(false, "কোনো গুগল অ্যাকাউন্ট সংযুক্ত নেই।")
            return
        }

        viewModelScope.launch {
            _backupUiState.update { it.copy(isBackingUp = true, errorMessage = null) }
            try {
                val encryptedBytes = repository.createEncryptedBackup(account.email, account.displayName)
                val uploadResult = targetService?.uploadBackup(encryptedBytes, account.email)

                if (uploadResult != null && uploadResult.isSuccess) {
                    val timestamp = uploadResult.getOrNull() ?: System.currentTimeMillis()
                    _backupUiState.update {
                        it.copy(
                            isBackingUp = false,
                            lastBackupTime = timestamp,
                            cloudBackupExists = true,
                            lastStatusMessage = "Backup সফল হয়েছে ✓",
                            errorMessage = null
                        )
                    }
                    onResult(true, "✅ Backup সফল হয়েছে")
                } else {
                    val rawError = uploadResult?.exceptionOrNull()?.localizedMessage
                        ?: "Backup সম্পন্ন করা যায়নি।"
                    val displayError = "$rawError\nআপনার বর্তমান হিসাব নিরাপদ আছে।"
                    _backupUiState.update {
                        it.copy(
                            isBackingUp = false,
                            errorMessage = displayError
                        )
                    }
                    onResult(false, "❌ $displayError")
                }
            } catch (e: Exception) {
                val displayError = "ত্রুটি: ${e.localizedMessage ?: "অপ্রত্যাশিত সমস্যা"}। বর্তমান হিসাব নিরাপদ আছে।"
                _backupUiState.update {
                    it.copy(
                        isBackingUp = false,
                        errorMessage = displayError
                    )
                }
                onResult(false, "❌ $displayError")
            }
        }
    }

    fun triggerRestore(context: Context, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        val targetPref = backupPrefManager ?: BackupPreferenceManager(context)
        val targetService = driveService ?: GoogleDriveBackupService(context, targetPref)
        val account = _backupUiState.value.account

        if (account == null) {
            _backupUiState.update { it.copy(errorMessage = "কোনো গুগল অ্যাকাউন্ট সংযুক্ত নেই।") }
            onResult(false, "কোনো গুগল অ্যাকাউন্ট সংযুক্ত নেই।")
            return
        }

        viewModelScope.launch {
            _backupUiState.update { it.copy(isRestoring = true, errorMessage = null) }
            try {
                val downloadResult = targetService.downloadBackup(account.email)
                if (downloadResult.isSuccess) {
                    val bytes = downloadResult.getOrThrow()
                    val restoreResult = repository.restoreEncryptedBackup(bytes, account.email, context)
                    if (restoreResult.isSuccess) {
                        _backupUiState.update {
                            it.copy(
                                isRestoring = false,
                                cloudBackupExists = true,
                                lastStatusMessage = "আপনার হিসাব সফলভাবে Restore হয়েছে ✓",
                                errorMessage = null
                            )
                        }
                        onResult(true, "✅ আপনার হিসাব সফলভাবে Restore হয়েছে")
                    } else {
                        val restoreError = restoreResult.exceptionOrNull()?.localizedMessage
                            ?: "Backup ফাইলটি ডিক্রিপ্ট বা ভ্যালিডেট করা যায়নি।"
                        val displayMsg = "$restoreError\nআপনার বর্তমান হিসাব নিরাপদ আছে।"
                        _backupUiState.update {
                            it.copy(
                                isRestoring = false,
                                errorMessage = displayMsg
                            )
                        }
                        onResult(false, "❌ $displayMsg")
                    }
                } else {
                    val downloadError = downloadResult.exceptionOrNull()?.localizedMessage
                        ?: "Google Drive-এ কোনো ব্যাকআপ ফাইল পাওয়া যায়নি।"
                    val displayMsg = "$downloadError\nআপনার বর্তমান হিসাব নিরাপদ আছে।"
                    _backupUiState.update {
                        it.copy(
                            isRestoring = false,
                            errorMessage = displayMsg
                        )
                    }
                    onResult(false, "❌ $displayMsg")
                }
            } catch (e: Exception) {
                val displayMsg = "Restore ব্যর্থ হয়েছে: ${e.localizedMessage}। বর্তমান হিসাব নিরাপদ আছে।"
                _backupUiState.update {
                    it.copy(
                        isRestoring = false,
                        errorMessage = displayMsg
                    )
                }
                onResult(false, "❌ $displayMsg")
            }
        }
    }

    fun deleteCloudBackup(context: Context? = null, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        val targetPref = backupPrefManager ?: context?.let { BackupPreferenceManager(it) }
        val targetService = driveService ?: if (context != null && targetPref != null) GoogleDriveBackupService(context, targetPref) else null
        val account = _backupUiState.value.account

        viewModelScope.launch {
            _backupUiState.update { it.copy(isDeletingCloud = true, errorMessage = null) }
            try {
                targetService?.deleteCloudBackup(account?.email)
                _backupUiState.update {
                    it.copy(
                        isDeletingCloud = false,
                        cloudBackupExists = false,
                        cloudBackupFileId = null,
                        lastStatusMessage = "Google Drive ব্যাকআপ মুছে ফেলা হয়েছে",
                        errorMessage = null
                    )
                }
                onResult(true, "Google Drive ব্যাকআপ মুছে ফেলা হয়েছে")
            } catch (_: Exception) {
                _backupUiState.update {
                    it.copy(
                        isDeletingCloud = false,
                        errorMessage = "ক্লাউড ব্যাকআপ মুছে ফেলা যায়নি।"
                    )
                }
                onResult(false, "ক্লাউড ব্যাকআপ মুছে ফেলা যায়নি।")
            }
        }
    }
}
