package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Query("SELECT * FROM debts ORDER BY createdDate DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE id = :id LIMIT 1")
    fun getDebtById(id: Long): Flow<DebtEntity?>

    @Query("SELECT * FROM debts WHERE id = :id LIMIT 1")
    suspend fun getDebtByIdDirect(id: Long): DebtEntity?

    @Query("SELECT * FROM debts WHERE type = :type ORDER BY createdDate DESC")
    fun getDebtsByType(type: String): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebtById(id: Long)

    @Query("UPDATE debts SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDebtStatus(id: Long, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM debts ORDER BY id ASC")
    suspend fun getAllDebtsList(): List<DebtEntity>

    @Query("DELETE FROM debts")
    suspend fun deleteAllDebts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(debts: List<DebtEntity>)
}
