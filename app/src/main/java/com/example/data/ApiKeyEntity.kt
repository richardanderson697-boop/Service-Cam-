package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_keys")
data class ApiKeyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyName: String,
    val apiKey: String,
    val scopes: String = "STREAM_READ,DOWNTIME_LOG_READ",
    val createdTimestamp: Long = System.currentTimeMillis(),
    val isRevoked: Boolean = false,
    val totalRequests: Int = 0
)
