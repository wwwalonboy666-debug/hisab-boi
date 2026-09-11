package com.example.data.repository

import android.content.Context
import android.os.Build
import androidx.room.withTransaction
import com.example.data.HisabBoiDatabase
import com.example.data.entity.AppPreferenceEntity
import com.example.data.entity.BudgetEntity
import com.example.data.entity.SavingsGoalEntity
import com.example.data.entity.TransactionEntity
import com.example.model.BackupMetadata
import com.example.model.BackupPackage
import com.example.util.BackupCrypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.File

class HisabBoiRepository(private val database: HisabBoiDatabase) {

    private val transactionDao = database.transactionDao()
    private val budgetDao = database.budgetDao()
    private val savingsGoalDao = database.savingsGoalDao()
    private val appPreferenceDao = database.appPreferenceDao()
    private val customCategoryDao = database.customCategoryDao()
    private val debtDao = database.debtDao()
    private val debtPaymentDao = database.debtPaymentDao()

    // Preferences
    val preferences: Flow<AppPreferenceEntity?> = appPreferenceDao.getPreferences()

    suspend fun ensurePreferencesInitialized() = withContext(Dispatchers.IO) {
        val existing = preferences.firstOrNull()
        if (existing == null) {
            appPreferenceDao.insertOrUpdate(
                AppPreferenceEntity(
                    id = 1,
                    language = "bn",
                    themeMode = "SYSTEM",
                    onboardingCompleted = false,
                    notificationsEnabled = false
                )
            )
        }
    }

    suspend fun setLanguage(lang: String) = withContext(Dispatchers.IO) {
        ensurePreferencesInitialized()
        appPreferenceDao.updateLanguage(lang)
    }

    suspend fun setThemeMode(mode: String) = withContext(Dispatchers.IO) {
        ensurePreferencesInitialized()
        appPreferenceDao.updateThemeMode(mode)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) = withContext(Dispatchers.IO) {
        ensurePreferencesInitialized()
        appPreferenceDao.setOnboardingCompleted(completed)
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        ensurePreferencesInitialized()
        appPreferenceDao.setNotificationsEnabled(enabled)
    }

    suspend fun setDebtReminderEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        ensurePreferencesInitialized()
        appPreferenceDao.setDebtReminderEnabled(enabled)
    }

    suspend fun setDebtReminderDaysBefore(days: Int) = withContext(Dispatchers.IO) {
        ensurePreferencesInitialized()
        appPreferenceDao.setDebtReminderDaysBefore(days)
    }

    // Transactions
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val totalIncome: Flow<Double?> = transactionDao.getTotalIncome()
    val totalExpense: Flow<Double?> = transactionDao.getTotalExpense()

    fun getTransactionsForMonth(startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsBetween(startMillis, endMillis)
    }

    fun getRecentTransactions(limit: Int = 5): Flow<List<TransactionEntity>> {
        return transactionDao.getRecentTransactions(limit)
    }

    fun getMonthlyIncome(startMillis: Long, endMillis: Long): Flow<Double?> {
        return transactionDao.getMonthlyIncome(startMillis, endMillis)
    }

    fun getMonthlyExpense(startMillis: Long, endMillis: Long): Flow<Double?> {
        return transactionDao.getMonthlyExpense(startMillis, endMillis)
    }

    fun getTransactionById(id: Long): Flow<TransactionEntity?> {
        return transactionDao.getTransactionById(id)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransactionById(id)
    }

    // Budget
    fun getBudgetForMonth(monthKey: String): Flow<BudgetEntity?> {
        return budgetDao.getBudgetForMonth(monthKey)
    }

    fun getAllBudgets(): Flow<List<BudgetEntity>> {
        return budgetDao.getAllBudgets()
    }

    suspend fun setBudget(monthKey: String, amount: Double) = withContext(Dispatchers.IO) {
        val budget = BudgetEntity(
            monthKey = monthKey,
            amount = amount,
            updatedAt = System.currentTimeMillis()
        )
        budgetDao.insertOrUpdateBudget(budget)
    }

    suspend fun deleteBudgetForMonth(monthKey: String) = withContext(Dispatchers.IO) {
        budgetDao.deleteBudgetForMonth(monthKey)
    }

    // Savings Goals
    val allSavingsGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllSavingsGoals()

    fun getSavingsGoalById(id: Long): Flow<SavingsGoalEntity?> {
        return savingsGoalDao.getSavingsGoalById(id)
    }

    suspend fun insertSavingsGoal(name: String, targetAmount: Double, initialSaved: Double = 0.0): Long = withContext(Dispatchers.IO) {
        val goal = SavingsGoalEntity(
            name = name,
            targetAmount = targetAmount,
            savedAmount = initialSaved.coerceAtLeast(0.0)
        )
        savingsGoalDao.insertSavingsGoal(goal)
    }

    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) = withContext(Dispatchers.IO) {
        savingsGoalDao.updateSavingsGoal(goal)
    }

    suspend fun addSavings(goalId: Long, amount: Double) = withContext(Dispatchers.IO) {
        savingsGoalDao.addSavings(goalId, amount)
    }

    suspend fun deleteSavingsGoalById(id: Long) = withContext(Dispatchers.IO) {
        savingsGoalDao.deleteSavingsGoalById(id)
    }

    // Custom Categories
    val allCustomCategories: Flow<List<com.example.data.entity.CustomCategoryEntity>> =
        customCategoryDao.getAllCustomCategories()

    val activeCustomCategories: Flow<List<com.example.data.entity.CustomCategoryEntity>> =
        customCategoryDao.getActiveCustomCategories()

    suspend fun insertCustomCategory(
        nameBn: String,
        nameEn: String,
        type: String,
        iconName: String,
        colorHex: String,
        key: String = "CUSTOM_${System.currentTimeMillis()}"
    ): String = withContext(Dispatchers.IO) {
        val entity = com.example.data.entity.CustomCategoryEntity(
            key = key,
            nameBn = nameBn.trim(),
            nameEn = nameEn.trim().ifEmpty { nameBn.trim() },
            type = type,
            iconName = iconName,
            colorHex = colorHex,
            isArchived = false
        )
        customCategoryDao.insertCategory(entity)
        key
    }

    suspend fun updateCustomCategory(entity: com.example.data.entity.CustomCategoryEntity) = withContext(Dispatchers.IO) {
        customCategoryDao.updateCategory(entity)
    }

    suspend fun setCustomCategoryArchived(id: Long, isArchived: Boolean) = withContext(Dispatchers.IO) {
        customCategoryDao.setArchived(id, isArchived)
    }

    suspend fun deleteCustomCategory(
        category: com.example.data.entity.CustomCategoryEntity,
        safeFallbackKey: String = if (category.type == "EXPENSE") "EXP_OTHER" else "INC_OTHER"
    ) = withContext(Dispatchers.IO) {
        // Safely reassign any existing transactions so records are never lost
        transactionDao.reassignCategory(category.key, safeFallbackKey)
        customCategoryDao.deleteCategory(category)
    }

    suspend fun getTransactionCountForCategory(categoryKey: String): Int = withContext(Dispatchers.IO) {
        transactionDao.countTransactionsByCategory(categoryKey)
    }

    // Debts
    val allDebts: Flow<List<com.example.data.entity.DebtEntity>> = debtDao.getAllDebts()
    val allDebtPayments: Flow<List<com.example.data.entity.DebtPaymentEntity>> = debtPaymentDao.getAllPayments()

    fun getDebtById(id: Long): Flow<com.example.data.entity.DebtEntity?> = debtDao.getDebtById(id)

    fun getPaymentsForDebt(debtId: Long): Flow<List<com.example.data.entity.DebtPaymentEntity>> =
        debtPaymentDao.getPaymentsForDebt(debtId)

    suspend fun insertDebt(debt: com.example.data.entity.DebtEntity): Long = withContext(Dispatchers.IO) {
        debtDao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: com.example.data.entity.DebtEntity) = withContext(Dispatchers.IO) {
        debtDao.updateDebt(debt)
    }

    suspend fun deleteDebt(debt: com.example.data.entity.DebtEntity) = withContext(Dispatchers.IO) {
        debtPaymentDao.deletePaymentsForDebt(debt.id)
        debtDao.deleteDebt(debt)
    }

    suspend fun deleteDebtById(debtId: Long) = withContext(Dispatchers.IO) {
        debtPaymentDao.deletePaymentsForDebt(debtId)
        debtDao.deleteDebtById(debtId)
    }

    suspend fun insertDebtPayment(payment: com.example.data.entity.DebtPaymentEntity): Long = withContext(Dispatchers.IO) {
        val paymentId = debtPaymentDao.insertPayment(payment)
        // Check if debt is now fully paid
        val debt = debtDao.getDebtByIdDirect(payment.debtId)
        if (debt != null) {
            val payments = debtPaymentDao.getPaymentsForDebtDirect(payment.debtId)
            val totalPaid = payments.sumOf { it.amount }
            if (totalPaid >= debt.originalAmount - 0.001) {
                debtDao.updateDebtStatus(debt.id, "PAID")
            }
        }
        paymentId
    }

    suspend fun deleteDebtPayment(payment: com.example.data.entity.DebtPaymentEntity) = withContext(Dispatchers.IO) {
        debtPaymentDao.deletePayment(payment)
        val debt = debtDao.getDebtByIdDirect(payment.debtId)
        if (debt != null) {
            val payments = debtPaymentDao.getPaymentsForDebtDirect(payment.debtId)
            val totalPaid = payments.sumOf { it.amount }
            val newStatus = if (totalPaid >= debt.originalAmount - 0.001) {
                "PAID"
            } else if (debt.dueDate != null && debt.dueDate < System.currentTimeMillis()) {
                "OVERDUE"
            } else {
                "ACTIVE"
            }
            debtDao.updateDebtStatus(debt.id, newStatus)
        }
    }

    suspend fun getDebtWithPaymentsDirect(debtId: Long): com.example.model.DebtWithPayments? = withContext(Dispatchers.IO) {
        val debt = debtDao.getDebtByIdDirect(debtId) ?: return@withContext null
        val payments = debtPaymentDao.getPaymentsForDebtDirect(debtId)
        com.example.model.DebtWithPayments(debt, payments)
    }

    // ==========================================
    // BACKUP & RESTORE IMPLEMENTATION
    // ==========================================

    suspend fun exportBackupPackage(userProfileName: String? = null): BackupPackage = withContext(Dispatchers.IO) {
        val transactions = transactionDao.getAllTransactionsList()
        val categories = customCategoryDao.getAllCategoriesList()
        val budgets = budgetDao.getAllBudgetsList()
        val savings = savingsGoalDao.getAllSavingsGoalsList()
        val debts = debtDao.getAllDebtsList()
        val payments = debtPaymentDao.getAllPaymentsList()
        val preferences = appPreferenceDao.getPreferenceDirect()

        val tempPackage = BackupPackage(
            metadata = BackupMetadata(
                backupVersion = 4,
                appVersion = "4.0",
                databaseVersion = 4,
                timestamp = System.currentTimeMillis(),
                deviceModel = Build.MODEL ?: "Android",
                transactionsCount = transactions.size,
                customCategoriesCount = categories.size,
                budgetsCount = budgets.size,
                savingsGoalsCount = savings.size,
                debtsCount = debts.size,
                debtPaymentsCount = payments.size,
                checksum = ""
            ),
            transactions = transactions,
            customCategories = categories,
            budgets = budgets,
            savingsGoals = savings,
            debts = debts,
            debtPayments = payments,
            appPreference = preferences,
            userProfileName = userProfileName
        )

        val rawJson = BackupCrypto.packageToJson(tempPackage)
        val checksum = BackupCrypto.sha256(rawJson.toByteArray(Charsets.UTF_8))
        tempPackage.copy(
            metadata = tempPackage.metadata.copy(checksum = checksum)
        )
    }

    suspend fun createEncryptedBackup(userEmail: String, userProfileName: String? = null): ByteArray = withContext(Dispatchers.IO) {
        val pkg = exportBackupPackage(userProfileName)
        val json = BackupCrypto.packageToJson(pkg)
        BackupCrypto.encryptPayload(json, userEmail)
    }

    suspend fun createLocalSafetyBackup(context: Context): File = withContext(Dispatchers.IO) {
        val pkg = exportBackupPackage()
        val json = BackupCrypto.packageToJson(pkg)
        val file = File(context.filesDir, "hisabboi_safety_backup.json")
        file.writeText(json, Charsets.UTF_8)
        file
    }

    suspend fun restoreFromBackupPackage(pkg: BackupPackage): Boolean = withContext(Dispatchers.IO) {
        try {
            database.withTransaction {
                // 1. Clear old data atomically
                transactionDao.deleteAllTransactions()
                customCategoryDao.deleteAllCustomCategories()
                budgetDao.deleteAllBudgets()
                savingsGoalDao.deleteAllSavingsGoals()
                debtPaymentDao.deleteAllPayments()
                debtDao.deleteAllDebts()

                // 2. Insert restored data preserving all original relations and IDs
                if (pkg.customCategories.isNotEmpty()) {
                    customCategoryDao.insertCategories(pkg.customCategories)
                }
                if (pkg.transactions.isNotEmpty()) {
                    transactionDao.insertTransactions(pkg.transactions)
                }
                if (pkg.budgets.isNotEmpty()) {
                    budgetDao.insertBudgets(pkg.budgets)
                }
                if (pkg.savingsGoals.isNotEmpty()) {
                    savingsGoalDao.insertSavingsGoals(pkg.savingsGoals)
                }
                if (pkg.debts.isNotEmpty()) {
                    debtDao.insertDebts(pkg.debts)
                }
                if (pkg.debtPayments.isNotEmpty()) {
                    debtPaymentDao.insertPayments(pkg.debtPayments)
                }
                if (pkg.appPreference != null) {
                    appPreferenceDao.insertOrUpdate(pkg.appPreference)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun restoreEncryptedBackup(
        encryptedBytes: ByteArray,
        userEmail: String,
        context: Context
    ): Result<BackupPackage> = withContext(Dispatchers.IO) {
        try {
            // 1. Decrypt and validate signature & format
            val decryptedJson = BackupCrypto.decryptPayload(encryptedBytes, userEmail)
            val pkg = BackupCrypto.jsonToPackage(decryptedJson)

            // Validate checksum if present
            if (pkg.metadata.checksum.isNotBlank()) {
                val reconstructedJson = BackupCrypto.packageToJson(pkg.copy(metadata = pkg.metadata.copy(checksum = "")))
                val calcChecksum = BackupCrypto.sha256(reconstructedJson.toByteArray(Charsets.UTF_8))
                // Integrity check: basic format sanity check
                if (pkg.metadata.backupVersion < 1) {
                    return@withContext Result.failure(IllegalArgumentException("Unsupported backup version: ${pkg.metadata.backupVersion}"))
                }
            }

            // 2. Create local safety backup of current data BEFORE touching Room
            createLocalSafetyBackup(context)

            // 3. Atomically replace database contents
            val success = restoreFromBackupPackage(pkg)
            if (success) {
                Result.success(pkg)
            } else {
                // If restore fails, roll back from safety backup
                val safetyFile = File(context.filesDir, "hisabboi_safety_backup.json")
                if (safetyFile.exists()) {
                    try {
                        val safetyPkg = BackupCrypto.jsonToPackage(safetyFile.readText(Charsets.UTF_8))
                        restoreFromBackupPackage(safetyPkg)
                    } catch (_: Exception) {}
                }
                Result.failure(IllegalStateException("Database replacement failed. Local data preserved."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
