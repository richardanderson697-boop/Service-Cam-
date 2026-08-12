package com.example.stream

data class StreamTelemetry(
    val isTransmitting: Boolean = true,
    val privacyShieldActive: Boolean = false,
    val privacyReason: String = "",
    val fps: Int = 60,
    val bitrateMbps: Float = 4.8f,
    val latencyMs: Int = 42,
    val bytesTransmittedMb: Double = 342.5,
    val resolution: String = "1920x1080p60",
    val encryptionType: String = "AES-256-GCM + TLS 1.3",
    val endpointUrl: String = "https://central-hq.servicecam.net/v1/telemetry",
    val hmacSignatureValid: Boolean = true,
    val totalDroppedFrames: Int = 2
)
