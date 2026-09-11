package com.example.ui

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.SavingsGoalEntity
import com.example.data.entity.TransactionEntity
import com.example.model.AppLanguage
import com.example.model.ThemeMode
import com.example.model.TransactionType
import com.example.ui.components.ConfirmationDialog
import com.example.ui.screens.AddContributionSheet
import com.example.ui.screens.AddEditTransactionSheet
import com.example.ui.screens.BackupRestoreScreen
import com.example.ui.screens.BudgetBottomSheet
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.CreateSavingsGoalSheet
import com.example.ui.screens.DebtManagerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SavingsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.HisabBoiTheme
import com.example.viewmodel.HisabBoiViewModel
import kotlinx.coroutines.flow.collectLatest

enum class AppTab(val icon: ImageVector) {
    HOME(Icons.Default.Home),
    TRANSACTIONS(Icons.Default.ReceiptLong),
    REPORTS(Icons.Default.BarChart),
    SAVINGS(Icons.Default.Spa),
    SETTINGS(Icons.Default.Settings)
}

@Composable
fun MainApp(
    viewModel: HisabBoiViewModel
) {
    val preferences by viewModel.preferences.collectAsState()
    val themeMode by viewModel.currentThemeMode.collectAsState()
    val isSystemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemDark
    }

    HisabBoiTheme(darkTheme = darkTheme) {
        var isSplashActive by remember { mutableStateOf(true) }
        val userProfile by viewModel.userProfile.collectAsState()

        if (isSplashActive) {
            val strings by viewModel.strings.collectAsState()
            SplashScreen(
                strings = strings,
                onNavigateNext = { isSplashActive = false }
            )
        } else if (!preferences.onboardingCompleted) {
            val strings by viewModel.strings.collectAsState()
            OnboardingScreen(
                strings = strings,
                onComplete = { viewModel.completeOnboarding() }
            )
        } else if (!userProfile.isSetupCompleted) {
            ProfileSetupScreen(
                viewModel = viewModel,
                onSetupComplete = {
                    // Profile saved, automatically advances to MainAppContent
                }
            )
        } else {
            MainAppContent(viewModel = viewModel)
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: HisabBoiViewModel
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsState()
    val strings by viewModel.strings.collectAsState()
    val budgetState by viewModel.budgetState.collectAsState()
    val year by viewModel.selectedYear.collectAsState()
    val monthIndex by viewModel.selectedMonthIndex.collectAsState()
    val customCategories by viewModel.customCategories.collectAsState()

    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog & Sheet States
    var showDebtManager by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }
    var showBackupRestore by remember { mutableStateOf(false) }
    var prefilledTransactionDate by remember { mutableStateOf<Long?>(null) }
    var showAddEditSheet by remember { mutableStateOf(false) }
    var activeTransactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

    var showBudgetSheet by remember { mutableStateOf(false) }

    var showCreateSavingsGoalSheet by remember { mutableStateOf(false) }
    var contributingGoal by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    val isHomeRoot = currentTab == AppTab.HOME &&
        !showDebtManager &&
        !showCalendar &&
        !showBackupRestore &&
        !showAddEditSheet &&
        !showBudgetSheet &&
        !showCreateSavingsGoalSheet &&
        contributingGoal == null

    BackHandler(enabled = isHomeRoot || showExitConfirmationDialog || showDebtManager || showCalendar || showBackupRestore || showAddEditSheet || showBudgetSheet || showCreateSavingsGoalSheet || contributingGoal != null) {
        when {
            showExitConfirmationDialog -> showExitConfirmationDialog = false
            showAddEditSheet -> {
                showAddEditSheet = false
                editingTransaction = null
                prefilledTransactionDate = null
            }
            showBudgetSheet -> showBudgetSheet = false
            showCreateSavingsGoalSheet -> showCreateSavingsGoalSheet = false
            contributingGoal != null -> contributingGoal = null
            showDebtManager -> showDebtManager = false
            showCalendar -> showCalendar = false
            showBackupRestore -> showBackupRestore = false
            currentTab != AppTab.HOME -> currentTab = AppTab.HOME
            else -> showExitConfirmationDialog = true
        }
    }

    // Collect transient user messages
    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    if (showDebtManager) {
        DebtManagerScreen(
            viewModel = viewModel,
            onNavigateBack = { showDebtManager = false }
        )
    } else if (showCalendar) {
        CalendarScreen(
            viewModel = viewModel,
            onNavigateBack = { showCalendar = false },
            onOpenAddTransaction = { dateMillis, type ->
                activeTransactionType = type
                editingTransaction = null
                prefilledTransactionDate = dateMillis
                showAddEditSheet = true
            },
            onOpenEditTransaction = { tx ->
                editingTransaction = tx
                prefilledTransactionDate = null
                showAddEditSheet = true
            }
        )
    } else if (showBackupRestore) {
        BackupRestoreScreen(
            viewModel = viewModel,
            onNavigateBack = { showBackupRestore = false }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_app_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.testTag("main_bottom_nav_bar")
            ) {
                // 1. Home
                NavigationBarItem(
                    selected = currentTab == AppTab.HOME,
                    onClick = { currentTab = AppTab.HOME },
                    icon = {
                        Icon(
                            imageVector = AppTab.HOME.icon,
                            contentDescription = strings.navHome,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = strings.navHome,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (currentTab == AppTab.HOME) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // 2. Transactions
                NavigationBarItem(
                    selected = currentTab == AppTab.TRANSACTIONS,
                    onClick = { currentTab = AppTab.TRANSACTIONS },
                    icon = {
                        Icon(
                            imageVector = AppTab.TRANSACTIONS.icon,
                            contentDescription = strings.navTransactions,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = strings.navTransactions,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (currentTab == AppTab.TRANSACTIONS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_item_transactions")
                )

                // 3. Reports
                NavigationBarItem(
                    selected = currentTab == AppTab.REPORTS,
                    onClick = { currentTab = AppTab.REPORTS },
                    icon = {
                        Icon(
                            imageVector = AppTab.REPORTS.icon,
                            contentDescription = strings.navReports,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = strings.navReports,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (currentTab == AppTab.REPORTS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_item_reports")
                )

                // 4. Savings
                val savingsLabel = if (language == AppLanguage.BN) "সঞ্চয়" else "Savings"
                NavigationBarItem(
                    selected = currentTab == AppTab.SAVINGS,
                    onClick = { currentTab = AppTab.SAVINGS },
                    icon = {
                        Icon(
                            imageVector = AppTab.SAVINGS.icon,
                            contentDescription = savingsLabel,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = savingsLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (currentTab == AppTab.SAVINGS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_item_savings")
                )

                // 5. Settings
                NavigationBarItem(
                    selected = currentTab == AppTab.SETTINGS,
                    onClick = { currentTab = AppTab.SETTINGS },
                    icon = {
                        Icon(
                            imageVector = AppTab.SETTINGS.icon,
                            contentDescription = strings.navSettings,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = strings.navSettings,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (currentTab == AppTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onOpenAddTransaction = { type ->
                        activeTransactionType = type
                        editingTransaction = null
                        prefilledTransactionDate = null
                        showAddEditSheet = true
                    },
                    onOpenEditTransaction = { tx ->
                        editingTransaction = tx
                        prefilledTransactionDate = null
                        showAddEditSheet = true
                    },
                    onOpenSetBudget = { showBudgetSheet = true },
                    onOpenSavings = { currentTab = AppTab.SAVINGS },
                    onOpenAllTransactions = { currentTab = AppTab.TRANSACTIONS },
                    onOpenDebtManager = { showDebtManager = true },
                    onOpenCalendar = { showCalendar = true },
                    onOpenBackupRestore = { showBackupRestore = true }
                )

                AppTab.TRANSACTIONS -> TransactionsScreen(
                    viewModel = viewModel,
                    onOpenEditTransaction = { tx ->
                        editingTransaction = tx
                        prefilledTransactionDate = null
                        showAddEditSheet = true
                    },
                    onOpenCalendar = { showCalendar = true }
                )

                AppTab.REPORTS -> ReportsScreen(viewModel = viewModel)

                AppTab.SAVINGS -> SavingsScreen(
                    viewModel = viewModel,
                    onOpenCreateGoal = { showCreateSavingsGoalSheet = true },
                    onOpenAddContribution = { goal -> contributingGoal = goal }
                )

                AppTab.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onOpenBackupRestore = { showBackupRestore = true }
                )
            }
        }
    }
    }

    // Add / Edit Transaction Sheet
    if (showAddEditSheet) {
        AddEditTransactionSheet(
            initialType = activeTransactionType,
            existingTransaction = editingTransaction,
            customCategories = customCategories,
            language = language,
            strings = strings,
            initialDateMillis = prefilledTransactionDate,
            onDismiss = {
                showAddEditSheet = false
                editingTransaction = null
                prefilledTransactionDate = null
            },
            onCreateCustomCategory = { nameBn, nameEn, type, iconName, colorHex, onCreated ->
                viewModel.createCustomCategory(nameBn, nameEn, type, iconName, colorHex, onCreated)
            },
            onSave = { type, amount, categoryKey, dateMillis, note ->
                prefilledTransactionDate = null
                if (editingTransaction != null) {
                    viewModel.updateTransaction(
                        id = editingTransaction!!.id,
                        type = type,
                        amount = amount,
                        categoryKey = categoryKey,
                        dateMillis = dateMillis,
                        note = note
                    )
                } else {
                    viewModel.addTransaction(
                        type = type,
                        amount = amount,
                        categoryKey = categoryKey,
                        dateMillis = dateMillis,
                        note = note
                    )
                }
                editingTransaction = null
            }
        )
    }

    // Monthly Budget Sheet
    if (showBudgetSheet) {
        BudgetBottomSheet(
            currentBudgetAmount = budgetState.budgetAmount,
            hasBudget = budgetState.budget != null,
            year = year,
            monthIndex = monthIndex,
            language = language,
            strings = strings,
            onDismiss = { showBudgetSheet = false },
            onSaveBudget = { amount -> viewModel.setBudget(amount) },
            onDeleteBudget = { viewModel.deleteBudget() }
        )
    }

    // Create Savings Goal Sheet
    if (showCreateSavingsGoalSheet) {
        CreateSavingsGoalSheet(
            language = language,
            strings = strings,
            onDismiss = { showCreateSavingsGoalSheet = false },
            onCreateGoal = { name, target, initial ->
                viewModel.createSavingsGoal(name, target, initial)
            }
        )
    }

    // Add Contribution to Savings Goal Sheet
    if (contributingGoal != null) {
        AddContributionSheet(
            goalName = contributingGoal!!.name,
            language = language,
            strings = strings,
            onDismiss = { contributingGoal = null },
            onAddSavings = { amount ->
                viewModel.addSavings(contributingGoal!!.id, amount)
            }
        )
    }

    // Exit Confirmation Dialog for Home / Root Screen
    if (showExitConfirmationDialog) {
        ConfirmationDialog(
            title = "অ্যাপ থেকে বের হতে চান?",
            message = "আপনি কি HisabBoi থেকে বের হয়ে যেতে চান?",
            confirmButtonText = "বের হয়ে যান",
            dismissButtonText = "বাতিল",
            onConfirm = {
                showExitConfirmationDialog = false
                var ctx = context
                while (ctx is ContextWrapper) {
                    if (ctx is Activity) break
                    ctx = ctx.baseContext
                }
                (ctx as? Activity)?.finish()
            },
            onDismiss = {
                showExitConfirmationDialog = false
            },
            isDestructive = false
        )
    }
}
