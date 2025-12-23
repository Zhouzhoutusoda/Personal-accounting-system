package com.example.personalincomeandexpenditureaccountingsystem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Account
import com.example.personalincomeandexpenditureaccountingsystem.data.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * 账本 ViewModel
 * 管理账本相关的业务逻辑和 UI 状态
 */
class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    // 所有账本列表
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 操作成功消息
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadAllAccounts()
    }

    /**
     * 加载所有账本
     */
    private fun loadAllAccounts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllAccounts()
                    .catch { exception ->
                        _errorMessage.value = "加载账本失败: ${exception.message}"
                    }
                    .collect { accountList ->
                        _accounts.value = accountList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = "发生错误: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 新增账本
     */
    fun addAccount(account: Account) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val newId = repository.insertAccount(account)
                _successMessage.value = "账本 '${account.name}' 创建成功"
                _isLoading.value = false
                // 重新加载列表
                loadAllAccounts()
            } catch (e: Exception) {
                _errorMessage.value = "创建账本失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 更新账本
     */
    fun updateAccount(account: Account) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateAccount(account)
                _successMessage.value = "账本已更新"
                _isLoading.value = false
                loadAllAccounts()
            } catch (e: Exception) {
                _errorMessage.value = "更新账本失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 删除账本
     */
    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteAccount(account)
                _successMessage.value = "账本已删除"
                _isLoading.value = false
                loadAllAccounts()
            } catch (e: Exception) {
                _errorMessage.value = "删除账本失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * 清除成功消息
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    /**
     * 获取账本总余额
     */
    fun getTotalBalance(): Double {
        return _accounts.value.sumOf { it.balance }
    }

    /**
     * 获取账本数量
     */
    fun getAccountCount(): Int {
        return _accounts.value.size
    }
}
