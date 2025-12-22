# 项目数据库架构文档

## 项目概述
个人收支记账系统的数据库实现，基于 Android Room ORM 框架。

## 依赖配置

### 添加的依赖
在 `build.gradle.kts` 中添加了以下 Room 依赖：
- `androidx.room:room-runtime:2.6.1` - Room 运行时库
- `androidx.room:room-compiler:2.6.1` - Room 编译器（用于代码生成）
- `androidx.room:room-ktx:2.6.1` - Room Kotlin 扩展库

### Gradle 配置
- 启用 `org.jetbrains.kotlin.kapt` 插件，用于注解处理器编译

---

## 数据库结构

### 数据库名称
`personal_accounting_database`

### 数据库版本
Version 1

---

## 实体类设计

### 1. Account（账本表）
**表名：** `account`

**字段：**
| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | Long | 账本ID | 主键，自增 |
| name | String | 账本名称 | 非空 |
| balance | Double | 账本余额 | 默认0.0 |

**用途：** 存储用户的多个账本信息

---

### 2. Record（收支记录表）
**表名：** `record`

**字段：**
| 字段名 | 类型 | 说明 | 约束 |
|-------|------|------|------|
| id | Long | 记录ID | 主键，自增 |
| title | String | 标题 | 非空 |
| amount | Double | 金额 | 非空 |
| type | String | 类型 | 非空，值：income 或 expense |
| category | String | 类别 | 非空 |
| date | Long | 日期 | 非空，毫秒时间戳 |
| note | String | 备注 | 默认空字符串 |
| accountId | Long | 所属账本ID | 外键，关联 Account.id |

**外键约束：**
- 参考表：Account
- 参考字段：id
- 删除策略：CASCADE（删除账本时同时删除其所有记录）

**用途：** 存储每条收支记录

---

## 数据访问对象（DAO）设计

### 1. AccountDao 接口
**位置：** `data/dao/AccountDao.kt`

**主要方法：**
- `insertAccount()` - 插入新账本
- `updateAccount()` - 更新账本信息
- `deleteAccount()` / `deleteAccountById()` - 删除账本
- `getAccountById()` - 根据ID查询账本
- `getAllAccounts()` - 查询所有账本（返回 Flow）
- `getAllAccountsOnce()` - 查询所有账本（一次性查询）
- `getAccountByName()` - 根据名称查询账本
- `updateBalance()` - 更新账本余额
- `addBalance()` - 增加账本余额
- `subtractBalance()` - 减少账本余额

### 2. RecordDao 接口
**位置：** `data/dao/RecordDao.kt`

**主要方法：**
- `insertRecord()` - 插入新记录
- `updateRecord()` - 更新记录
- `deleteRecord()` / `deleteRecordById()` - 删除记录
- `getRecordById()` - 根据ID查询记录
- `getAllRecordsByAccountId()` - 查询账本的所有记录（Flow）
- `getAllRecordsByAccountIdOnce()` - 查询账本的所有记录（一次性）
- `getRecordsByDateRange()` - 按日期范围查询
- `getRecordsByType()` - 按类型查询（收入/支出）
- `getRecordsByCategory()` - 按类别查询
- `deleteAllRecordsByAccountId()` - 删除账本的所有记录
- `getTotalIncome()` - 获取总收入
- `getTotalExpense()` - 获取总支出

---

## 数据库实现

### AppDatabase 类
**位置：** `data/database/AppDatabase.kt`

**特点：**
- 继承 `RoomDatabase`
- 配置了 Account 和 Record 两个实体
- 实现了单例模式的线程安全获取方式
- 提供了 `getDatabase()` 静态方法获取数据库实例

**代码示例：**
```kotlin
// 获取数据库实例
val db = AppDatabase.getDatabase(context)

// 使用 DAO
db.accountDao()
db.recordDao()
```

---

## 使用示例

### 1. 创建新账本
```kotlin
viewModelScope.launch {
    val account = Account(name = "日常开支", balance = 5000.0)
    val accountId = db.accountDao().insertAccount(account)
}
```

### 2. 添加收支记录
```kotlin
viewModelScope.launch {
    val record = Record(
        title = "午餐",
        amount = 25.0,
        type = "expense",
        category = "饮食",
        date = System.currentTimeMillis(),
        note = "公司食堂",
        accountId = 1L
    )
    db.recordDao().insertRecord(record)
}
```

### 3. 查询账本记录（实时监听）
```kotlin
db.recordDao().getAllRecordsByAccountId(accountId).collect { records ->
    // 更新UI
    updateUI(records)
}
```

### 4. 获取财务统计
```kotlin
viewModelScope.launch {
    val totalIncome = db.recordDao().getTotalIncome(accountId)
    val totalExpense = db.recordDao().getTotalExpense(accountId)
}
```

---

## 测试验证

### 单元测试
**位置：** `androidTest/java/com/example/personalincomeandexpenditureaccountingsystem/DatabaseTest.kt`

**测试内容：**
- `testInsertAndQueryAccount()` - 验证账本的增删查改
- `testInsertAndQueryRecord()` - 验证记录的增删查改
- `testMultipleAccounts()` - 验证多账本管理

**运行测试命令：**
```bash
./gradlew connectedAndroidTest
```

---

## 项目目录结构
```
app/src/main/java/com/example/personalincomeandexpenditureaccountingsystem/
├── data/
│   ├── dao/
│   │   ├── AccountDao.kt
│   │   └── RecordDao.kt
│   ├── database/
│   │   └── AppDatabase.kt
│   └── entity/
│       ├── Account.kt
│       └── Record.kt
└── ...其他文件
```

---

## 后续开发计划

根据需求文档，后续可以扩展：

### 进阶功能
1. **多账本管理** - 已为此预留了 accountId 字段
2. **收支趋势图表** - 使用 RecordDao 的统计方法
3. **本地数据备份/恢复** - 扩展数据库导出功能
4. **自定义分类** - 可创建 Category 表扩展

### 可能的表扩展
- `Category` 表 - 管理用户自定义分类
- `Budget` 表 - 预算管理
- `Transfer` 表 - 账本间转账记录

---

## 数据库版本管理

当需要升级数据库版本时（修改表结构），需要：
1. 修改 `@Database` 注解中的 `version` 值
2. 在 Room 的 migration 中定义迁移脚本
3. 使用 `addMigrations()` 添加迁移规则

示例：
```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "db")
    .addMigrations(MIGRATION_1_2)
    .build()

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 数据库升级逻辑
    }
}
```

---

**数据库设计完成于：** 2025年12月22日
**团队：** 个人收支记账系统开发组
