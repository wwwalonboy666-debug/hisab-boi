package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream

class UserProfileManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private fun loadProfile(): UserProfile {
        val name = prefs.getString(KEY_USER_NAME, "") ?: ""
        val photoPath = prefs.getString(KEY_PHOTO_PATH, null)
        val photoUpdatedAt = prefs.getLong(KEY_PHOTO_UPDATED_AT, 0L)
        val isSetupCompleted = prefs.getBoolean(KEY_IS_SETUP_COMPLETED, false)

        val validPhotoPath = if (photoPath != null && File(photoPath).exists()) {
            photoPath
        } else {
            null
        }

        return UserProfile(
            name = name,
            photoPath = validPhotoPath,
            photoUpdatedAt = photoUpdatedAt,
            isSetupCompleted = isSetupCompleted
        )
    }

    fun saveProfile(name: String, photoUri: Uri?, context: Context) {
        val trimmedName = name.trim()
        val savedPhotoPath = if (photoUri != null) {
            copyUriToInternalStorage(context, photoUri)
        } else {
            null
        }
        val now = System.currentTimeMillis()

        prefs.edit()
            .putString(KEY_USER_NAME, trimmedName)
            .putString(KEY_PHOTO_PATH, savedPhotoPath)
            .putLong(KEY_PHOTO_UPDATED_AT, now)
            .putBoolean(KEY_IS_SETUP_COMPLETED, true)
            .apply()

        _profile.value = UserProfile(
            name = trimmedName,
            photoPath = savedPhotoPath,
            photoUpdatedAt = now,
            isSetupCompleted = true
        )
    }

    fun updateName(name: String) {
        val trimmedName = name.trim()
        prefs.edit()
            .putString(KEY_USER_NAME, trimmedName)
            .apply()

        _profile.value = _profile.value.copy(name = trimmedName)
    }

    fun updatePhoto(uri: Uri, context: Context) {
        val savedPhotoPath = copyUriToInternalStorage(context, uri)
        val now = System.currentTimeMillis()

        prefs.edit()
            .putString(KEY_PHOTO_PATH, savedPhotoPath)
            .putLong(KEY_PHOTO_UPDATED_AT, now)
            .apply()

        _profile.value = _profile.value.copy(
            photoPath = savedPhotoPath,
            photoUpdatedAt = now
        )
    }

    fun removePhoto(context: Context) {
        try {
            val currentPath = prefs.getString(KEY_PHOTO_PATH, null)
            if (currentPath != null) {
                val file = File(currentPath)
                if (file.exists()) file.delete()
            }
            context.filesDir.listFiles()?.forEach { f ->
                if (f.name.startsWith("profile_photo_") && f.name.endsWith(".jpg")) {
                    f.delete()
                }
            }
        } catch (_: Exception) {}

        val now = System.currentTimeMillis()
        prefs.edit()
            .remove(KEY_PHOTO_PATH)
            .putLong(KEY_PHOTO_UPDATED_AT, now)
            .apply()

        _profile.value = _profile.value.copy(
            photoPath = null,
            photoUpdatedAt = now
        )
    }

    private fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val now = System.currentTimeMillis()
            val targetFile = File(context.filesDir, "profile_photo_$now.jpg")

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            try {
                val oldPath = prefs.getString(KEY_PHOTO_PATH, null)
                if (oldPath != null && oldPath != targetFile.absolutePath) {
                    val oldFile = File(oldPath)
                    if (oldFile.exists()) oldFile.delete()
                }
            } catch (_: Exception) {}

            targetFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val PREFS_NAME = "hisabboi_user_profile"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_PHOTO_PATH = "user_photo_path"
        private const val KEY_PHOTO_UPDATED_AT = "user_photo_updated_at"
        private const val KEY_IS_SETUP_COMPLETED = "is_setup_completed"
    }
}
