package com.example.personalincomeandexpenditureaccountingsystem

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_simple)

        try {
            // 简单初始化
            val titleView: TextView = findViewById(R.id.tv_title)
            titleView.text = "📱 个人记账 App"
            
            val balanceView: TextView = findViewById(R.id.tv_total_balance)
            balanceView.text = "¥1000.00"
            
            val todayIncomeView: TextView = findViewById(R.id.tv_today_income)
            todayIncomeView.text = "¥500.00"
            
            val todayExpenseView: TextView = findViewById(R.id.tv_today_expense)
            todayExpenseView.text = "¥200.00"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}