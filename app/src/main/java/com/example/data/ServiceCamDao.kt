package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceCamDao {
    // Downtime Logs
    @Query("SELECT * FROM downtime_logs ORDER BY startTime DESC")
    fun getAllDowntimeLogs(): Flow<List<DowntimeLogEntity>>

    @Query("SELECT * FROM downtime_logs WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveDowntimeLog(): DowntimeLogEntity?

    @Query("SELECT * FROM downtime_logs WHERE isCompleted = 0 LIMIT 1")
    fun observeActiveDowntimeLog(): Flow<DowntimeLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDowntimeLog(log: DowntimeLogEntity): Long

    @Update
    suspend fun updateDowntimeLog(log: DowntimeLogEntity)

    @Query("DELETE FROM downtime_logs WHERE id = :id")
    suspend fun deleteDowntimeLog(id: Long)

    // API Keys
    @Query("SELECT * FROM api_keys ORDER BY createdTimestamp DESC")
    fun getAllApiKeys(): Flow<List<ApiKeyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApiKey(apiKey: ApiKeyEntity)

    @Query("UPDATE api_keys SET isRevoked = :isRevoked WHERE id = :id")
    suspend fun setApiKeyRevoked(id: Long, isRevoked: Boolean)

    @Query("UPDATE api_keys SET totalRequests = totalRequests + 1 WHERE apiKey = :key")
    suspend fun incrementApiKeyUsage(key: String)

    // System Config
    @Query("SELECT * FROM system_config WHERE id = 1 LIMIT 1")
    fun getSystemConfig(): Flow<SystemConfigEntity?>

    @Query("SELECT * FROM system_config WHERE id = 1 LIMIT 1")
    suspend fun getSystemConfigOnce(): SystemConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSystemConfig(config: SystemConfigEntity)
}
