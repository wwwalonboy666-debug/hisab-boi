package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AutoBackupFrequency
import com.example.model.GoogleAccountInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BackupPreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("hisabboi_backup_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCOUNT_EMAIL = "google_account_email"
        private const val KEY_ACCOUNT_NAME = "google_account_name"
        private const val KEY_LAST_BACKUP_TIME = "last_backup_time"
        private const val KEY_AUTO_BACKUP_FREQ = "auto_backup_frequency"
        private const val KEY_CLOUD_FILE_ID = "cloud_backup_file_id"
    }

    private val _connectedAccount = MutableStateFlow(loadAccount())
    val connectedAccount: StateFlow<GoogleAccountInfo?> = _connectedAccount.asStateFlow()

    private val _lastBackupTime = MutableStateFlow(loadLastBackupTime())
    val lastBackupTime: StateFlow<Long?> = _lastBackupTime.asStateFlow()

    private val _autoBackupFrequency = MutableStateFlow(loadAutoBackupFrequency())
    val autoBackupFrequency: StateFlow<AutoBackupFrequency> = _autoBackupFrequency.asStateFlow()

    private fun loadAccount(): GoogleAccountInfo? {
        val email = prefs.getString(KEY_ACCOUNT_EMAIL, null) ?: return null
        val name = prefs.getString(KEY_ACCOUNT_NAME, "") ?: ""
        return GoogleAccountInfo(
            email = email,
            displayName = name,
            isConnected = true
        )
    }

    private fun loadLastBackupTime(): Long? {
        val time = prefs.getLong(KEY_LAST_BACKUP_TIME, -1L)
        return if (time > 0L) time else null
    }

    private fun loadAutoBackupFrequency(): AutoBackupFrequency {
        val raw = prefs.getString(KEY_AUTO_BACKUP_FREQ, AutoBackupFrequency.OFF.name)
        return try {
            AutoBackupFrequency.valueOf(raw ?: AutoBackupFrequency.OFF.name)
        } catch (_: Exception) {
            AutoBackupFrequency.OFF
        }
    }

    fun saveAccount(account: GoogleAccountInfo?) {
        if (account != null) {
            prefs.edit()
                .putString(KEY_ACCOUNT_EMAIL, account.email)
                .putString(KEY_ACCOUNT_NAME, account.displayName)
                .apply()
            _connectedAccount.value = account
        } else {
            // Disconnect: keep local backup history intact, clear auth state, disable auto backup
            prefs.edit()
                .remove(KEY_ACCOUNT_EMAIL)
                .remove(KEY_ACCOUNT_NAME)
                .putString(KEY_AUTO_BACKUP_FREQ, AutoBackupFrequency.OFF.name)
                .apply()
            _connectedAccount.value = null
            _autoBackupFrequency.value = AutoBackupFrequency.OFF
        }
    }

    fun updateLastBackupTime(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_BACKUP_TIME, timestamp).apply()
        _lastBackupTime.value = timestamp
    }

    fun setAutoBackupFrequency(freq: AutoBackupFrequency) {
        prefs.edit().putString(KEY_AUTO_BACKUP_FREQ, freq.name).apply()
        _autoBackupFrequency.value = freq
    }

    fun saveCloudFileId(fileId: String?) {
        if (fileId != null) {
            prefs.edit().putString(KEY_CLOUD_FILE_ID, fileId).apply()
        } else {
            prefs.edit().remove(KEY_CLOUD_FILE_ID).apply()
        }
    }

    fun getCloudFileId(): String? {
        return prefs.getString(KEY_CLOUD_FILE_ID, null)
    }

    fun clearAllCloudMetadata() {
        prefs.edit()
            .remove(KEY_CLOUD_FILE_ID)
            .remove(KEY_LAST_BACKUP_TIME)
            .apply()
        _lastBackupTime.value = null
    }
}
