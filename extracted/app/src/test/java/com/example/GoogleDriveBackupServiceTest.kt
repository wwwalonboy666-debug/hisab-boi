package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.data.BackupPreferenceManager
import com.example.data.GoogleDriveBackupService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GoogleDriveBackupServiceTest {

    private lateinit var prefManager: BackupPreferenceManager
    private lateinit var driveService: GoogleDriveBackupService

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        prefManager = BackupPreferenceManager(context)
        prefManager.clearAllCloudMetadata()
        driveService = GoogleDriveBackupService(context, prefManager)
    }

    @Test
    fun testUploadBackup_withoutValidToken_returnsFailure_andDoesNotUpdateLastBackupTime() = runBlocking {
        // When Google account/token is unavailable in test environment:
        val dummyBytes = "EncryptedBackupPayloadBytes".toByteArray(Charsets.UTF_8)
        val result = driveService.uploadBackup(dummyBytes, "testuser@example.com")

        // 1. Result must be real failure, NEVER silently converted to success
        assertTrue("Upload backup must fail when token/network cannot reach Google Drive", result.isFailure)

        // 2. lastBackupTime and cloudBackupFileId must NOT be updated
        assertNull("lastBackupTime must not be updated on failed upload", prefManager.lastBackupTime.value)
        assertNull("cloudFileId must not be saved on failed upload", prefManager.getCloudFileId())
    }

    @Test
    fun testDownloadBackup_withoutExistingCloudFile_returnsFailure() = runBlocking {
        // When app data is cleared, no file ID exists and no Google Drive token exists:
        val result = driveService.downloadBackup("testuser@example.com")

        // Result must be a real failure
        assertTrue("Download backup must fail when no cloud backup is found", result.isFailure)
    }

    @Test
    fun testCheckCloudBackupExists_returnsFalse_whenNoAccount() = runBlocking {
        val exists = driveService.checkCloudBackupExists("nonexistent@example.com")
        assertFalse("checkCloudBackupExists should be false when unauthenticated", exists)
    }
}
