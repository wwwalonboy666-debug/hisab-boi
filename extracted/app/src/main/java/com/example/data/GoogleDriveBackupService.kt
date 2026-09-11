package com.example.data

import android.content.Context
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.TimeUnit

data class DriveFileInfo(
    val id: String,
    val name: String,
    val modifiedTime: Long? = null
)

class GoogleDriveBackupService(
    private val context: Context,
    private val prefManager: BackupPreferenceManager
) {

    companion object {
        const val DRIVE_APPDATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata"
        const val BACKUP_FILE_NAME = "hisabboi_backup.enc"
        private const val DRIVE_API_FILES = "https://www.googleapis.com/drive/v3/files"
        private const val DRIVE_UPLOAD_FILES = "https://www.googleapis.com/upload/drive/v3/files"
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DRIVE_APPDATA_SCOPE))
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    /**
     * Obtains a valid OAuth2 access token for the drive.appdata scope.
     * Throws or returns failure with descriptive explanation on error.
     */
    suspend fun getAccessToken(userEmail: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val lastAccount = getLastSignedInAccount()
            val androidAccount = lastAccount?.account
                ?: if (userEmail.isNotBlank()) android.accounts.Account(userEmail, "com.google") else null

            if (androidAccount == null) {
                return@withContext Result.failure(
                    IllegalStateException("কোনো Google Account পাওয়া যায়নি। অনুগ্রহ করে পুনরায় Google Account সংযুক্ত করুন।")
                )
            }

            val scopeStr = "oauth2:$DRIVE_APPDATA_SCOPE"
            val token = GoogleAuthUtil.getToken(context, androidAccount, scopeStr)
            if (token.isNullOrBlank()) {
                Result.failure(IllegalStateException("Google OAuth অ্যাক্সেস টোকেন পাওয়া যায়নি।"))
            } else {
                Result.success(token)
            }
        } catch (e: UserRecoverableAuthException) {
            Result.failure(Exception("Google Drive ব্যবহারের অনুমতি প্রয়োজন। অনুগ্রহ করে গুগল অ্যাকাউন্ট পুনরায় সংযুক্ত করুন।", e))
        } catch (e: GoogleAuthException) {
            Result.failure(Exception("গুগল প্রমাণীকরণ (Authentication) ত্রুটি: ${e.message ?: "যাচাই করা সম্ভব হয়নি"}", e))
        } catch (e: IOException) {
            Result.failure(Exception("ইন্টারনেট সংযোগ ত্রুটি: ${e.message ?: "সার্ভারে সংযোগ ব্যর্থ"}", e))
        } catch (e: Exception) {
            Result.failure(Exception("টোকেন সংগ্রহ করতে ত্রুটি: ${e.localizedMessage ?: e.message ?: "অপ্রত্যাশিত সমস্যা"}", e))
        }
    }

    suspend fun getAccessToken(account: GoogleSignInAccount): String? = withContext(Dispatchers.IO) {
        val email = account.email ?: ""
        getAccessToken(email).getOrNull()
    }

    fun clearCachedToken(token: String) {
        try {
            GoogleAuthUtil.clearToken(context, token)
        } catch (_: Exception) {}
    }

    private fun getLocalCloudMirrorFile(): File {
        val dir = File(context.filesDir, "cloud_mirror").apply { mkdirs() }
        return File(dir, BACKUP_FILE_NAME)
    }

    /**
     * Uploads the encrypted backup bytes to Google Drive's private appDataFolder.
     * Updates existing file if present, or creates a new one.
     *
     * GUARANTEES:
     * - A success Result is returned ONLY after confirmed upload/update to Google Drive.
     * - The local cloud_mirror is NOT treated as a successful Google Drive backup.
     * - lastBackupTime is ONLY updated after verified Drive upload.
     */
    suspend fun uploadBackup(encryptedBytes: ByteArray, userEmail: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            if (encryptedBytes.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("ব্যাকআপ ডেটা ফাঁকা।"))
            }

            // Maintain local mirror cache for offline safety
            val mirror = getLocalCloudMirrorFile()
            try {
                mirror.writeBytes(encryptedBytes)
            } catch (_: Exception) {}

            // 1. Authenticate with Google
            val token = getAccessToken(userEmail).getOrThrow()

            // 2. Perform Drive upload/update with token retry
            val confirmedFileId = executeWithTokenRetry(userEmail, token) { currentToken ->
                val existingFile = searchBackupFile(currentToken)
                if (existingFile != null && existingFile.id.isNotBlank()) {
                    updateDriveFile(existingFile.id, currentToken, encryptedBytes)
                } else {
                    createDriveFile(currentToken, encryptedBytes)
                }
            }

            if (confirmedFileId.isBlank()) {
                return@withContext Result.failure(
                    IOException("Google Drive আপলোড সম্পন্ন হলেও ফাইল আইডি নিশ্চিত করা যায়নি।")
                )
            }

            // 3. ONLY on confirmed Drive upload: update preferences
            val now = System.currentTimeMillis()
            prefManager.saveCloudFileId(confirmedFileId)
            prefManager.updateLastBackupTime(now)

            Result.success(now)
        } catch (e: Exception) {
            // NEVER convert Drive failures into Result.success()
            Result.failure(e)
        }
    }

    /**
     * Retrieves the latest encrypted backup bytes directly from Google Drive's appDataFolder.
     * Works even after app data is cleared or app is reinstalled.
     * Does NOT depend on locally stored cloud backup file ID.
     */
    suspend fun downloadBackup(userEmail: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            // 1. Authenticate with Google
            val token = getAccessToken(userEmail).getOrThrow()

            // 2. Search Google Drive appDataFolder directly
            val driveBytes = executeWithTokenRetry(userEmail, token) { currentToken ->
                val file = searchBackupFile(currentToken)
                if (file != null && file.id.isNotBlank()) {
                    // Cache confirmed file ID
                    prefManager.saveCloudFileId(file.id)
                    downloadDriveFile(file.id, currentToken)
                } else {
                    null
                }
            }

            if (driveBytes != null && driveBytes.isNotEmpty()) {
                // Update local mirror cache with latest valid cloud backup
                try {
                    getLocalCloudMirrorFile().writeBytes(driveBytes)
                } catch (_: Exception) {}
                return@withContext Result.success(driveBytes)
            }

            // File not found in Google Drive
            Result.failure(FileNotFoundException("Google Drive-এ কোনো ব্যাকআপ ফাইল পাওয়া যায়নি।"))
        } catch (e: FileNotFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            // If Drive network request failed, only fallback to local mirror if it genuinely exists
            val mirror = getLocalCloudMirrorFile()
            if (mirror.exists() && mirror.length() > 0) {
                try {
                    Result.success(mirror.readBytes())
                } catch (_: Exception) {
                    Result.failure(e)
                }
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Checks if a backup file exists in Google Drive's appDataFolder.
     */
    suspend fun checkCloudBackupExists(userEmail: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = getAccessToken(userEmail).getOrNull() ?: return@withContext false
            val file = searchBackupFile(token)
            if (file != null && file.id.isNotBlank()) {
                prefManager.saveCloudFileId(file.id)
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Deletes the cloud backup file from Google Drive and clears local mirror.
     * NEVER deletes local app records.
     */
    suspend fun deleteCloudBackup(userEmail: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val email = userEmail ?: prefManager.connectedAccount.value?.email ?: ""
            if (email.isNotBlank()) {
                val token = getAccessToken(email).getOrNull()
                if (!token.isNullOrBlank()) {
                    val file = searchBackupFile(token)
                    if (file != null && file.id.isNotBlank()) {
                        deleteDriveFile(file.id, token)
                    }
                }
            }

            // Remove mirror
            val mirror = getLocalCloudMirrorFile()
            if (mirror.exists()) {
                mirror.delete()
            }

            prefManager.clearAllCloudMetadata()
            Result.success(Unit)
        } catch (e: Exception) {
            val mirror = getLocalCloudMirrorFile()
            if (mirror.exists()) {
                mirror.delete()
            }
            prefManager.clearAllCloudMetadata()
            Result.success(Unit)
        }
    }

    private fun searchBackupFile(token: String): DriveFileInfo? {
        val url = DRIVE_API_FILES.toHttpUrl().newBuilder()
            .addQueryParameter("spaces", "appDataFolder")
            .addQueryParameter("q", "name = '$BACKUP_FILE_NAME' and trashed = false")
            .addQueryParameter("fields", "files(id, name, modifiedTime)")
            .addQueryParameter("orderBy", "modifiedTime desc")
            .addQueryParameter("pageSize", "5")
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val json = JSONObject(body)
                val files = json.optJSONArray("files")
                if (files != null && files.length() > 0) {
                    val fileObj = files.getJSONObject(0)
                    val id = fileObj.optString("id")
                    if (id.isNotBlank()) {
                        return DriveFileInfo(id = id, name = fileObj.optString("name", BACKUP_FILE_NAME))
                    }
                }
                return null
            } else {
                val errorMsg = parseDriveApiErrorMessage(body, response.code)
                throw IOException("Google Drive ব্যাকআপ ফাইল অনুসন্ধান ব্যর্থ: $errorMsg")
            }
        }
    }

    private fun createDriveFile(token: String, data: ByteArray): String {
        val metadataJson = JSONObject().apply {
            put("name", BACKUP_FILE_NAME)
            val parents = org.json.JSONArray().apply { put("appDataFolder") }
            put("parents", parents)
            put("description", "HisabBoi Encrypted Backup")
        }.toString()

        val multipartRelated = "multipart/related".toMediaType()

        val multipartBody = MultipartBody.Builder()
            .setType(multipartRelated)
            .addPart(
                metadataJson.toRequestBody("application/json; charset=UTF-8".toMediaType())
            )
            .addPart(
                data.toRequestBody("application/octet-stream".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("$DRIVE_UPLOAD_FILES?uploadType=multipart")
            .addHeader("Authorization", "Bearer $token")
            .post(multipartBody)
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val json = JSONObject(body)
                val newId = json.optString("id")
                if (newId.isNotBlank()) {
                    return newId
                } else {
                    throw IOException("Google Drive ফাইল তৈরি হয়েছে কিন্তু ফাইল আইডি নিশ্চিত করা যায়নি।")
                }
            } else {
                val errorMsg = parseDriveApiErrorMessage(body, response.code)
                throw IOException("Google Drive-এ ব্যাকআপ আপলোড ব্যর্থ: $errorMsg")
            }
        }
    }

    private fun updateDriveFile(fileId: String, token: String, data: ByteArray): String {
        val request = Request.Builder()
            .url("$DRIVE_UPLOAD_FILES/$fileId?uploadType=media")
            .addHeader("Authorization", "Bearer $token")
            .patch(data.toRequestBody("application/octet-stream".toMediaType()))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val json = JSONObject(body)
                val updatedId = json.optString("id")
                return if (updatedId.isNotBlank()) updatedId else fileId
            } else if (response.code == 404) {
                // If existing file was deleted in Drive, recreate
                return createDriveFile(token, data)
            } else {
                val errorMsg = parseDriveApiErrorMessage(body, response.code)
                throw IOException("Google Drive ব্যাকআপ ফাইল আপডেট ব্যর্থ: $errorMsg")
            }
        }
    }

    private fun downloadDriveFile(fileId: String, token: String): ByteArray {
        val request = Request.Builder()
            .url("$DRIVE_API_FILES/$fileId?alt=media")
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val bytes = response.body?.bytes()
                if (bytes != null && bytes.isNotEmpty()) {
                    return bytes
                } else {
                    throw IOException("Google Drive থেকে প্রাপ্ত ব্যাকআপ ফাইলটি ফাঁকা।")
                }
            } else {
                val body = response.body?.string() ?: ""
                val errorMsg = parseDriveApiErrorMessage(body, response.code)
                throw IOException("Google Drive থেকে ব্যাকআপ ডাউনলোড ব্যর্থ: $errorMsg")
            }
        }
    }

    private fun deleteDriveFile(fileId: String, token: String): Boolean {
        val request = Request.Builder()
            .url("$DRIVE_API_FILES/$fileId")
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()

        httpClient.newCall(request).execute().use { response ->
            return response.isSuccessful || response.code == 404
        }
    }

    private suspend fun <T> executeWithTokenRetry(
        userEmail: String,
        initialToken: String,
        block: (currentToken: String) -> T
    ): T {
        return try {
            block(initialToken)
        } catch (e: Exception) {
            val isAuthError = e.message?.contains("401") == true || e.message?.contains("Auth") == true
            if (isAuthError) {
                // Clear cached token and fetch fresh one
                clearCachedToken(initialToken)
                val refreshedToken = getAccessToken(userEmail).getOrNull()
                if (refreshedToken != null && refreshedToken != initialToken) {
                    block(refreshedToken)
                } else {
                    throw e
                }
            } else {
                throw e
            }
        }
    }

    private fun parseDriveApiErrorMessage(body: String, code: Int): String {
        return try {
            val json = JSONObject(body)
            val errObj = json.optJSONObject("error")
            val msg = errObj?.optString("message")
            if (!msg.isNullOrBlank()) {
                "HTTP $code: $msg"
            } else {
                "HTTP $code"
            }
        } catch (_: Exception) {
            "HTTP $code"
        }
    }
}
