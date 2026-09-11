package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_categories")
data class CustomCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String, // e.g. "CUSTOM_1725548000"
    val nameBn: String,
    val nameEn: String,
    val type: String, // "EXPENSE" or "INCOME"
    val iconName: String, // e.g. "Storefront", "Spa", "Pets", etc.
    val colorHex: String = "#4CAF50",
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
