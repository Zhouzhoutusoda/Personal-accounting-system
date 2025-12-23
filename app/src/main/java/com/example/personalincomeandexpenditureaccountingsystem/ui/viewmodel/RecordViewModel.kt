package com.example.personalincomeandexpenditureaccountingsystem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Record
import com.example.personalincomeandexpenditureaccountingsystem.data.repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * 收支记录 ViewModel
 * 管理记录相关的业务逻辑和 UI 状态
 */
class RecordViewModel(private val repository: RecordRepository) : ViewModel() {

    // 当前账本的所有记录
    private val _records = MutableStateFlow<List<Record>>(emptyList())
    val records: StateFlow<List<Record>> = _records.asStateFlow()

    // 当前选中的账本 ID
    private val _currentAccountId = MutableStateFlow<Long?>(null)
    val currentAccountId: StateFlow<Long?> = _currentAccountId.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 操作成功消息
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // 本月统计数据
    private val _monthlyStats = MutableStateFlow(MonthlyStats())
    val monthlyStats: StateFlow<MonthlyStats> = _monthlyStats.asStateFlow()

    /**
     * 设置当前账本并加载其记录
     */
    fun setCurrentAccount(accountId: Long) {
        _currentAccountId.value = accountId
        loadRecordsByAccountId(accountId)
        calculateMonthlyStats(accountId)
    }

    /**
     * 加载指定账本的记录
     */
    private fun loadRecordsByAccountId(accountId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllRecordsByAccountId(accountId)
                    .catch { exception ->
                        _errorMessage.value = "加载记录失败: ${exception.message}"
                    }
                    .collect { recordList ->
                        _records.value = recordList.sortedByDescending { it.date }
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = "发生错误: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 新增记录
     */
    fun addRecord(record: Record) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val newId = repository.insertRecord(record)
                _successMessage.value = "记录已保存"
                _isLoading.value = false
                // 重新加载记录列表
                _currentAccountId.value?.let { loadRecordsByAccountId(it) }
                _currentAccountId.value?.let { calculateMonthlyStats(it) }
            } catch (e: Exception) {
                _errorMessage.value = "保存记录失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 更新记录
     */
    fun updateRecord(record: Record) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateRecord(record)
                _successMessage.value = "记录已更新"
                _isLoading.value = false
                _currentAccountId.value?.let { loadRecordsByAccountId(it) }
                _currentAccountId.value?.let { calculateMonthlyStats(it) }
            } catch (e: Exception) {
                _errorMessage.value = "更新记录失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 删除记录
     */
    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteRecord(record)
                _successMessage.value = "记录已删除"
                _isLoading.value = false
                _currentAccountId.value?.let { loadRecordsByAccountId(it) }
                _currentAccountId.value?.let { calculateMonthlyStats(it) }
            } catch (e: Exception) {
                _errorMessage.value = "删除记录失败: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * 计算本月统计数据
     */
    private fun calculateMonthlyStats(accountId: Long) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val calendar = java.util.Calendar.getInstance()
                calendar.timeInMillis = now
                calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                val monthStart = calendar.timeInMillis

                val income = repository.getTotalIncome(accountId)
                val expense = repository.getTotalExpense(accountId)

                _monthlyStats.value = MonthlyStats(
                    income = income,
                    expense = expense,
                    balance = income - expense
                )
            } catch (e: Exception) {
                _errorMessage.value = "计算统计数据失败: ${e.message}"
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
     * 获取今日总支出
     */
    fun getTodayExpense(): Double {
        val today = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = today
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        val dayStart = calendar.timeInMillis

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        val dayEnd = calendar.timeInMillis

        return _records.value
            .filter { it.type == "expense" && it.date in dayStart..dayEnd }
            .sumOf { it.amount }
    }

    /**
     * 获取今日总收入
     */
    fun getTodayIncome(): Double {
        val today = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = today
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        val dayStart = calendar.timeInMillis

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        val dayEnd = calendar.timeInMillis

        return _records.value
            .filter { it.type == "income" && it.date in dayStart..dayEnd }
            .sumOf { it.amount }
    }

    /**
     * 获取记录数量
     */
    fun getRecordCount(): Int {
        return _records.value.size
    }

    /**
     * 数据类：月度统计
     */
    data class MonthlyStats(
        val income: Double = 0.0,
        val expense: Double = 0.0,
        val balance: Double = 0.0
    )
}
