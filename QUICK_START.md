# 数据库快速开始指南

## ✅ 已完成的工作

### 1. 依赖配置
- ✅ 添加 Room 依赖到 `gradle/libs.versions.toml`
- ✅ 配置 `app/build.gradle.kts` 中的 kapt 编译器和 Room 库

### 2. 实体类设计
- ✅ `Account.kt` - 账本表实体（包含：id, name, balance）
- ✅ `Record.kt` - 收支记录表实体（包含：id, title, amount, type, category, date, note, accountId）

### 3. 数据访问层
- ✅ `AccountDao.kt` - 账本数据操作接口
- ✅ `RecordDao.kt` - 收支记录数据操作接口

### 4. 数据库配置
- ✅ `AppDatabase.kt` - Room 数据库主类，包含单例模式实现

### 5. 测试验证
- ✅ `DatabaseTest.kt` - 集成测试用例

---

## 📦 项目结构

```
app/src/main/java/com/example/personalincomeandexpenditureaccountingsystem/
└── data/
    ├── dao/
    │   ├── AccountDao.kt          # 账本数据操作接口
    │   └── RecordDao.kt           # 收支记录数据操作接口
    ├── database/
    │   └── AppDatabase.kt         # Room 数据库实现
    └── entity/
        ├── Account.kt             # 账本实体类
        └── Record.kt              # 收支记录实体类
```

---

## 🚀 使用方法

### 初始化数据库

在 Application 或首次使用时：

```kotlin
import com.example.personalincomeandexpenditureaccountingsystem.data.database.AppDatabase

// 获取数据库实例
val db = AppDatabase.getDatabase(context)
```

### 常见操作示例

#### 创建新账本

```kotlin
viewModelScope.launch {
    val account = Account(
        name = "我的钱包",
        balance = 5000.0
    )
    val accountId = db.accountDao().insertAccount(account)
    Log.d("Account", "新建账本ID: $accountId")
}
```

#### 添加支出记录

```kotlin
viewModelScope.launch {
    val record = Record(
        title = "买菜",
        amount = 45.5,
        type = "expense",        // "expense" 表示支出
        category = "饮食",
        date = System.currentTimeMillis(),
        note = "菜市场购物",
        accountId = 1L           // 所属账本ID
    )
    val recordId = db.recordDao().insertRecord(record)
    Log.d("Record", "新建记录ID: $recordId")
}
```

#### 添加收入记录

```kotlin
viewModelScope.launch {
    val record = Record(
        title = "工资",
        amount = 8000.0,
        type = "income",         // "income" 表示收入
        category = "薪资",
        date = System.currentTimeMillis(),
        note = "12月工资",
        accountId = 1L
    )
    db.recordDao().insertRecord(record)
}
```

#### 监听账本记录变化（实时更新UI）

```kotlin
viewModelScope.launch {
    db.recordDao().getAllRecordsByAccountId(accountId).collect { records ->
        // records 发生变化时自动更新UI
        updateRecordList(records)
    }
}
```

#### 查询财务统计

```kotlin
viewModelScope.launch {
    val accountId = 1L
    val totalIncome = db.recordDao().getTotalIncome(accountId)
    val totalExpense = db.recordDao().getTotalExpense(accountId)
    val balance = totalIncome - totalExpense
    
    Log.d("Statistics", "收入: $totalIncome, 支出: $totalExpense, 结余: $balance")
}
```

#### 查询日期范围内的记录

```kotlin
viewModelScope.launch {
    val startOfMonth = // 本月1日的时间戳
    val endOfMonth = // 本月最后一日的时间戳
    
    val records = db.recordDao().getRecordsByDateRange(
        accountId = 1L,
        startDate = startOfMonth,
        endDate = endOfMonth
    )
    Log.d("Records", "本月记录数: ${records.size}")
}
```

#### 更新记录

```kotlin
viewModelScope.launch {
    val record = db.recordDao().getRecordById(recordId)
    if (record != null) {
        val updatedRecord = record.copy(amount = 50.0)
        db.recordDao().updateRecord(updatedRecord)
    }
}
```

#### 删除记录

```kotlin
viewModelScope.launch {
    db.recordDao().deleteRecordById(recordId)
}
```

---

## 🧪 运行测试

### 连接设备或启动模拟器，然后运行：

```bash
./gradlew connectedAndroidTest
```

### 或在 Android Studio 中：
1. 右键点击 `DatabaseTest.kt` 文件
2. 选择 "Run 'DatabaseTest'" 

### 测试包括：
- ✅ 账本的增删查改
- ✅ 记录的增删查改  
- ✅ 多账本管理验证

---

## 📊 数据库架构总结

### Account 表（账本）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| name | String | 账本名称 |
| balance | Double | 账本余额 |

### Record 表（收支记录）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| title | String | 标题 |
| amount | Double | 金额 |
| type | String | 类型（income/expense） |
| category | String | 类别 |
| date | Long | 日期时间戳 |
| note | String | 备注 |
| accountId | Long | 所属账本ID（外键） |

---

## 🔗 相关文件

- 详细设计文档：[DATABASE_DESIGN.md](./DATABASE_DESIGN.md)
- 单元测试文件：`app/src/androidTest/java/.../DatabaseTest.kt`
- 数据访问接口：`app/src/main/java/.../data/dao/`
- 实体类定义：`app/src/main/java/.../data/entity/`

---

## 💡 提示

1. **线程安全**：所有数据库操作都应在协程中进行（使用 `suspend` 函数）
2. **Flow 监听**：使用 Flow 返回的方法可以实时监听数据变化
3. **日期存储**：使用 `System.currentTimeMillis()` 获取当前时间戳（毫秒）
4. **外键约束**：删除账本时会自动删除其所有记录

---

**项目状态：** ✅ 数据库完全就绪，可以开始开发 UI 和业务逻辑
