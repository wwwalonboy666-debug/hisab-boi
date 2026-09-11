package com.example.util

import com.example.data.entity.AppPreferenceEntity
import com.example.data.entity.BudgetEntity
import com.example.data.entity.CustomCategoryEntity
import com.example.data.entity.DebtEntity
import com.example.data.entity.DebtPaymentEntity
import com.example.data.entity.SavingsGoalEntity
import com.example.data.entity.TransactionEntity
import com.example.model.BackupMetadata
import com.example.model.BackupPackage
import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCrypto {

    private const val MAGIC_HEADER = "HBK4" // HisabBoi Backup format v4
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256
    private const val GCM_TAG_LENGTH = 128
    private const val IV_LENGTH = 12
    private const val SALT_LENGTH = 16

    fun sha256(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun packageToJson(backup: BackupPackage): String {
        val root = JSONObject()

        // Metadata
        val metaObj = JSONObject().apply {
            put("backupVersion", backup.metadata.backupVersion)
            put("appVersion", backup.metadata.appVersion)
            put("databaseVersion", backup.metadata.databaseVersion)
            put("timestamp", backup.metadata.timestamp)
            put("deviceModel", backup.metadata.deviceModel)
            put("transactionsCount", backup.transactions.size)
            put("customCategoriesCount", backup.customCategories.size)
            put("budgetsCount", backup.budgets.size)
            put("savingsGoalsCount", backup.savingsGoals.size)
            put("debtsCount", backup.debts.size)
            put("debtPaymentsCount", backup.debtPayments.size)
            put("checksum", backup.metadata.checksum)
        }
        root.put("metadata", metaObj)

        // Transactions
        val txArray = JSONArray()
        backup.transactions.forEach { tx ->
            val obj = JSONObject().apply {
                put("id", tx.id)
                put("amount", tx.amount)
                put("type", tx.type)
                put("categoryKey", tx.categoryKey)
                put("dateMillis", tx.dateMillis)
                put("note", tx.note)
                put("createdAt", tx.createdAt)
                put("updatedAt", tx.updatedAt)
            }
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        // Custom Categories
        val catArray = JSONArray()
        backup.customCategories.forEach { cat ->
            val obj = JSONObject().apply {
                put("id", cat.id)
                put("key", cat.key)
                put("nameBn", cat.nameBn)
                put("nameEn", cat.nameEn)
                put("type", cat.type)
                put("iconName", cat.iconName)
                put("colorHex", cat.colorHex)
                put("createdAt", cat.createdAt)
                put("isArchived", cat.isArchived)
            }
            catArray.put(obj)
        }
        root.put("customCategories", catArray)

        // Budgets
        val budgetArray = JSONArray()
        backup.budgets.forEach { b ->
            val obj = JSONObject().apply {
                put("id", b.id)
                put("monthKey", b.monthKey)
                put("amount", b.amount)
                put("createdAt", b.createdAt)
            }
            budgetArray.put(obj)
        }
        root.put("budgets", budgetArray)

        // Savings Goals
        val savingsArray = JSONArray()
        backup.savingsGoals.forEach { s ->
            val obj = JSONObject().apply {
                put("id", s.id)
                put("name", s.name)
                put("targetAmount", s.targetAmount)
                put("savedAmount", s.savedAmount)
                put("createdAt", s.createdAt)
                put("updatedAt", s.updatedAt)
            }
            savingsArray.put(obj)
        }
        root.put("savingsGoals", savingsArray)

        // Debts
        val debtsArray = JSONArray()
        backup.debts.forEach { d ->
            val obj = JSONObject().apply {
                put("id", d.id)
                put("personName", d.personName)
                put("type", d.type)
                put("originalAmount", d.originalAmount)
                put("createdDate", d.createdDate)
                if (d.dueDate != null) {
                    put("dueDate", d.dueDate)
                } else {
                    put("dueDate", JSONObject.NULL)
                }
                put("note", d.note)
                put("status", d.status)
                put("createdAt", d.createdAt)
                put("updatedAt", d.updatedAt)
            }
            debtsArray.put(obj)
        }
        root.put("debts", debtsArray)

        // Debt Payments
        val paymentsArray = JSONArray()
        backup.debtPayments.forEach { p ->
            val obj = JSONObject().apply {
                put("id", p.id)
                put("debtId", p.debtId)
                put("amount", p.amount)
                put("paymentDate", p.paymentDate)
                put("note", p.note)
                put("createdAt", p.createdAt)
            }
            paymentsArray.put(obj)
        }
        root.put("debtPayments", paymentsArray)

        // App Preferences
        backup.appPreference?.let { pref ->
            val prefObj = JSONObject().apply {
                put("language", pref.language)
                put("themeMode", pref.themeMode)
                put("notificationsEnabled", pref.notificationsEnabled)
                put("onboardingCompleted", pref.onboardingCompleted)
                put("debtReminderEnabled", pref.debtReminderEnabled)
                put("debtReminderDaysBefore", pref.debtReminderDaysBefore)
            }
            root.put("appPreference", prefObj)
        }

        // Profile Name
        backup.userProfileName?.let { name ->
            root.put("userProfileName", name)
        }

        return root.toString()
    }

    fun jsonToPackage(jsonString: String): BackupPackage {
        val root = JSONObject(jsonString)
        val metaObj = root.getJSONObject("metadata")
        val metadata = BackupMetadata(
            backupVersion = metaObj.optInt("backupVersion", 4),
            appVersion = metaObj.optString("appVersion", "4.0"),
            databaseVersion = metaObj.optInt("databaseVersion", 4),
            timestamp = metaObj.optLong("timestamp", System.currentTimeMillis()),
            deviceModel = metaObj.optString("deviceModel", ""),
            transactionsCount = metaObj.optInt("transactionsCount", 0),
            customCategoriesCount = metaObj.optInt("customCategoriesCount", 0),
            budgetsCount = metaObj.optInt("budgetsCount", 0),
            savingsGoalsCount = metaObj.optInt("savingsGoalsCount", 0),
            debtsCount = metaObj.optInt("debtsCount", 0),
            debtPaymentsCount = metaObj.optInt("debtPaymentsCount", 0),
            checksum = metaObj.optString("checksum", "")
        )

        // Transactions
        val transactions = mutableListOf<TransactionEntity>()
        val txArray = root.optJSONArray("transactions")
        if (txArray != null) {
            for (i in 0 until txArray.length()) {
                val o = txArray.getJSONObject(i)
                transactions.add(
                    TransactionEntity(
                        id = o.optLong("id", 0L),
                        amount = o.optDouble("amount", 0.0),
                        type = o.optString("type", "EXPENSE"),
                        categoryKey = o.optString("categoryKey", ""),
                        dateMillis = o.optLong("dateMillis", 0L),
                        note = o.optString("note", ""),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }
        }

        // Categories
        val customCategories = mutableListOf<CustomCategoryEntity>()
        val catArray = root.optJSONArray("customCategories")
        if (catArray != null) {
            for (i in 0 until catArray.length()) {
                val o = catArray.getJSONObject(i)
                customCategories.add(
                    CustomCategoryEntity(
                        id = o.optLong("id", 0L),
                        key = o.optString("key", ""),
                        nameBn = o.optString("nameBn", ""),
                        nameEn = o.optString("nameEn", ""),
                        type = o.optString("type", "EXPENSE"),
                        iconName = o.optString("iconName", "category"),
                        colorHex = o.optString("colorHex", "#2E7D32"),
                        createdAt = o.optLong("createdAt", 0L),
                        isArchived = o.optBoolean("isArchived", false)
                    )
                )
            }
        }

        // Budgets
        val budgets = mutableListOf<BudgetEntity>()
        val budgetArray = root.optJSONArray("budgets")
        if (budgetArray != null) {
            for (i in 0 until budgetArray.length()) {
                val o = budgetArray.getJSONObject(i)
                budgets.add(
                    BudgetEntity(
                        id = o.optLong("id", 0L),
                        monthKey = o.optString("monthKey", ""),
                        amount = o.optDouble("amount", 0.0),
                        createdAt = o.optLong("createdAt", 0L)
                    )
                )
            }
        }

        // Savings Goals
        val savingsGoals = mutableListOf<SavingsGoalEntity>()
        val savingsArray = root.optJSONArray("savingsGoals")
        if (savingsArray != null) {
            for (i in 0 until savingsArray.length()) {
                val o = savingsArray.getJSONObject(i)
                val goalName = if (o.has("name")) o.optString("name", "") else o.optString("title", "")
                savingsGoals.add(
                    SavingsGoalEntity(
                        id = o.optLong("id", 0L),
                        name = goalName,
                        targetAmount = o.optDouble("targetAmount", 0.0),
                        savedAmount = o.optDouble("savedAmount", 0.0),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = o.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }
        }

        // Debts
        val debts = mutableListOf<DebtEntity>()
        val debtsArray = root.optJSONArray("debts")
        if (debtsArray != null) {
            for (i in 0 until debtsArray.length()) {
                val o = debtsArray.getJSONObject(i)
                debts.add(
                    DebtEntity(
                        id = o.optLong("id", 0L),
                        personName = o.optString("personName", ""),
                        type = o.optString("type", "LENT"),
                        originalAmount = o.optDouble("originalAmount", 0.0),
                        createdDate = o.optLong("createdDate", 0L),
                        dueDate = if (o.isNull("dueDate")) null else o.optLong("dueDate"),
                        note = o.optString("note", ""),
                        status = o.optString("status", "ACTIVE"),
                        createdAt = o.optLong("createdAt", 0L),
                        updatedAt = o.optLong("updatedAt", 0L)
                    )
                )
            }
        }

        // Debt Payments
        val payments = mutableListOf<DebtPaymentEntity>()
        val paymentsArray = root.optJSONArray("debtPayments")
        if (paymentsArray != null) {
            for (i in 0 until paymentsArray.length()) {
                val o = paymentsArray.getJSONObject(i)
                payments.add(
                    DebtPaymentEntity(
                        id = o.optLong("id", 0L),
                        debtId = o.optLong("debtId", 0L),
                        amount = o.optDouble("amount", 0.0),
                        paymentDate = o.optLong("paymentDate", 0L),
                        note = o.optString("note", ""),
                        createdAt = o.optLong("createdAt", 0L)
                    )
                )
            }
        }

        // Preference
        val pref = root.optJSONObject("appPreference")?.let { o ->
            AppPreferenceEntity(
                id = 1,
                language = o.optString("language", "BN"),
                themeMode = o.optString("themeMode", "SYSTEM"),
                notificationsEnabled = o.optBoolean("notificationsEnabled", true),
                onboardingCompleted = o.optBoolean("onboardingCompleted", true),
                debtReminderEnabled = o.optBoolean("debtReminderEnabled", true),
                debtReminderDaysBefore = o.optInt("debtReminderDaysBefore", 1)
            )
        }

        val profileName = root.optString("userProfileName").takeIf { it.isNotBlank() }

        return BackupPackage(
            metadata = metadata,
            transactions = transactions,
            customCategories = customCategories,
            budgets = budgets,
            savingsGoals = savingsGoals,
            debts = debts,
            debtPayments = payments,
            appPreference = pref,
            userProfileName = profileName
        )
    }

    private fun deriveKey(seed: String, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(seed.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    fun encryptPayload(plainJson: String, userSeed: String): ByteArray {
        val plainBytes = plainJson.toByteArray(Charsets.UTF_8)
        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_LENGTH).also { SecureRandom().nextBytes(it) }

        val secretKey = deriveKey(userSeed, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        val cipherText = cipher.doFinal(plainBytes)

        val headerBytes = MAGIC_HEADER.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(headerBytes.size + salt.size + iv.size + cipherText.size)
        buffer.put(headerBytes)
        buffer.put(salt)
        buffer.put(iv)
        buffer.put(cipherText)
        return buffer.array()
    }

    fun decryptPayload(encryptedBytes: ByteArray, userSeed: String): String {
        val headerBytes = MAGIC_HEADER.toByteArray(Charsets.UTF_8)
        if (encryptedBytes.size < headerBytes.size + SALT_LENGTH + IV_LENGTH) {
            throw IllegalArgumentException("Invalid backup file: file too small or unrecognized format.")
        }

        val buffer = ByteBuffer.wrap(encryptedBytes)
        val magic = ByteArray(headerBytes.size)
        buffer.get(magic)
        if (!magic.contentEquals(headerBytes)) {
            throw IllegalArgumentException("Unsupported backup signature or corrupted file.")
        }

        val salt = ByteArray(SALT_LENGTH)
        buffer.get(salt)
        val iv = ByteArray(IV_LENGTH)
        buffer.get(iv)

        val cipherText = ByteArray(buffer.remaining())
        buffer.get(cipherText)

        val secretKey = deriveKey(userSeed, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        val plainBytes = cipher.doFinal(cipherText)

        return String(plainBytes, Charsets.UTF_8)
    }
}
