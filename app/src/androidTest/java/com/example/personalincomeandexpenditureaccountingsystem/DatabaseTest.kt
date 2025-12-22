package com.example.personalincomeandexpenditureaccountingsystem.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.personalincomeandexpenditureaccountingsystem.data.database.AppDatabase
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Account
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Record
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    
    private lateinit var db: AppDatabase
    
    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = AppDatabase.getDatabase(context)
    }
    
    @After
    fun closeDb() {
        db.close()
    }
    
    @Test
    fun testInsertAndQueryAccount() = runBlocking {
        val account = Account(name = "Test Account", balance = 1000.0)
        val accountId = db.accountDao().insertAccount(account)
        
        val retrieved = db.accountDao().getAccountById(accountId)
        assert(retrieved != null)
        assert(retrieved?.name == "Test Account")
        assert(retrieved?.balance == 1000.0)
    }
    
    @Test
    fun testInsertAndQueryRecord() = runBlocking {
        // 先创建一个账本
        val account = Account(name = "Test Account 2", balance = 2000.0)
        val accountId = db.accountDao().insertAccount(account)
        
        // 创建一条记录
        val record = Record(
            title = "Test Income",
            amount = 500.0,
            type = "income",
            category = "Salary",
            date = System.currentTimeMillis(),
            note = "Test note",
            accountId = accountId
        )
        val recordId = db.recordDao().insertRecord(record)
        
        // 查询记录
        val retrieved = db.recordDao().getRecordById(recordId)
        assert(retrieved != null)
        assert(retrieved?.title == "Test Income")
        assert(retrieved?.amount == 500.0)
        assert(retrieved?.type == "income")
    }
    
    @Test
    fun testMultipleAccounts() = runBlocking {
        val account1 = Account(name = "Account 1", balance = 1000.0)
        val account2 = Account(name = "Account 2", balance = 2000.0)
        
        db.accountDao().insertAccount(account1)
        db.accountDao().insertAccount(account2)
        
        val allAccounts = db.accountDao().getAllAccountsOnce()
        assert(allAccounts.size >= 2)
    }
}
