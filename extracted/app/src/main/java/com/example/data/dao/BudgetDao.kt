package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey LIMIT 1")
    fun getBudgetForMonth(monthKey: String): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets ORDER BY monthKey DESC")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE monthKey = :monthKey")
    suspend fun deleteBudgetForMonth(monthKey: String)

    @Query("SELECT * FROM budgets ORDER BY id ASC")
    suspend fun getAllBudgetsList(): List<BudgetEntity>

    @Query("DELETE FROM budgets")
    suspend fun deleteAllBudgets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)
}
