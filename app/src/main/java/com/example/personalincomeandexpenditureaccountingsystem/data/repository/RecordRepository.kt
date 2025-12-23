package com.example.personalincomeandexpenditureaccountingsystem.data.repository

import com.example.personalincomeandexpenditureaccountingsystem.data.dao.RecordDao
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Record
import kotlinx.coroutines.flow.Flow

/**
 * 收支记录数据仓储层
 * 负责与数据库的交互，为 ViewModel 提供数据接口
 */
class RecordRepository(private val recordDao: RecordDao) {

    /**
     * 新增记录
     */
    suspend fun insertRecord(record: Record): Long {
        return recordDao.insertRecord(record)
    }

    /**
     * 更新记录
     */
    suspend fun updateRecord(record: Record) {
        recordDao.updateRecord(record)
    }

    /**
     * 删除记录
     */
    suspend fun deleteRecord(record: Record) {
        recordDao.deleteRecord(record)
    }

    /**
     * 根据 ID 删除记录
     */
    suspend fun deleteRecordById(id: Long) {
        recordDao.deleteRecordById(id)
    }

    /**
     * 根据 ID 获取单条记录
     */
    suspend fun getRecordById(id: Long): Record? {
        return recordDao.getRecordById(id)
    }

    /**
     * 获取指定账本的所有记录（实时监听）
     */
    fun getAllRecordsByAccountId(accountId: Long): Flow<List<Record>> {
        return recordDao.getAllRecordsByAccountId(accountId)
    }

    /**
     * 获取指定账本的所有记录（单次查询）
     */
    suspend fun getAllRecordsByAccountIdOnce(accountId: Long): List<Record> {
        return recordDao.getAllRecordsByAccountIdOnce(accountId)
    }

    /**
     * 获取指定日期范围内的记录
     */
    suspend fun getRecordsByDateRange(accountId: Long, startDate: Long, endDate: Long): List<Record> {
        return recordDao.getRecordsByDateRange(accountId, startDate, endDate)
    }

    /**
     * 获取指定类型的记录
     */
    fun getRecordsByType(accountId: Long, type: String): Flow<List<Record>> {
        return recordDao.getRecordsByType(accountId, type)
    }

    /**
     * 获取指定分类的记录
     */
    fun getRecordsByCategory(accountId: Long, category: String): Flow<List<Record>> {
        return recordDao.getRecordsByCategory(accountId, category)
    }

    /**
     * 获取总收入（指定账本）
     */
    suspend fun getTotalIncome(accountId: Long): Double {
        return recordDao.getTotalIncome(accountId) ?: 0.0
    }

    /**
     * 获取总支出（指定账本）
     */
    suspend fun getTotalExpense(accountId: Long): Double {
        return recordDao.getTotalExpense(accountId) ?: 0.0
    }

    /**
     * 删除指定账本的所有记录
     */
    suspend fun deleteAllRecordsByAccountId(accountId: Long) {
        recordDao.deleteAllRecordsByAccountId(accountId)
    }
}
