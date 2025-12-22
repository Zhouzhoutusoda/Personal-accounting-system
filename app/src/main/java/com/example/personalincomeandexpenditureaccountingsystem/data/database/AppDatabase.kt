package com.example.personalincomeandexpenditureaccountingsystem.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.personalincomeandexpenditureaccountingsystem.data.dao.AccountDao
import com.example.personalincomeandexpenditureaccountingsystem.data.dao.RecordDao
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Account
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Record

@Database(
    entities = [Account::class, Record::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun accountDao(): AccountDao
    abstract fun recordDao(): RecordDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personal_accounting_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
