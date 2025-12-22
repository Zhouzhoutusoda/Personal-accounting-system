package com.example.personalincomeandexpenditureaccountingsystem.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    
    /**
     * 插入新的收支记录
     */
    @Insert
    suspend fun insertRecord(record: Record): Long
    
    /**
     * 更新收支记录
     */
    @Update
    suspend fun updateRecord(record: Record)
    
    /**
     * 删除收支记录
     */
    @Delete
    suspend fun deleteRecord(record: Record)
    
    /**
     * 根据ID删除收支记录
     */
    @Query("DELETE FROM record WHERE id = :recordId")
    suspend fun deleteRecordById(recordId: Long)
    
    /**
     * 根据ID查询收支记录
     */
    @Query("SELECT * FROM record WHERE id = :recordId")
    suspend fun getRecordById(recordId: Long): Record?
    
    /**
     * 查询指定账本的所有记录
     */
    @Query("SELECT * FROM record WHERE accountId = :accountId ORDER BY date DESC")
    fun getAllRecordsByAccountId(accountId: Long): Flow<List<Record>>
    
    /**
     * 查询指定账本的所有记录（不使用Flow，一次性查询）
     */
    @Query("SELECT * FROM record WHERE accountId = :accountId ORDER BY date DESC")
    suspend fun getAllRecordsByAccountIdOnce(accountId: Long): List<Record>
    
    /**
     * 查询指定账本在指定日期范围内的记录
     */
    @Query("SELECT * FROM record WHERE accountId = :accountId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getRecordsByDateRange(accountId: Long, startDate: Long, endDate: Long): List<Record>
    
    /**
     * 查询指定账本的指定类型的记录（收入或支出）
     */
    @Query("SELECT * FROM record WHERE accountId = :accountId AND type = :type ORDER BY date DESC")
    fun getRecordsByType(accountId: Long, type: String): Flow<List<Record>>
    
    /**
     * 查询指定账本的指定类别的记录
     */
    @Query("SELECT * FROM record WHERE accountId = :accountId AND category = :category ORDER BY date DESC")
    fun getRecordsByCategory(accountId: Long, category: String): Flow<List<Record>>
    
    /**
     * 删除指定账本的所有记录
     */
    @Query("DELETE FROM record WHERE accountId = :accountId")
    suspend fun deleteAllRecordsByAccountId(accountId: Long)
    
    /**
     * 获取指定账本的总收入
     */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM record WHERE accountId = :accountId AND type = 'income'")
    suspend fun getTotalIncome(accountId: Long): Double
    
    /**
     * 获取指定账本的总支出
     */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM record WHERE accountId = :accountId AND type = 'expense'")
    suspend fun getTotalExpense(accountId: Long): Double
}
