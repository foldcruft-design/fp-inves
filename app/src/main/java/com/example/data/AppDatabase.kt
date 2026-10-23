package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FunPayDao {
    // Accounts
    @Query("SELECT * FROM funpay_accounts ORDER BY isActive DESC, id ASC")
    fun getAllAccounts(): Flow<List<FunPayAccountEntity>>

    @Query("SELECT * FROM funpay_accounts WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveAccount(): FunPayAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: FunPayAccountEntity): Long

    @Update
    suspend fun updateAccount(account: FunPayAccountEntity)

    @Delete
    suspend fun deleteAccount(account: FunPayAccountEntity)

    @Query("UPDATE funpay_accounts SET isActive = CASE WHEN id = :accountId THEN 1 ELSE 0 END")
    suspend fun setActiveAccount(accountId: Int)

    // Device Nodes (Multi-Device 24/7 Session Automation without limits)
    @Query("SELECT * FROM device_nodes ORDER BY id ASC")
    fun getAllDeviceNodes(): Flow<List<DeviceNodeSessionEntity>>

    @Query("SELECT * FROM device_nodes WHERE isSessionActive = 1")
    suspend fun getActiveDeviceNodes(): List<DeviceNodeSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceNode(node: DeviceNodeSessionEntity): Long

    @Update
    suspend fun updateDeviceNode(node: DeviceNodeSessionEntity)

    @Delete
    suspend fun deleteDeviceNode(node: DeviceNodeSessionEntity)

    // Lot Categories
    @Query("SELECT * FROM lot_categories ORDER BY id ASC")
    fun getAllLotCategories(): Flow<List<LotCategoryEntity>>

    @Query("SELECT * FROM lot_categories WHERE isAutoRaiseEnabled = 1")
    suspend fun getEnabledLotCategories(): List<LotCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLotCategory(category: LotCategoryEntity)

    @Update
    suspend fun updateLotCategory(category: LotCategoryEntity)

    @Delete
    suspend fun deleteLotCategory(category: LotCategoryEntity)

    // Auto-reply rules
    @Query("SELECT * FROM auto_reply_rules ORDER BY id ASC")
    fun getAllAutoReplyRules(): Flow<List<AutoReplyRuleEntity>>

    @Query("SELECT * FROM auto_reply_rules WHERE isEnabled = 1")
    suspend fun getEnabledAutoReplyRules(): List<AutoReplyRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutoReplyRule(rule: AutoReplyRuleEntity)

    @Update
    suspend fun updateAutoReplyRule(rule: AutoReplyRuleEntity)

    @Delete
    suspend fun deleteAutoReplyRule(rule: AutoReplyRuleEntity)

    // Orders
    @Query("SELECT * FROM funpay_orders ORDER BY createdAtTimestamp DESC")
    fun getAllOrders(): Flow<List<FunPayOrderEntity>>

    @Query("SELECT * FROM funpay_orders ORDER BY createdAtTimestamp DESC")
    suspend fun getAllOrdersSnapshot(): List<FunPayOrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: FunPayOrderEntity)

    @Update
    suspend fun updateOrder(order: FunPayOrderEntity)

    @Delete
    suspend fun deleteOrder(order: FunPayOrderEntity)

    // Settings
    @Query("SELECT * FROM automation_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<AutomationSettingsEntity?>

    @Query("SELECT * FROM automation_settings WHERE id = 1")
    suspend fun getSettingsSnapshot(): AutomationSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AutomationSettingsEntity)

    // Session Logs
    @Query("SELECT * FROM session_logs ORDER BY timestamp DESC LIMIT 120")
    fun getRecentLogs(): Flow<List<SessionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SessionLogEntity)

    @Query("DELETE FROM session_logs")
    suspend fun clearLogs()
}

@Database(
    entities = [
        FunPayAccountEntity::class,
        DeviceNodeSessionEntity::class,
        LotCategoryEntity::class,
        AutoReplyRuleEntity::class,
        FunPayOrderEntity::class,
        AutomationSettingsEntity::class,
        SessionLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun funPayDao(): FunPayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fp_glass_helper.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
