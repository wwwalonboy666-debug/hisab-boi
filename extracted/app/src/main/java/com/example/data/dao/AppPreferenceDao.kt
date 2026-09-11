package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.AppPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppPreferenceDao {

    @Query("SELECT * FROM app_preferences WHERE id = 1 LIMIT 1")
    fun getPreferences(): Flow<AppPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(preference: AppPreferenceEntity)

    @Query("UPDATE app_preferences SET language = :lang WHERE id = 1")
    suspend fun updateLanguage(lang: String)

    @Query("UPDATE app_preferences SET themeMode = :mode WHERE id = 1")
    suspend fun updateThemeMode(mode: String)

    @Query("UPDATE app_preferences SET onboardingCompleted = :completed WHERE id = 1")
    suspend fun setOnboardingCompleted(completed: Boolean)

    @Query("UPDATE app_preferences SET notificationsEnabled = :enabled WHERE id = 1")
    suspend fun setNotificationsEnabled(enabled: Boolean)

    @Query("UPDATE app_preferences SET debtReminderEnabled = :enabled WHERE id = 1")
    suspend fun setDebtReminderEnabled(enabled: Boolean)

    @Query("UPDATE app_preferences SET debtReminderDaysBefore = :days WHERE id = 1")
    suspend fun setDebtReminderDaysBefore(days: Int)

    @Query("SELECT * FROM app_preferences WHERE id = 1 LIMIT 1")
    suspend fun getPreferenceDirect(): AppPreferenceEntity?
}
