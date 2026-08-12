package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ServiceCamViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.PrivacyMaskBg
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceVariant
import kotlinx.coroutines.delay

@Composable
fun PrivacyBreaksScreen(viewModel: ServiceCamViewModel) {
    val activeDowntime by viewModel.activeDowntimeLog.collectAsState()
    val downtimeLogs by viewModel.downtimeLogs.collectAsState()

    var customNote by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("Sector 4 - Restroom B") }
    var locationMenuExpanded by remember { mutableStateOf(false) }

    val locations = listOf(
        "Sector 4 - Restroom B",
        "Staff Cafeteria - Zone C",
        "Officer Break Lounge 2",
        "Personal Vehicles Parking",
        "Off-Duty Precinct HQ"
    )

    // Active downtime elapsed timer
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    LaunchedEffect(activeDowntime) {
        if (activeDowntime != null) {
            while (true) {
                val start = activeDowntime?.startTime ?: System.currentTimeMillis()
                elapsedSeconds = (System.currentTimeMillis() - start) / 1000
                delay(1000)
            }
        } else {
            elapsedSeconds = 0L
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SCREEN TITLE
        item {
            Column {
                Text(
                    text = "PRIVACY & DOWNTIME CONTROLS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = SafetyAmber,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Halt streaming & broadcast signed downtime signals to central home system",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ACTIVE DOWNTIME CARD (IF IN BREAK)
        item {
            AnimatedVisibility(visible = activeDowntime != null) {
                activeDowntime?.let { active ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PrivacyMaskBg),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SafetyAmber)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = SafetyAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DOWNTIME IN PROGRESS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = SafetyAmber,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Timer Display
                            val mins = elapsedSeconds / 60
                            val secs = elapsedSeconds % 60
                            val timerString = String.format("%02d:%02d", mins, secs)

                            Text(
                                text = timerString,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White
                            )

                            Text(
                                text = "Reason: ${active.reason}",
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Location: ${active.locationTag}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // End Downtime Button
                            Button(
                                onClick = { viewModel.stopDowntime() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("end_downtime_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusGreen,
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "END BREAK & RESUME LIVE STREAM",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // QUICK-ACTION TOGGLE BUTTONS (IF NOT IN BREAK)
        if (activeDowntime == null) {
            item {
                Text(
                    text = "SELECT PRIVACY DOWNTIME REASON",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Restroom Mode Button
                    DowntimeQuickCard(
                        title = "Restroom Facility",
                        subtitle = "Immediately suspends video/audio for restroom privacy",
                        icon = Icons.Default.Wc,
                        accentColor = SafetyAmber,
                        testTag = "toggle_restroom_button",
                        onClick = {
                            viewModel.startDowntime("Restroom Facility", customNote.ifBlank { "Standard restroom break" }, selectedLocation)
                        }
                    )

                    // Meal / Lunch Break Button
                    DowntimeQuickCard(
                        title = "Meal / Lunch Break",
                        subtitle = "Scheduled lunch downtime signal to central HQ",
                        icon = Icons.Default.Restaurant,
                        accentColor = CyanAccent,
                        testTag = "toggle_meal_button",
                        onClick = {
                            viewModel.startDowntime("Meal Break", customNote.ifBlank { "Scheduled meal break" }, selectedLocation)
                        }
                    )

                    // Personal Time Button
                    DowntimeQuickCard(
                        title = "Personal Time / Off-Duty",
                        subtitle = "Personal break or end of active patrol shift",
                        icon = Icons.Default.Coffee,
                        accentColor = Color(0xFFE040FB),
                        testTag = "toggle_personal_button",
                        onClick = {
                            viewModel.startDowntime("Personal Time", customNote.ifBlank { "Personal break" }, selectedLocation)
                        }
                    )

                    // Emergency Pause Button
                    DowntimeQuickCard(
                        title = "Emergency Pause / Admin",
                        subtitle = "Immediate administrative or confidential pause",
                        icon = Icons.Default.Shield,
                        accentColor = Color(0xFFFF5252),
                        testTag = "toggle_emergency_button",
                        onClick = {
                            viewModel.startDowntime("Emergency Pause", customNote.ifBlank { "Admin confidential pause" }, selectedLocation)
                        }
                    )
                }
            }

            // LOCATION TAG & NOTE INPUT
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TacticalSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "LOCATION TAG & OPTIONAL NOTE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Location Dropdown
                        Box {
                            OutlinedTextField(
                                value = selectedLocation,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Location Tag") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyanAccent) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { locationMenuExpanded = true },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                enabled = false
                            )
                            DropdownMenu(
                                expanded = locationMenuExpanded,
                                onDismissRequest = { locationMenuExpanded = false }
                            ) {
                                locations.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(loc) },
                                        onClick = {
                                            selectedLocation = loc
                                            locationMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Note Input Field
                        OutlinedTextField(
                            value = customNote,
                            onValueChange = { customNote = it },
                            label = { Text("Optional Downtime Note / Supervisor Info") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Note, contentDescription = null, tint = CyanAccent) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("downtime_note_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }
        }

        // HOME SYSTEM RECENT SIGNALS LOG
        item {
            Text(
                text = "DISPATCHED CENTRAL HOME SYSTEM SIGNALS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(downtimeLogs.take(5)) { log ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "ACK",
                        tint = StatusGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.reason + " (" + log.locationTag + ")",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "ACK Code: ${log.centralAckCode} • ${if (log.isCompleted) "${log.durationSeconds}s duration" else "ACTIVE"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StatusGreen.copy(0.15f)
                    ) {
                        Text(
                            text = log.syncStatus,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DowntimeQuickCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TacticalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.2f)
            ) {
                Box(modifier = Modifier.padding(10.dp)) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.PauseCircle,
                contentDescription = "Activate",
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
