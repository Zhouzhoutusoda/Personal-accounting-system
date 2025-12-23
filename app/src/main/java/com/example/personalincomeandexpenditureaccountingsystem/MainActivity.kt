package com.example.personalincomeandexpenditureaccountingsystem

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalincomeandexpenditureaccountingsystem.data.database.AppDatabase
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Account
import com.example.personalincomeandexpenditureaccountingsystem.data.repository.AccountRepository
import com.example.personalincomeandexpenditureaccountingsystem.ui.adapter.AccountCardAdapter
import com.example.personalincomeandexpenditureaccountingsystem.ui.viewmodel.AccountViewModel
import com.example.personalincomeandexpenditureaccountingsystem.ui.viewmodel.AccountViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var accountViewModel: AccountViewModel
    private lateinit var accountCardAdapter: AccountCardAdapter
    private lateinit var containerAccounts: LinearLayout
    private lateinit var btnAddAccount: Button
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: android.widget.ScrollView
    private lateinit var emptyState: LinearLayout

    // 统计 UI
    private lateinit var tvTodayExpense: TextView
    private lateinit var tvTodayIncome: TextView
    private lateinit var tvMonthIncome: TextView
    private lateinit var tvMonthExpense: TextView
    private lateinit var tvMonthBalance: TextView
    private lateinit var tvTotalBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        setupViewModel()
        setupObservers()
        setupClickListeners()
    }

    private fun initUI() {
        containerAccounts = findViewById(R.id.container_accounts)
        btnAddAccount = findViewById(R.id.btn_add_account)
        bottomNav = findViewById(R.id.bottom_nav)
        progressBar = findViewById(R.id.progress_bar)
        scrollView = findViewById(R.id.scroll_view)
        emptyState = findViewById(R.id.empty_state)

        tvTodayExpense = findViewById(R.id.tv_today_expense)
        tvTodayIncome = findViewById(R.id.tv_today_income)
        tvMonthIncome = findViewById(R.id.tv_month_income)
        tvMonthExpense = findViewById(R.id.tv_month_expense)
        tvMonthBalance = findViewById(R.id.tv_month_balance)
        tvTotalBalance = findViewById(R.id.tv_total_balance)
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val repository = AccountRepository(database.accountDao())
        val factory = AccountViewModelFactory(repository)
        accountViewModel = ViewModelProvider(this, factory).get(AccountViewModel::class.java)

        accountCardAdapter = AccountCardAdapter(
            onItemClick = { account ->
                // 查看详情
                Toast.makeText(this, "查看账本: ${account.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { account ->
                // 编辑账本
                Toast.makeText(this, "编辑账本: ${account.name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { account ->
                // 删除账本
                showDeleteConfirmation(account)
            }
        )
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            accountViewModel.accounts.collect { accounts ->
                updateUI(accounts)
            }
        }

        lifecycleScope.launch {
            accountViewModel.isLoading.collect { isLoading ->
                progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
            }
        }

        lifecycleScope.launch {
            accountViewModel.errorMessage.collect { error ->
                if (error != null) {
                    Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
                    accountViewModel.clearErrorMessage()
                }
            }
        }

        lifecycleScope.launch {
            accountViewModel.successMessage.collect { success ->
                if (success != null) {
                    Toast.makeText(this@MainActivity, success, Toast.LENGTH_SHORT).show()
                    accountViewModel.clearSuccessMessage()
                }
            }
        }
    }

    private fun updateUI(accounts: List<Account>) {
        containerAccounts.removeAllViews()

        if (accounts.isEmpty()) {
            scrollView.visibility = android.widget.FrameLayout.GONE
            emptyState.visibility = android.widget.FrameLayout.VISIBLE
        } else {
            scrollView.visibility = android.widget.FrameLayout.VISIBLE
            emptyState.visibility = android.widget.FrameLayout.GONE

            // 添加账本卡片
            for (account in accounts) {
                val cardView = layoutInflater.inflate(R.layout.item_account, containerAccounts, false)
                bindAccountCard(cardView, account)
                containerAccounts.addView(cardView)
            }
        }

        // 更新统计信息
        updateStatistics(accounts)
    }

    private fun bindAccountCard(cardView: android.view.View, account: Account) {
        cardView.findViewById<TextView>(R.id.tv_account_name).text = account.name
        cardView.findViewById<TextView>(R.id.tv_balance).text = String.format("¥%.2f", account.balance)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val createdDate = dateFormat.format(java.util.Date(System.currentTimeMillis()))
        cardView.findViewById<TextView>(R.id.tv_created_date).text = "创建于 $createdDate"

        // TODO: 获取月度统计数据并显示
        cardView.findViewById<TextView>(R.id.tv_month_income).text = "¥0.00"
        cardView.findViewById<TextView>(R.id.tv_month_expense).text = "¥0.00"

        // 设置点击监听
        cardView.setOnClickListener {
            Toast.makeText(this, "进入账本详情: ${account.name}", Toast.LENGTH_SHORT).show()
        }

        cardView.findViewById<Button>(R.id.btn_view_details).setOnClickListener {
            Toast.makeText(this, "查看账本详情: ${account.name}", Toast.LENGTH_SHORT).show()
        }

        cardView.findViewById<android.widget.ImageButton>(R.id.btn_edit).setOnClickListener {
            Toast.makeText(this, "编辑账本: ${account.name}", Toast.LENGTH_SHORT).show()
        }

        cardView.findViewById<android.widget.ImageButton>(R.id.btn_delete).setOnClickListener {
            showDeleteConfirmation(account)
        }
    }

    private fun updateStatistics(accounts: List<Account>) {
        val totalBalance = accountViewModel.getTotalBalance()
        tvTotalBalance.text = String.format("¥%.2f", totalBalance)

        // TODO: 计算今日和本月统计
        tvTodayExpense.text = "¥0.00"
        tvTodayIncome.text = "¥0.00"
        tvMonthIncome.text = "¥0.00"
        tvMonthExpense.text = "¥0.00"
        tvMonthBalance.text = "¥0.00"
    }

    private fun setupClickListeners() {
        btnAddAccount.setOnClickListener {
            Toast.makeText(this, "新增账本", Toast.LENGTH_SHORT).show()
        }

        val btnQuickExpense = findViewById<Button>(R.id.btn_quick_expense)
        btnQuickExpense.setOnClickListener {
            Toast.makeText(this, "快速记录支出", Toast.LENGTH_SHORT).show()
        }

        val btnQuickIncome = findViewById<Button>(R.id.btn_quick_income)
        btnQuickIncome.setOnClickListener {
            Toast.makeText(this, "快速记录收入", Toast.LENGTH_SHORT).show()
        }

        val btnSettings = findViewById<android.widget.ImageButton>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            Toast.makeText(this, "打开设置", Toast.LENGTH_SHORT).show()
        }

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "首页", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_records -> {
                    Toast.makeText(this, "记录列表", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_statistics -> {
                    Toast.makeText(this, "统计分析", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_home
    }

    private fun showDeleteConfirmation(account: Account) {
        android.app.AlertDialog.Builder(this)
            .setTitle("删除账本")
            .setMessage("确定要删除账本 \"${account.name}\" 吗？删除后其中的所有记录也会被删除。")
            .setPositiveButton("确定") { _, _ ->
                accountViewModel.deleteAccount(account)
            }
            .setNegativeButton("取消", null)
            .show()
    }
}