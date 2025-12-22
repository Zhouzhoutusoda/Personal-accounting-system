package com.example.personalincomeandexpenditureaccountingsystem.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    
    /**
     * 插入新的账本
     */
    @Insert
    suspend fun insertAccount(account: Account): Long
    
    /**
     * 更新账本信息
     */
    @Update
    suspend fun updateAccount(account: Account)
    
    /**
     * 删除账本
     */
    @Delete
    suspend fun deleteAccount(account: Account)
    
    /**
     * 根据ID删除账本
     */
    @Query("DELETE FROM account WHERE id = :accountId")
    suspend fun deleteAccountById(accountId: Long)
    
    /**
     * 根据ID查询账本
     */
    @Query("SELECT * FROM account WHERE id = :accountId")
    suspend fun getAccountById(accountId: Long): Account?
    
    /**
     * 查询所有账本
     */
    @Query("SELECT * FROM account ORDER BY id DESC")
    fun getAllAccounts(): Flow<List<Account>>
    
    /**
     * 查询所有账本（不使用Flow，一次性查询）
     */
    @Query("SELECT * FROM account ORDER BY id DESC")
    suspend fun getAllAccountsOnce(): List<Account>
    
    /**
     * 根据账本名称查询账本
     */
    @Query("SELECT * FROM account WHERE name = :name")
    suspend fun getAccountByName(name: String): Account?
    
    /**
     * 更新账本余额
     */
    @Query("UPDATE account SET balance = :balance WHERE id = :accountId")
    suspend fun updateBalance(accountId: Long, balance: Double)
    
    /**
     * 增加账本余额
     */
    @Query("UPDATE account SET balance = balance + :amount WHERE id = :accountId")
    suspend fun addBalance(accountId: Long, amount: Double)
    
    /**
     * 减少账本余额
     */
    @Query("UPDATE account SET balance = balance - :amount WHERE id = :accountId")
    suspend fun subtractBalance(accountId: Long, amount: Double)
}
