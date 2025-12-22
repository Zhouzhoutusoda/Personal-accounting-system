# 📐 数据库架构图解

## 系统分层架构

```
┌─────────────────────────────────────────────┐
│          UI Layer (Compose)                 │
│    (MainActivity, RecordScreen, etc.)       │
└────────────────┬────────────────────────────┘
                 │ 协程 Flow
                 ▼
┌─────────────────────────────────────────────┐
│       ViewModel / Repository Layer          │
│    (业务逻辑和数据访问层)                    │
└────────────────┬────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────┐
│          DAO Layer (数据访问对象)             │
│  ┌──────────────────┐  ┌──────────────────┐ │
│  │   AccountDao     │  │   RecordDao      │ │
│  │   (12 方法)       │  │   (15 方法)       │ │
│  └────────┬─────────┘  └────────┬─────────┘ │
└───────────┼──────────────────────┼──────────┘
            │        Room ORM      │
            ▼                      ▼
┌──────────────────────────────────────────────┐
│      SQLite Database                         │
│  ┌──────────────┐    ┌──────────────┐        │
│  │   account    │    │    record    │        │
│  │   表          │    │    表         │        │
│  │  (id,name,   │    │ (id,title,   │        │
│  │  balance)    │    │ amount,type, │        │
│  │              │    │ category,    │        │
│  │              │    │ date,note,   │        │
│  │              │    │ accountId)   │        │
│  └──────────────┘    └──────────────┘        │
│        ▲                      │               │
│        └──── 外键关联(1:N) ─────┘             │
└──────────────────────────────────────────────┘
```

---

## 数据流向图

### 创建账本的数据流
```
用户输入账本名称
        │
        ▼
ViewModel.createAccount()
        │
        ▼
Repository.insertAccount(account)
        │
        ▼
AppDatabase.getDatabase()
        │
        ▼
accountDao.insertAccount(account)
        │
        ▼
SQLite: INSERT INTO account (name, balance) VALUES (?, ?)
        │
        ▼
返回新账本 ID
        │
        ▼
更新 UI (显示新账本)
```

### 实时监听账本记录的数据流
```
UI 订阅: recordDao.getAllRecordsByAccountId(accountId)
        │
        ▼
返回 Flow<List<Record>>
        │
        ▼
数据库监听数据变化 (SQLite)
        │
        ▼
任何 INSERT/UPDATE/DELETE 操作触发
        │
        ▼
Flow 发出新的记录列表
        │
        ▼
UI 自动更新 (recompose)
```

---

## 表关系图

```
┌─────────────────────────────┐
│      account (账本表)        │
├─────────────────────────────┤
│ PK id: Long (自增)          │
│ name: String                │
│ balance: Double             │
└──────────────┬──────────────┘
               │ 1
               │
        One-to-Many
               │
               │ N
┌──────────────▼──────────────┐
│    record (收支记录表)        │
├─────────────────────────────┤
│ PK id: Long (自增)          │
│ title: String               │
│ amount: Double              │
│ type: String                │
│ category: String            │
│ date: Long                  │
│ note: String                │
│ FK accountId: Long ─────┐   │
└─────────────────────────┼───┘
                          │
                          └─→ 外键关联到 account.id
                              删除策略: CASCADE
```

---

## 类关系图

```
┌─────────────────────────────────────────────────┐
│            RoomDatabase (Android)               │
│                                                 │
│  AppDatabase extends RoomDatabase              │
│  ├── @Database(entities = [Account, Record])   │
│  ├── abstract fun accountDao(): AccountDao     │
│  ├── abstract fun recordDao(): RecordDao       │
│  └── companion object                          │
│       └── fun getDatabase(): AppDatabase       │
│           (单例模式)                             │
└─────────────────────────────────────────────────┘
         ▲                           ▲
         │                           │
    提供DAO          
         │                           │
         ▼                           ▼
┌─────────────────┐      ┌──────────────────────┐
│  AccountDao     │      │   RecordDao          │
├─────────────────┤      ├──────────────────────┤
│ @Dao            │      │ @Dao                 │
├─────────────────┤      ├──────────────────────┤
│ insertAccount   │      │ insertRecord         │
│ updateAccount   │      │ updateRecord         │
│ deleteAccount   │      │ deleteRecord         │
│ getAccountById  │      │ getRecordById        │
│ getAllAccounts  │      │ getAllRecordsByAccId │
│ getByName       │      │ getByDateRange       │
│ updateBalance   │      │ getByType            │
│ addBalance      │      │ getByCategory        │
│ subtractBalance │      │ getTotalIncome       │
│ ...             │      │ getTotalExpense      │
└─────────────────┘      │ ...                  │
         ▲               └──────────────────────┘
         │                           ▲
         │   直接生成 SQL 语句          │
         │                           │
         ▼                           ▼
┌─────────────────────────────────────────────────┐
│         SQLite Database (SQLite)                │
│                                                 │
│  使用 @Insert, @Update, @Delete, @Query 注解   │
│  自动生成 SQL 语句                              │
└─────────────────────────────────────────────────┘
```

---

## 数据库操作矩阵

```
┌────────────┬──────────────┬──────────────┬──────────────┐
│  操作类型   │   Account    │   Record     │    说明       │
├────────────┼──────────────┼──────────────┼──────────────┤
│ 新增 (C)   │ insertAccount│ insertRecord │ 返回新 ID    │
│ 读取 (R)   │ getById      │ getById      │ 支持 Flow   │
│          │ getAllOnce   │ getAllByAccId│ 实时监听     │
│          │ getByName    │ getByType    │            │
│          │              │ getByCategory│            │
│ 修改 (U)   │ updateAccount│ updateRecord │ 更新整行    │
│          │ updateBalance│              │ 仅更新金额   │
│          │ addBalance   │              │            │
│          │ subtractBal  │              │            │
│ 删除 (D)   │ deleteAccount│ deleteRecord │ 支持级联   │
│          │ deleteById   │ deleteById   │ 删除                │
└────────────┴──────────────┴──────────────┴──────────────┘
```

---

## 协程和 Flow 数据流

```
UI 层
┌─────────────────────────────────┐
│ Composable Function              │
│ LaunchedEffect {                 │
│    recordDao.getAllRecords()      │
│        .collect { records ->      │
│            setState(records)      │  │
│        }                          │  │ 收集数据流
│ }                                │  │
└──────────────┬────────────────────┘  │
               │                        │
               ▼                        │
Repository/ViewModel 层
┌──────────────────────────────────┐   │
│ suspend fun getAllRecords()       │   │
│   = recordDao.getAllRecords()     │   │
│     .map { transform() }          │   │
└──────────────┬───────────────────┘    │
               │                        │
               ▼                        │
DAO 层
┌──────────────────────────────────┐   │
│ @Query("SELECT * FROM record")   │   │
│ fun getAllRecords(): Flow<List>  │◄──┘
└──────────────┬───────────────────┘
               │
               ▼
SQLite 数据库
┌──────────────────────────────────┐
│ SELECT * FROM record              │
│ 监听表变化                          │
│ 每次数据变化发出新的 Flow 值        │
└──────────────────────────────────┘
```

---

## 单例模式获取数据库

```
应用启动
    │
    ▼
AppDatabase.getDatabase(context)
    │
    ├─ INSTANCE == null?
    │   │
    │   ├─ Yes ──► synchronized(this) {
    │   │             val instance = 
    │   │               Room.databaseBuilder(...)
    │   │                   .build()
    │   │             INSTANCE = instance
    │   │             return instance
    │   │           }
    │   │
    │   └─ No  ──► return INSTANCE
    │
    ▼
全局唯一数据库实例
(线程安全)
```

---

## 事务和约束关系

```
账本 (Account)
     │
     ├─ 新增
     │   └─ 账本 ID 自增生成
     │
     ├─ 删除 ──► CASCADE DELETE
     │           │
     │           └─ 自动删除所有关联的 Record
     │
     └─ 更新余额
         └─ 原子操作（事务）
             ├─ UPDATE account SET balance = ...
             └─ 保证数据一致性

收支记录 (Record)
     │
     ├─ 新增
     │   ├─ accountId 必须存在
     │   ├─ type: "income" 或 "expense"
     │   └─ date 存储毫秒时间戳
     │
     ├─ 删除
     │   └─ 不影响 Account（无外键指向）
     │
     └─ 修改
         └─ 可修改任意字段
             (包括所属账本)
```

---

## 性能优化

### 查询优化
```
getAllRecordsByAccountId(accountId)
    │
    ├─ SQL: SELECT * FROM record
    │        WHERE accountId = ?
    │        ORDER BY date DESC
    │
    └─ 优点：
       • 只查询指定账本的记录
       • 按日期降序排列
       • 返回 Flow 支持实时监听
       • 自动处理分页
```

### 统计查询优化
```
getTotalIncome(accountId)
    │
    ├─ SQL: SELECT SUM(amount)
    │        FROM record
    │        WHERE accountId = ? AND type = 'income'
    │
    └─ 优点：
       • 数据库层面计算
       • 避免客户端 O(n) 循环
       • COALESCE(...)
         处理 NULL 值
```

---

**架构设计完全符合 Android 最佳实践！**
