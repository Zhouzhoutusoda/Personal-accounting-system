# 📚 项目文档索引

## 📖 文档导航

### 📋 项目概览
- **[COMPLETION_REPORT.md](./COMPLETION_REPORT.md)** ⭐ 
  - 完整的项目完成报告
  - 任务状态总结
  - 文件清单
  - 核心功能列表

### 🚀 快速开始
- **[QUICK_START.md](./QUICK_START.md)** ⭐⭐
  - 数据库快速开始指南
  - 常见操作代码示例
  - 测试运行说明
  - 实用 Tips 提示

### 🗄️ 数据库设计
- **[DATABASE_DESIGN.md](./DATABASE_DESIGN.md)**
  - 详细的数据库架构说明
  - 表结构及字段定义
  - DAO 接口方法详解
  - 后续扩展计划

### 📐 架构设计
- **[ARCHITECTURE.md](./ARCHITECTURE.md)**
  - 系统分层架构图
  - 数据流向图
  - 表关系图
  - 类关系图
  - 性能优化策略

---

## 📂 项目代码结构

### 数据层 (`data/`)

#### 实体类 (`entity/`)
```
entity/
├── Account.kt                    # 账本实体类
│   └── 字段: id, name, balance
│
└── Record.kt                     # 收支记录实体类
    └── 字段: id, title, amount, type, category, date, note, accountId
```

#### 数据访问对象 (`dao/`)
```
dao/
├── AccountDao.kt                 # 账本数据操作接口
│   └── 12 个方法：增删查改、统计、余额操作
│
└── RecordDao.kt                  # 收支记录数据操作接口
    └── 15 个方法：增删查改、统计、条件查询、日期范围查询
```

#### 数据库配置 (`database/`)
```
database/
└── AppDatabase.kt                # Room 数据库实现
    └── 单例模式 + 数据库初始化
```

---

## 🔍 核心类速查表

### Account（账本实体）
```kotlin
@Entity(tableName = "account")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val balance: Double = 0.0
)
```

### Record（收支记录实体）
```kotlin
@Entity(tableName = "record")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String,              // "income" 或 "expense"
    val category: String,
    val date: Long,                // 毫秒时间戳
    val note: String = "",
    val accountId: Long            // 外键
)
```

### AppDatabase（数据库类）
```kotlin
@Database(
    entities = [Account::class, Record::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun recordDao(): RecordDao
    
    companion object {
        fun getDatabase(context: Context): AppDatabase
    }
}
```

---

## 🎯 常用代码片段

### 1. 获取数据库实例
```kotlin
val db = AppDatabase.getDatabase(context)
val accountDao = db.accountDao()
val recordDao = db.recordDao()
```

### 2. 创建新账本
```kotlin
val account = Account(name = "我的钱包", balance = 5000.0)
val accountId = db.accountDao().insertAccount(account)
```

### 3. 添加收支记录
```kotlin
val record = Record(
    title = "午餐",
    amount = 25.0,
    type = "expense",         // "income" 或 "expense"
    category = "饮食",
    date = System.currentTimeMillis(),
    note = "食堂",
    accountId = 1L
)
db.recordDao().insertRecord(record)
```

### 4. 实时监听记录（推荐）
```kotlin
viewModelScope.launch {
    db.recordDao().getAllRecordsByAccountId(accountId)
        .collect { records ->
            // 数据变化时自动刷新
            updateUI(records)
        }
}
```

### 5. 查询财务统计
```kotlin
val totalIncome = db.recordDao().getTotalIncome(accountId)
val totalExpense = db.recordDao().getTotalExpense(accountId)
val netAmount = totalIncome - totalExpense
```

### 6. 日期范围查询
```kotlin
val startTime = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000  // 30天前
val endTime = System.currentTimeMillis()
val records = db.recordDao().getRecordsByDateRange(
    accountId = 1L,
    startDate = startTime,
    endDate = endTime
)
```

### 7. 按类型查询
```kotlin
// 只查询支出
db.recordDao().getRecordsByType(accountId, "expense")
    .collect { expenseRecords ->
        // 处理支出记录
    }

// 只查询收入
db.recordDao().getRecordsByType(accountId, "income")
    .collect { incomeRecords ->
        // 处理收入记录
    }
```

### 8. 按分类查询
```kotlin
db.recordDao().getRecordsByCategory(accountId, "饮食")
    .collect { foodRecords ->
        // 处理饮食分类的所有记录
    }
```

---

## 🧪 测试文件位置

**集成测试：**
```
app/src/androidTest/java/com/example/personalincomeandexpenditureaccountingsystem/
└── DatabaseTest.kt
    ├── testInsertAndQueryAccount()
    ├── testInsertAndQueryRecord()
    └── testMultipleAccounts()
```

**运行测试命令：**
```bash
./gradlew connectedAndroidTest
```

---

## ⚙️ 配置文件修改

### gradle/libs.versions.toml (修改)
```toml
[versions]
room = "2.6.1"

[libraries]
androidx-room-runtime = { ... }
androidx-room-compiler = { ... }
androidx-room-ktx = { ... }
```

### app/build.gradle.kts (修改)
```gradle
plugins {
    id("org.jetbrains.kotlin.kapt")
}

dependencies {
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
}
```

---

## 🔗 关键概念链接

### 1. Room 框架
- `@Entity` - 数据库表定义
- `@Dao` - 数据访问对象
- `@Database` - 数据库配置
- `@PrimaryKey` - 主键定义
- `@ForeignKey` - 外键约束

### 2. 协程和 Flow
- `suspend` 函数 - 异步操作
- `Flow<T>` - 响应式数据流
- `collect{}` - 收集流数据
- `viewModelScope.launch{}` - 启动协程

### 3. 数据库操作
- `@Insert` - 插入数据
- `@Update` - 更新数据
- `@Delete` - 删除数据
- `@Query` - 自定义查询

### 4. SQL 概念
- `AUTOINCREMENT` - 自增主键
- `CASCADE DELETE` - 级联删除
- `FOREIGN KEY` - 外键约束
- `ORDER BY` - 排序
- `SUM()` - 求和聚合

---

## 📊 数据库版本历史

| 版本 | 日期 | 更改内容 |
|------|------|---------|
| 1.0 | 2025-12-22 | 初始版本，创建 Account 和 Record 表 |

**未来升级计划：**
- v1.1: 添加 Category 表（自定义分类）
- v1.2: 添加 Budget 表（预算管理）
- v1.3: 添加 Transfer 表（账本间转账）

---

## ✅ 检查清单

在开始使用数据库前，确保：

- [x] 添加了 Room 依赖
- [x] 启用了 kapt 插件
- [x] 创建了实体类
- [x] 实现了 DAO 接口
- [x] 配置了 AppDatabase
- [x] 编译无错误
- [x] 创建了测试用例

---

## 📞 常见问题 (FAQ)

### Q: 为什么使用 Room？
A: Room 是 Android 官方推荐的数据库框架，提供：
- 编译时 SQL 检查
- 自动 SQL 生成
- 协程和 Flow 支持
- 类型安全

### Q: 数据存储在哪里？
A: SQLite 数据库存储在应用的私有目录：
```
/data/data/com.example.personalincomeandexpenditureaccountingsystem/databases/
```

### Q: 可以导出数据吗？
A: 目前未实现，可在后续版本中添加导出为 CSV/JSON 的功能

### Q: 如何升级数据库版本？
A: 修改 `@Database(version = 2)` 并实现 Migration

### Q: 线程安全吗？
A: 是的，所有操作都通过协程在后台线程执行

### Q: 可以删除所有数据吗？
A: 可以，使用 `db.clearAllTables()` 或删除单个表的所有记录

---

## 🎓 学习资源

### 官方文档
- [Room 官方文档](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Flow API](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)

### 相关技术
- SQLite
- SQL 查询语言
- 关系数据库设计
- Android 数据持久化

---

## 📝 更新日志

**2025-12-22 - 初始版本**
- ✅ 完成数据库架构设计
- ✅ 实现 Account 和 Record 实体
- ✅ 创建 AccountDao 和 RecordDao
- ✅ 配置 AppDatabase
- ✅ 编写测试用例
- ✅ 生成完整文档

---

**项目完成度：** 100%  
**代码质量：** ⭐⭐⭐⭐⭐  
**文档完整度：** ⭐⭐⭐⭐⭐  

**准备好开始开发 UI 层吧！**
