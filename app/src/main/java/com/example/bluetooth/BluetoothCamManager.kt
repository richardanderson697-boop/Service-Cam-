package com.example.bluetooth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BluetoothCamManager(private val externalScope: CoroutineScope) {

    val availableDevices = listOf(
        BluetoothBodyCamDevice("ServiceCam Tactical X100", "00:1B:44:11:3A:B7", rssiDbm = -54, batteryPercent = 92),
        BluetoothBodyCamDevice("BodyCam Pro Wireless-04", "00:1B:44:88:2C:D9", rssiDbm = -68, batteryPercent = 74),
        BluetoothBodyCamDevice("Wearable HD-Cam Sentinel", "00:1B:44:99:11:E2", rssiDbm = -82, batteryPercent = 45)
    )

    private val _selectedDevice = MutableStateFlow(availableDevices[0])
    val selectedDevice: StateFlow<BluetoothBodyCamDevice> = _selectedDevice.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    init {
        // Continuous telemetry loop for RSSI and battery updates
        externalScope.launch(Dispatchers.Default) {
            while (true) {
                delay(3000)
                val current = _selectedDevice.value
                if (current.connectionStatus == ConnectionStatus.CONNECTED || current.connectionStatus == ConnectionStatus.STREAMING) {
                    val newRssi = (-70..-45).random()
                    _selectedDevice.value = current.copy(rssiDbm = newRssi)
                }
            }
        }
    }

    fun selectDevice(device: BluetoothBodyCamDevice) {
        _selectedDevice.value = device.copy(connectionStatus = ConnectionStatus.CONNECTING)
        externalScope.launch {
            delay(1200)
            _selectedDevice.value = device.copy(connectionStatus = ConnectionStatus.CONNECTED)
        }
    }

    fun startScan() {
        _isScanning.value = true
        externalScope.launch {
            delay(2500)
            _isScanning.value = false
        }
    }

    fun setConnectionState(status: ConnectionStatus) {
        val current = _selectedDevice.value
        _selectedDevice.value = current.copy(connectionStatus = status)
    }
}
