package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_preferences")
data class AppPreferenceEntity(
    @PrimaryKey
    val id: Int = 1,
    val language: String = "bn",
    val themeMode: String = "SYSTEM",
    val onboardingCompleted: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val debtReminderEnabled: Boolean = true,
    val debtReminderDaysBefore: Int = 1
)
