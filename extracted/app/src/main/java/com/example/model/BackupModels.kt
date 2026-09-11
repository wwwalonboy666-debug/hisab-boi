package com.example.model

import com.example.data.entity.AppPreferenceEntity
import com.example.data.entity.BudgetEntity
import com.example.data.entity.CustomCategoryEntity
import com.example.data.entity.DebtEntity
import com.example.data.entity.DebtPaymentEntity
import com.example.data.entity.SavingsGoalEntity
import com.example.data.entity.TransactionEntity

enum class AutoBackupFrequency {
    OFF,
    DAILY
}

data class GoogleAccountInfo(
    val email: String,
    val displayName: String = "",
    val photoUrl: String? = null,
    val isConnected: Boolean = true
)

data class BackupMetadata(
    val backupVersion: Int = 4,
    val appVersion: String = "4.0",
    val databaseVersion: Int = 4,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceModel: String = "",
    val transactionsCount: Int = 0,
    val customCategoriesCount: Int = 0,
    val budgetsCount: Int = 0,
    val savingsGoalsCount: Int = 0,
    val debtsCount: Int = 0,
    val debtPaymentsCount: Int = 0,
    val checksum: String = ""
)

data class BackupPackage(
    val metadata: BackupMetadata,
    val transactions: List<TransactionEntity> = emptyList(),
    val customCategories: List<CustomCategoryEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList(),
    val savingsGoals: List<SavingsGoalEntity> = emptyList(),
    val debts: List<DebtEntity> = emptyList(),
    val debtPayments: List<DebtPaymentEntity> = emptyList(),
    val appPreference: AppPreferenceEntity? = null,
    val userProfileName: String? = null
)

data class BackupUiState(
    val account: GoogleAccountInfo? = null,
    val lastBackupTime: Long? = null,
    val autoBackupFrequency: AutoBackupFrequency = AutoBackupFrequency.OFF,
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val isDeletingCloud: Boolean = false,
    val cloudBackupFileId: String? = null,
    val cloudBackupExists: Boolean = false,
    val cloudBackupSize: Long? = null,
    val cloudBackupModifiedTime: Long? = null,
    val lastStatusMessage: String? = null,
    val errorMessage: String? = null
)
