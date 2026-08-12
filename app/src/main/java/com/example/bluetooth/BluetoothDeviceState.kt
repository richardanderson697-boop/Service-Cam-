package com.example.bluetooth

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    STREAMING,
    PRIVACY_PAUSED
}

data class BluetoothBodyCamDevice(
    val name: String,
    val macAddress: String,
    val rssiDbm: Int = -58,
    val batteryPercent: Int = 88,
    val firmwareVersion: String = "v3.1.4-SEC",
    val isPaired: Boolean = true,
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTED
)
