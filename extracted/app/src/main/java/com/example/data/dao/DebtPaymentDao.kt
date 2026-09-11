package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.DebtPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtPaymentDao {

    @Query("SELECT * FROM debt_payments WHERE debtId = :debtId ORDER BY paymentDate ASC")
    fun getPaymentsForDebt(debtId: Long): Flow<List<DebtPaymentEntity>>

    @Query("SELECT * FROM debt_payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<DebtPaymentEntity>>

    @Query("SELECT * FROM debt_payments WHERE debtId = :debtId ORDER BY paymentDate ASC")
    suspend fun getPaymentsForDebtDirect(debtId: Long): List<DebtPaymentEntity>

    @Query("SELECT SUM(amount) FROM debt_payments WHERE debtId = :debtId")
    fun getTotalPaidForDebt(debtId: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: DebtPaymentEntity): Long

    @Query("DELETE FROM debt_payments WHERE debtId = :debtId")
    suspend fun deletePaymentsForDebt(debtId: Long)

    @Query("DELETE FROM debt_payments")
    suspend fun deleteAllPayments()

    @Query("SELECT * FROM debt_payments ORDER BY id ASC")
    suspend fun getAllPaymentsList(): List<DebtPaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<DebtPaymentEntity>)

    @Delete
    suspend fun deletePayment(payment: DebtPaymentEntity)
}
