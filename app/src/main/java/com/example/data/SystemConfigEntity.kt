package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_config")
data class SystemConfigEntity(
    @PrimaryKey val id: Int = 1,
    val homeEndpointUrl: String = "https://central-hq.servicecam.net/v1/telemetry",
    val hmacSecret: String = "sk_live_aes256_990184758291039485",
    val unitIdentifier: String = "BODYCAM-UNIT-8092",
    val encryptionStandard: String = "AES-256-GCM + TLS 1.3",
    val lowLatencyTargetMs: Int = 45,
    val autoSyncDowntime: Boolean = true,
    val streamResolution: String = "1080p @ 60 FPS"
)
