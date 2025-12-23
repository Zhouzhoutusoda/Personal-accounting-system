package com.example.personalincomeandexpenditureaccountingsystem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.personalincomeandexpenditureaccountingsystem.data.repository.AccountRepository
import com.example.personalincomeandexpenditureaccountingsystem.data.repository.RecordRepository

/**
 * AccountViewModel 工厂类
 */
class AccountViewModelFactory(
    private val repository: AccountRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountViewModel(repository) as T
    }
}

/**
 * RecordViewModel 工厂类
 */
class RecordViewModelFactory(
    private val repository: RecordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordViewModel(repository) as T
    }
}
