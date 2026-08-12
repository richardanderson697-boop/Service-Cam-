package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.ApiGatewayController
import com.example.api.ApiResponse
import com.example.bluetooth.BluetoothBodyCamDevice
import com.example.bluetooth.BluetoothCamManager
import com.example.bluetooth.ConnectionStatus
import com.example.data.ApiKeyEntity
import com.example.data.DowntimeLogEntity
import com.example.data.ServiceCamDatabase
import com.example.data.ServiceCamRepository
import com.example.data.SystemConfigEntity
import com.example.stream.StreamTelemetry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServiceCamViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ServiceCamDatabase.getDatabase(application)
    private val repository = ServiceCamRepository(db.dao())
    val bluetoothManager = BluetoothCamManager(viewModelScope)
    val apiGatewayController = ApiGatewayController()

    val downtimeLogs: StateFlow<List<DowntimeLogEntity>> = repository.allDowntimeLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDowntimeLog: StateFlow<DowntimeLogEntity?> = repository.activeDowntimeLog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val apiKeys: StateFlow<List<ApiKeyEntity>> = repository.allApiKeys
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val systemConfig: StateFlow<SystemConfigEntity?> = repository.systemConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedDevice: StateFlow<BluetoothBodyCamDevice> = bluetoothManager.selectedDevice
    val isScanningBt: StateFlow<Boolean> = bluetoothManager.isScanning

    private val _streamTelemetry = MutableStateFlow(StreamTelemetry())
    val streamTelemetry: StateFlow<StreamTelemetry> = _streamTelemetry.asStateFlow()

    private val _lastApiTestResult = MutableStateFlow<ApiResponse?>(null)
    val lastApiTestResult: StateFlow<ApiResponse?> = _lastApiTestResult.asStateFlow()

    private val _homeSystemSignalStatus = MutableStateFlow("ALL_SYSTEMS_NOMINAL")
    val homeSystemSignalStatus: StateFlow<String> = _homeSystemSignalStatus.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureDefaultData()
        }

        // Keep telemetry in sync with active downtime
        viewModelScope.launch {
            activeDowntimeLog.collect { active ->
                if (active != null) {
                    _streamTelemetry.value = _streamTelemetry.value.copy(
                        isTransmitting = false,
                        privacyShieldActive = true,
                        privacyReason = active.reason
                    )
                    bluetoothManager.setConnectionState(ConnectionStatus.PRIVACY_PAUSED)
                    _homeSystemSignalStatus.value = "SIGNAL_BROADCAST: DOWNTIME_ACTIVE (${active.reason})"
                } else {
                    val currentTransmitting = _streamTelemetry.value.isTransmitting
                    _streamTelemetry.value = _streamTelemetry.value.copy(
                        privacyShieldActive = false,
                        privacyReason = ""
                    )
                    if (currentTransmitting) {
                        bluetoothManager.setConnectionState(ConnectionStatus.STREAMING)
                        _homeSystemSignalStatus.value = "SIGNAL_BROADCAST: LIVE_STREAMING (AES-256 ENCRYPTED)"
                    } else {
                        bluetoothManager.setConnectionState(ConnectionStatus.CONNECTED)
                        _homeSystemSignalStatus.value = "SIGNAL_BROADCAST: STANDBY_CONNECTED"
                    }
                }
            }
        }

        // Telemetry loop for live stats updates
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _streamTelemetry.value
                if (current.isTransmitting && !current.privacyShieldActive) {
                    val newFps = (58..60).random()
                    val newLatency = (38..48).random()
                    val newBytes = current.bytesTransmittedMb + 0.6
                    _streamTelemetry.value = current.copy(
                        fps = newFps,
                        latencyMs = newLatency,
                        bytesTransmittedMb = (newBytes * 10).toInt() / 10.0
                    )
                }
            }
        }
    }

    fun startDowntime(reason: String, note: String, location: String = "Sector 4 - Gate B") {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startDowntime(reason, note, location)
        }
    }

    fun stopDowntime() {
        val active = activeDowntimeLog.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.stopDowntime(active.id)
            _homeSystemSignalStatus.value = "SIGNAL_BROADCAST: DOWNTIME_COMPLETED (ACK_RECEIVED)"
        }
    }

    fun toggleLiveTransmission() {
        if (activeDowntimeLog.value != null) return // Blocked by privacy downtime
        val newTransmitting = !_streamTelemetry.value.isTransmitting
        _streamTelemetry.value = _streamTelemetry.value.copy(isTransmitting = newTransmitting)
        if (newTransmitting) {
            bluetoothManager.setConnectionState(ConnectionStatus.STREAMING)
            _homeSystemSignalStatus.value = "SIGNAL_BROADCAST: LIVE_STREAMING (AES-256 ENCRYPTED)"
        } else {
            bluetoothManager.setConnectionState(ConnectionStatus.CONNECTED)
            _homeSystemSignalStatus.value = "SIGNAL_BROADCAST: STANDBY_PAUSED"
        }
    }

    fun createApiKey(name: String, scopes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createApiKey(name, scopes)
        }
    }

    fun toggleApiKeyRevoked(id: Long, isRevoked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleApiKeyRevoked(id, isRevoked)
        }
    }

    fun deleteDowntimeLog(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDowntimeLog(id)
        }
    }

    fun testApiCall(endpoint: String, apiKeyInput: String) {
        val keys = apiKeys.value
        val telemetry = streamTelemetry.value
        val logs = downtimeLogs.value

        val response = apiGatewayController.executeApiCall(
            endpoint = endpoint,
            apiKeyInput = apiKeyInput,
            registeredKeys = keys,
            activeTelemetry = telemetry,
            downtimeLogs = logs,
            onTogglePrivacyRemote = { newPrivacy, reason ->
                if (newPrivacy) {
                    startDowntime(reason, "Toggled via external API request")
                } else {
                    stopDowntime()
                }
            }
        )

        if (response.success) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.incrementApiKeyUsage(apiKeyInput.trim())
            }
        }

        _lastApiTestResult.value = response
    }

    fun saveConfig(config: SystemConfigEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveConfig(config)
        }
    }

    fun selectBluetoothDevice(device: BluetoothBodyCamDevice) {
        bluetoothManager.selectDevice(device)
    }

    fun scanBluetoothDevices() {
        bluetoothManager.startScan()
    }
}
