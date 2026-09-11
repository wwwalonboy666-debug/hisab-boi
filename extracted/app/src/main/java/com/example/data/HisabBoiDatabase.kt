package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AppPreferenceDao
import com.example.data.dao.BudgetDao
import com.example.data.dao.CustomCategoryDao
import com.example.data.dao.DebtDao
import com.example.data.dao.DebtPaymentDao
import com.example.data.dao.SavingsGoalDao
import com.example.data.dao.TransactionDao
import com.example.data.entity.AppPreferenceEntity
import com.example.data.entity.BudgetEntity
import com.example.data.entity.CustomCategoryEntity
import com.example.data.entity.DebtEntity
import com.example.data.entity.DebtPaymentEntity
import com.example.data.entity.SavingsGoalEntity
import com.example.data.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        AppPreferenceEntity::class,
        CustomCategoryEntity::class,
        DebtEntity::class,
        DebtPaymentEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class HisabBoiDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun appPreferenceDao(): AppPreferenceDao
    abstract fun customCategoryDao(): CustomCategoryDao
    abstract fun debtDao(): DebtDao
    abstract fun debtPaymentDao(): DebtPaymentDao

    companion object {
        @Volatile
        private var INSTANCE: HisabBoiDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `custom_categories` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `key` TEXT NOT NULL,
                        `nameBn` TEXT NOT NULL,
                        `nameEn` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `iconName` TEXT NOT NULL,
                        `colorHex` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `debts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `personName` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `originalAmount` REAL NOT NULL,
                        `createdDate` INTEGER NOT NULL,
                        `dueDate` INTEGER,
                        `note` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debts_personName` ON `debts` (`personName`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debts_type` ON `debts` (`type`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debts_status` ON `debts` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debts_dueDate` ON `debts` (`dueDate`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `debt_payments` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `debtId` INTEGER NOT NULL,
                        `amount` REAL NOT NULL,
                        `paymentDate` INTEGER NOT NULL,
                        `note` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debt_payments_debtId` ON `debt_payments` (`debtId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debt_payments_paymentDate` ON `debt_payments` (`paymentDate`)")

                db.execSQL("ALTER TABLE `app_preferences` ADD COLUMN `debtReminderEnabled` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE `app_preferences` ADD COLUMN `debtReminderDaysBefore` INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `custom_categories` ADD COLUMN `isArchived` INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): HisabBoiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HisabBoiDatabase::class.java,
                    "hisabboi_database.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
