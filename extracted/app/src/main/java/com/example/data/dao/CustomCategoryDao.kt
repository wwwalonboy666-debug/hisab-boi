package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CustomCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCategoryDao {

    @Query("SELECT * FROM custom_categories ORDER BY id ASC")
    fun getAllCustomCategories(): Flow<List<CustomCategoryEntity>>

    @Query("SELECT * FROM custom_categories WHERE isArchived = 0 ORDER BY id ASC")
    fun getActiveCustomCategories(): Flow<List<CustomCategoryEntity>>

    @Query("SELECT * FROM custom_categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CustomCategoryEntity?

    @Query("SELECT * FROM custom_categories WHERE `key` = :key LIMIT 1")
    suspend fun getCategoryByKey(key: String): CustomCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CustomCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CustomCategoryEntity)

    @Query("UPDATE custom_categories SET isArchived = :isArchived WHERE id = :id")
    suspend fun setArchived(id: Long, isArchived: Boolean)

    @Delete
    suspend fun deleteCategory(category: CustomCategoryEntity)

    @Query("DELETE FROM custom_categories WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM custom_categories ORDER BY id ASC")
    suspend fun getAllCategoriesList(): List<CustomCategoryEntity>

    @Query("DELETE FROM custom_categories")
    suspend fun deleteAllCustomCategories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CustomCategoryEntity>)
}
