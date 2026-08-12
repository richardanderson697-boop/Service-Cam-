package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ServiceCamViewModel
import com.example.ui.screens.ApiSecurityScreen
import com.example.ui.screens.AuditLogsScreen
import com.example.ui.screens.LiveStreamScreen
import com.example.ui.screens.PrivacyBreaksScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.ServiceCamTheme
import com.example.ui.theme.TacticalSurface

data class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {

    private val viewModel: ServiceCamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ServiceCamTheme {
                ServiceCamMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ServiceCamMainApp(viewModel: ServiceCamViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        NavigationTab("Live Stream", Icons.Filled.Videocam, Icons.Outlined.Videocam, "nav_tab_live_stream"),
        NavigationTab("Privacy & Breaks", Icons.Filled.Shield, Icons.Outlined.Shield, "nav_tab_privacy_breaks"),
        NavigationTab("Audit Logs", Icons.Filled.History, Icons.Outlined.History, "nav_tab_audit_logs"),
        NavigationTab("API & Security", Icons.Filled.Code, Icons.Outlined.Code, "nav_tab_api_security")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = TacticalSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("service_cam_bottom_navigation")
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                tint = if (isSelected) CyanAccent else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyanAccent else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyanAccent.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> LiveStreamScreen(
                    viewModel = viewModel,
                    onNavigateToPrivacy = { selectedTabIndex = 1 }
                )
                1 -> PrivacyBreaksScreen(viewModel = viewModel)
                2 -> AuditLogsScreen(viewModel = viewModel)
                3 -> ApiSecurityScreen(viewModel = viewModel)
            }
        }
    }
}

