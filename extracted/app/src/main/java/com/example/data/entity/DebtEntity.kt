package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debts",
    indices = [
        Index(value = ["personName"]),
        Index(value = ["type"]),
        Index(value = ["status"]),
        Index(value = ["dueDate"])
    ]
)
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val personName: String,
    val type: String, // "LENT" (User lent money -> user will receive) or "BORROWED" (User borrowed money -> user needs to pay)
    val originalAmount: Double,
    val createdDate: Long,
    val dueDate: Long? = null,
    val note: String = "",
    val status: String = "ACTIVE", // "ACTIVE", "PAID", "OVERDUE"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
