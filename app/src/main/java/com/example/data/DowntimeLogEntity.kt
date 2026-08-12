package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downtime_logs")
data class DowntimeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reason: String, // e.g. "Restroom Facility", "Meal Break", "Personal Break", "Emergency Off"
    val note: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val durationSeconds: Long = 0,
    val isCompleted: Boolean = false,
    val syncStatus: String = "SYNCED", // "SYNCED", "PENDING_SYNC", "FAILED"
    val centralAckCode: String = "ACK-" + ((100000..999999).random()),
    val hmacSignature: String = "",
    val locationTag: String = "Sector 4 - Gate B"
)
