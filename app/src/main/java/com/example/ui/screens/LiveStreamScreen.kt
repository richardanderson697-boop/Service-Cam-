package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth.BluetoothBodyCamDevice
import com.example.bluetooth.ConnectionStatus
import com.example.ui.ServiceCamViewModel
import com.example.ui.components.LiveVideoCanvas
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.LiveRed
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamScreen(
    viewModel: ServiceCamViewModel,
    onNavigateToPrivacy: () -> Unit
) {
    val telemetry by viewModel.streamTelemetry.collectAsState()
    val connectedDevice by viewModel.selectedDevice.collectAsState()
    val activeDowntime by viewModel.activeDowntimeLog.collectAsState()
    val homeSignalStatus by viewModel.homeSystemSignalStatus.collectAsState()
    val isScanningBt by viewModel.isScanningBt.collectAsState()

    var showBtSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP HEADER BAR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SERVICE CAM",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = CyanAccent,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Bodycam Live Streaming & Security Relay",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                // Bluetooth Device Selector Pill
                Surface(
                    onClick = { showBtSheet = true },
                    shape = RoundedCornerShape(20.dp),
                    color = TacticalSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("bluetooth_selector_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BluetoothConnected,
                            contentDescription = "Bluetooth Device",
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = connectedDevice.name.take(16),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // CENTRAL HOME SYSTEM SIGNAL STATUS CARD
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (activeDowntime != null) SafetyAmber.copy(0.2f) else StatusGreen.copy(0.2f)
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                imageVector = if (activeDowntime != null) Icons.Default.Shield else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (activeDowntime != null) SafetyAmber else StatusGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CENTRAL HOME SYSTEM RELAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = homeSignalStatus,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // LIVE HUD VIDEO PREVIEW CANVAS
        item {
            LiveVideoCanvas(
                telemetry = telemetry,
                connectedDevice = connectedDevice
            )
        }

        // MAIN CONTROL BUTTONS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (activeDowntime != null) {
                    Button(
                        onClick = onNavigateToPrivacy,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("active_privacy_resume_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyAmber, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "MANAGE BREAK / PRIVACY", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.toggleLiveTransmission() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("toggle_stream_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (telemetry.isTransmitting) LiveRed else StatusGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (telemetry.isTransmitting) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (telemetry.isTransmitting) "PAUSE STREAMING" else "START STREAMING",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onNavigateToPrivacy,
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("go_to_privacy_toggle_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SafetyAmber),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SafetyAmber)
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = SafetyAmber)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "BREAK / RESTROOM", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // DEVICE & TELEMETRY SUMMARY CARDS
        item {
            Text(
                text = "STREAM & BLUETOOTH TELEMETRY",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Device Status Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "BODYCAM STATUS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = connectedDevice.connectionStatus.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Battery: ${connectedDevice.batteryPercent}%", fontSize = 11.sp, color = Color.White)
                        Text(text = "Signal: ${connectedDevice.rssiDbm} dBm", fontSize = 11.sp, color = Color.White)
                    }
                }

                // Security Specs Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "ENCRYPTION & SPECS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "AES-256-GCM", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "TLS 1.3 Tunnel", fontSize = 11.sp, color = Color.White)
                        Text(text = "Res: 1080p @ 60fps", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }

    // BLUETOOTH DEVICE SELECTION BOTTOM SHEET
    if (showBtSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBtSheet = false },
            sheetState = sheetState,
            containerColor = TacticalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PAIRED BLUETOOTH BODY CAMS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { viewModel.scanBluetoothDevices() },
                        modifier = Modifier.testTag("scan_bluetooth_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan",
                            tint = CyanAccent
                        )
                    }
                }

                if (isScanningBt) {
                    Text(
                        text = "Scanning for nearby ServiceCam Bluetooth devices...",
                        fontSize = 12.sp,
                        color = CyanAccent,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                viewModel.bluetoothManager.availableDevices.forEach { device ->
                    val isSelected = device.macAddress == connectedDevice.macAddress
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.selectBluetoothDevice(device)
                                showBtSheet = false
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) TacticalSurfaceVariant else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = null,
                                tint = if (isSelected) CyanAccent else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "MAC: ${device.macAddress} • Battery ${device.batteryPercent}%",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = CyanAccent
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
