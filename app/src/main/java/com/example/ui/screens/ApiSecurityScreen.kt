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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApiKeyEntity
import com.example.data.SystemConfigEntity
import com.example.ui.ServiceCamViewModel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.LiveRed
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TacticalSurface
import com.example.ui.theme.TacticalSurfaceVariant

@Composable
fun ApiSecurityScreen(viewModel: ServiceCamViewModel) {
    val apiKeys by viewModel.apiKeys.collectAsState()
    val config by viewModel.systemConfig.collectAsState()
    val apiTestResult by viewModel.lastApiTestResult.collectAsState()

    var showCreateKeyDialog by remember { mutableStateOf(false) }
    var newKeyName by remember { mutableStateOf("") }
    var newKeyScopes by remember { mutableStateOf("STREAM_READ,DOWNTIME_LOG_READ") }

    // API Sandbox Tester State
    var selectedEndpoint by remember { mutableStateOf("GET /api/v1/status") }
    var selectedApiKeyInput by remember { mutableStateOf("") }
    var endpointMenuExpanded by remember { mutableStateOf(false) }

    val availableEndpoints = listOf(
        "GET /api/v1/status",
        "GET /api/v1/downtime-logs",
        "POST /api/v1/privacy/toggle",
        "GET /api/v1/stream/telemetry"
    )

    // Config editing
    var homeUrlInput by remember { mutableStateOf(config?.homeEndpointUrl ?: "https://central-hq.servicecam.net/v1/telemetry") }
    var hmacSecretInput by remember { mutableStateOf(config?.hmacSecret ?: "sk_live_aes256_990184758291039485") }

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
                    text = "API GATEWAY & ENCRYPTION",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = CyanAccent,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Secure API Access Keys, Low-Latency AES-256 Specs & Sandbox Tester",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // INTERACTIVE API SANDBOX TESTER
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = CyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVE API SANDBOX & REQUEST INSPECTOR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Endpoint Selector
                    Box {
                        OutlinedTextField(
                            value = selectedEndpoint,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select API Endpoint") },
                            leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, tint = CyanAccent) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { endpointMenuExpanded = true }
                                .testTag("api_endpoint_selector"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            enabled = false
                        )
                        DropdownMenu(
                            expanded = endpointMenuExpanded,
                            onDismissRequest = { endpointMenuExpanded = false }
                        ) {
                            availableEndpoints.forEach { ep ->
                                DropdownMenuItem(
                                    text = { Text(ep) },
                                    onClick = {
                                        selectedEndpoint = ep
                                        endpointMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // API Key Input / Quick Pick
                    OutlinedTextField(
                        value = selectedApiKeyInput,
                        onValueChange = { selectedApiKeyInput = it },
                        label = { Text("API Key (or select from list below)") },
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyanAccent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_key_test_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.testApiCall(selectedEndpoint, selectedApiKeyInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("send_api_request_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "EXECUTE API REQUEST", fontWeight = FontWeight.Bold)
                    }

                    // API Response Viewer
                    apiTestResult?.let { res ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(0.7f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (res.success) StatusGreen else LiveRed
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (res.success) StatusGreen.copy(0.2f) else LiveRed.copy(0.2f)
                                    ) {
                                        Text(
                                            text = res.message,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (res.success) StatusGreen else LiveRed,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = res.rawJsonData,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // API KEY MANAGEMENT HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REGISTERED API ACCESS KEYS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { showCreateKeyDialog = true },
                    modifier = Modifier.testTag("create_api_key_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "NEW KEY", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // API KEY LIST
        items(apiKeys) { keyEntity ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = if (keyEntity.isRevoked) Color.Gray else CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = keyEntity.keyName,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = keyEntity.apiKey,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyanAccent
                            )
                        }

                        // Quick Select for Sandbox
                        IconButton(
                            onClick = { selectedApiKeyInput = keyEntity.apiKey },
                            modifier = Modifier.testTag("use_key_${keyEntity.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Use in Sandbox",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Revoke Switch
                        Switch(
                            checked = !keyEntity.isRevoked,
                            onCheckedChange = { active ->
                                viewModel.toggleApiKeyRevoked(keyEntity.id, !active)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = StatusGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row {
                        Text(
                            text = "Scopes: ${keyEntity.scopes}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Calls: ${keyEntity.totalRequests}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                    }
                }
            }
        }

        // ENCRYPTION & CENTRAL HQ SETTINGS
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TacticalSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CENTRAL HQ SYSTEM & AES-256 CONFIG",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SafetyAmber
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = homeUrlInput,
                        onValueChange = { homeUrlInput = it },
                        label = { Text("Central Home Endpoint URL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("central_endpoint_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = hmacSecretInput,
                        onValueChange = { hmacSecretInput = it },
                        label = { Text("HMAC SHA-256 Secret Key") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hmac_secret_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val current = config ?: SystemConfigEntity()
                            viewModel.saveConfig(
                                current.copy(
                                    homeEndpointUrl = homeUrlInput,
                                    hmacSecret = hmacSecretInput
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_config_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyAmber, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "SAVE CENTRAL SYSTEM CONFIG", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // CREATE API KEY DIALOG
    if (showCreateKeyDialog) {
        AlertDialog(
            onDismissRequest = { showCreateKeyDialog = false },
            title = { Text("Create ServiceCam API Key") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newKeyName,
                        onValueChange = { newKeyName = it },
                        label = { Text("App / Service Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_key_name_input")
                    )
                    OutlinedTextField(
                        value = newKeyScopes,
                        onValueChange = { newKeyScopes = it },
                        label = { Text("Scopes (comma separated)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_key_scopes_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newKeyName.isNotBlank()) {
                            viewModel.createApiKey(newKeyName, newKeyScopes)
                            newKeyName = ""
                            showCreateKeyDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_create_key_button")
                ) {
                    Text("Generate Key")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCreateKeyDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = TacticalSurface,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}
