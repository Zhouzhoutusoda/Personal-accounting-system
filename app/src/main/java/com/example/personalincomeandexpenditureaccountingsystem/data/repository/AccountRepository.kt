package com.example.personalincomeandexpenditureaccountingsystem.data.repository

import com.example.personalincomeandexpenditureaccountingsystem.data.dao.AccountDao
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Account
import kotlinx.coroutines.flow.Flow

/**
 * 账本数据仓储层
 * 负责与数据库的交互，为 ViewModel 提供数据接口
 */
class AccountRepository(private val accountDao: AccountDao) {

    /**
     * 获取所有账本（实时监听）
     */
    fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts()
    }

    /**
     * 获取所有账本（单次查询）
     */
    suspend fun getAllAccountsOnce(): List<Account> {
        return accountDao.getAllAccountsOnce()
    }

    /**
     * 根据 ID 获取账本
     */
    suspend fun getAccountById(id: Long): Account? {
        return accountDao.getAccountById(id)
    }

    /**
     * 根据名称获取账本
     */
    suspend fun getAccountByName(name: String): Account? {
        return accountDao.getAccountByName(name)
    }

    /**
     * 新增账本
     */
    suspend fun insertAccount(account: Account): Long {
        return accountDao.insertAccount(account)
    }

    /**
     * 更新账本信息
     */
    suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account)
    }

    /**
     * 删除账本
     */
    suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account)
    }

    /**
     * 根据 ID 删除账本
     */
    suspend fun deleteAccountById(id: Long) {
        accountDao.deleteAccountById(id)
    }

    /**
     * 更新账本余额
     */
    suspend fun updateBalance(id: Long, balance: Double) {
        accountDao.updateBalance(id, balance)
    }

    /**
     * 增加账本余额
     */
    suspend fun addBalance(id: Long, amount: Double) {
        accountDao.addBalance(id, amount)
    }

    /**
     * 减少账本余额
     */
    suspend fun subtractBalance(id: Long, amount: Double) {
        accountDao.subtractBalance(id, amount)
    }
}
