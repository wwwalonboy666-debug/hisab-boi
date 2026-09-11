package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.BackupPreferenceManager
import com.example.data.GoogleDriveBackupService
import com.example.data.HisabBoiDatabase
import com.example.data.repository.HisabBoiRepository
import com.example.model.AutoBackupFrequency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoBackupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefManager = BackupPreferenceManager(context)
                val account = prefManager.connectedAccount.value
                val frequency = prefManager.autoBackupFrequency.value

                if (account != null && frequency == AutoBackupFrequency.DAILY) {
                    val db = HisabBoiDatabase.getInstance(context)
                    val repository = HisabBoiRepository(db)
                    val driveService = GoogleDriveBackupService(context, prefManager)

                    val encryptedBytes = repository.createEncryptedBackup(account.email, account.displayName)
                    driveService.uploadBackup(encryptedBytes, account.email)
                }
            } catch (_: Exception) {
                // Background auto-backup should fail silently and safely without disturbing user
            } finally {
                pendingResult.finish()
            }
        }
    }
}
