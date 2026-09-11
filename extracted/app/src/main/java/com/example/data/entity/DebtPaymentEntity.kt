package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debt_payments",
    indices = [
        Index(value = ["debtId"]),
        Index(value = ["paymentDate"])
    ]
)
data class DebtPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val debtId: Long,
    val amount: Double,
    val paymentDate: Long,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
