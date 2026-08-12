package com.example.api

import com.example.data.ApiKeyEntity
import com.example.data.DowntimeLogEntity
import com.example.stream.StreamTelemetry

data class ApiResponse(
    val statusCode: Int,
    val success: Boolean,
    val endpoint: String,
    val message: String,
    val rawJsonData: String
)

class ApiGatewayController {

    fun executeApiCall(
        endpoint: String,
        apiKeyInput: String,
        registeredKeys: List<ApiKeyEntity>,
        activeTelemetry: StreamTelemetry,
        downtimeLogs: List<DowntimeLogEntity>,
        onTogglePrivacyRemote: ((Boolean, String) -> Unit)? = null
    ): ApiResponse {
        val matchingKey = registeredKeys.find { it.apiKey == apiKeyInput.trim() && !it.isRevoked }
        if (matchingKey == null) {
            return ApiResponse(
                statusCode = 401,
                success = false,
                endpoint = endpoint,
                message = "401 Unauthorized: Invalid or revoked API key",
                rawJsonData = """
                {
                  "error": "UNAUTHORIZED",
                  "message": "Invalid API key or key has been revoked by ServiceCam administrator",
                  "code": 401
                }
                """.trimIndent()
            )
        }

        return when (endpoint) {
            "GET /api/v1/status" -> {
                ApiResponse(
                    statusCode = 200,
                    success = true,
                    endpoint = endpoint,
                    message = "200 OK - System & Stream Status",
                    rawJsonData = """
                    {
                      "service": "ServiceCam Body Cam API",
                      "version": "v1.4.2",
                      "unit_id": "BODYCAM-UNIT-8092",
                      "is_streaming": ${activeTelemetry.isTransmitting && !activeTelemetry.privacyShieldActive},
                      "privacy_downtime_active": ${activeTelemetry.privacyShieldActive},
                      "downtime_reason": "${activeTelemetry.privacyReason}",
                      "encryption": "${activeTelemetry.encryptionType}",
                      "security_level": "HIGH_SECURITY_GCM",
                      "latency_ms": ${activeTelemetry.latencyMs},
                      "fps": ${activeTelemetry.fps}
                    }
                    """.trimIndent()
                )
            }
            "GET /api/v1/downtime-logs" -> {
                val logsJson = downtimeLogs.take(5).joinToString(",\n") { log ->
                    """    {
      "id": ${log.id},
      "reason": "${log.reason}",
      "start_time": ${log.startTime},
      "end_time": ${log.endTime ?: "null"},
      "duration_sec": ${log.durationSeconds},
      "completed": ${log.isCompleted},
      "sync_status": "${log.syncStatus}",
      "central_ack_code": "${log.centralAckCode}",
      "location": "${log.locationTag}"
    }"""
                }
                ApiResponse(
                    statusCode = 200,
                    success = true,
                    endpoint = endpoint,
                    message = "200 OK - Downtime Audit Logs",
                    rawJsonData = "[\n$logsJson\n]"
                )
            }
            "POST /api/v1/privacy/toggle" -> {
                val newPrivacy = !activeTelemetry.privacyShieldActive
                onTogglePrivacyRemote?.invoke(newPrivacy, "API Remote Override")
                ApiResponse(
                    statusCode = 200,
                    success = true,
                    endpoint = endpoint,
                    message = "200 OK - Privacy State Toggled",
                    rawJsonData = """
                    {
                      "action": "TOGGLE_PRIVACY",
                      "new_state": ${if (newPrivacy) "\"ACTIVE_BREAK\"" else "\"LIVE_RECORDING\""},
                      "signaled_to_home_system": true,
                      "timestamp": ${System.currentTimeMillis()},
                      "hmac_sha256": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
                    }
                    """.trimIndent()
                )
            }
            "GET /api/v1/stream/telemetry" -> {
                ApiResponse(
                    statusCode = 200,
                    success = true,
                    endpoint = endpoint,
                    message = "200 OK - Real-time Stream Telemetry",
                    rawJsonData = """
                    {
                      "fps": ${activeTelemetry.fps},
                      "bitrate_mbps": ${activeTelemetry.bitrateMbps},
                      "latency_ms": ${activeTelemetry.latencyMs},
                      "bytes_transmitted_mb": ${activeTelemetry.bytesTransmittedMb},
                      "resolution": "${activeTelemetry.resolution}",
                      "encryption_standard": "${activeTelemetry.encryptionType}",
                      "endpoint_url": "${activeTelemetry.endpointUrl}",
                      "hmac_valid": ${activeTelemetry.hmacSignatureValid}
                    }
                    """.trimIndent()
                )
            }
            else -> {
                ApiResponse(
                    statusCode = 404,
                    success = false,
                    endpoint = endpoint,
                    message = "404 Endpoint Not Found",
                    rawJsonData = """
                    {
                      "error": "NOT_FOUND",
                      "message": "Endpoint $endpoint does not exist"
                    }
                    """.trimIndent()
                )
            }
        }
    }
}
