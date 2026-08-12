package com.example.data

import kotlinx.coroutines.flow.Flow

class ServiceCamRepository(private val dao: ServiceCamDao) {

    val allDowntimeLogs: Flow<List<DowntimeLogEntity>> = dao.getAllDowntimeLogs()
    val activeDowntimeLog: Flow<DowntimeLogEntity?> = dao.observeActiveDowntimeLog()
    val allApiKeys: Flow<List<ApiKeyEntity>> = dao.getAllApiKeys()
    val systemConfig: Flow<SystemConfigEntity?> = dao.getSystemConfig()

    suspend fun startDowntime(reason: String, note: String, locationTag: String): Long {
        val existingActive = dao.getActiveDowntimeLog()
        if (existingActive != null) {
            // Complete previous if any
            val duration = (System.currentTimeMillis() - existingActive.startTime) / 1000
            dao.updateDowntimeLog(
                existingActive.copy(
                    endTime = System.currentTimeMillis(),
                    durationSeconds = duration,
                    isCompleted = true,
                    syncStatus = "SYNCED"
                )
            )
        }

        val newLog = DowntimeLogEntity(
            reason = reason,
            note = note,
            startTime = System.currentTimeMillis(),
            isCompleted = false,
            syncStatus = "SYNCED", // Signal broadcast immediately to central home system
            locationTag = locationTag
        )
        return dao.insertDowntimeLog(newLog)
    }

    suspend fun stopDowntime(logId: Long) {
        val active = dao.getActiveDowntimeLog() ?: return
        val duration = (System.currentTimeMillis() - active.startTime) / 1000
        dao.updateDowntimeLog(
            active.copy(
                endTime = System.currentTimeMillis(),
                durationSeconds = duration,
                isCompleted = true,
                syncStatus = "SYNCED"
            )
        )
    }

    suspend fun deleteDowntimeLog(id: Long) {
        dao.deleteDowntimeLog(id)
    }

    suspend fun createApiKey(name: String, scopes: String): String {
        val keyString = "sc_live_" + java.util.UUID.randomUUID().toString().replace("-", "").take(24)
        val newKey = ApiKeyEntity(
            keyName = name,
            apiKey = keyString,
            scopes = scopes
        )
        dao.insertApiKey(newKey)
        return keyString
    }

    suspend fun toggleApiKeyRevoked(id: Long, isRevoked: Boolean) {
        dao.setApiKeyRevoked(id, isRevoked)
    }

    suspend fun incrementApiKeyUsage(key: String) {
        dao.incrementApiKeyUsage(key)
    }

    suspend fun saveConfig(config: SystemConfigEntity) {
        dao.saveSystemConfig(config)
    }

    suspend fun ensureDefaultData() {
        val currentConfig = dao.getSystemConfigOnce()
        if (currentConfig == null) {
            dao.saveSystemConfig(SystemConfigEntity())
        }

        // Add demo API key if empty
        val sampleKeys = listOf(
            ApiKeyEntity(keyName = "Dispatch Dashboard App", apiKey = "sc_live_dispatch_8f92a17b", scopes = "STREAM_READ,DOWNTIME_LOG_READ", totalRequests = 142),
            ApiKeyEntity(keyName = "HQ Security Audit System", apiKey = "sc_live_hq_audit_90184e11", scopes = "DOWNTIME_LOG_READ", totalRequests = 89)
        )
        sampleKeys.forEach { dao.insertApiKey(it) }

        // Add past completed sample downtime log for demonstration
        val pastLog = DowntimeLogEntity(
            reason = "Meal Break",
            note = "Mandatory 30-min lunch break",
            startTime = System.currentTimeMillis() - 7200000,
            endTime = System.currentTimeMillis() - 5400000,
            durationSeconds = 1800,
            isCompleted = true,
            syncStatus = "SYNCED",
            locationTag = "HQ Cafeteria Zone C"
        )
        dao.insertDowntimeLog(pastLog)
    }
}
