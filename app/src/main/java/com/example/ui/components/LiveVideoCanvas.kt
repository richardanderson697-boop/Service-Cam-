package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth.BluetoothBodyCamDevice
import com.example.stream.StreamTelemetry
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.LiveRed
import com.example.ui.theme.PrivacyMaskBg
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TacticalDarkBg
import com.example.ui.theme.TacticalSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LiveVideoCanvas(
    telemetry: StreamTelemetry,
    connectedDevice: BluetoothBodyCamDevice,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hud_scan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TacticalDarkBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (telemetry.privacyShieldActive) {
                // PRIVACY BLACKOUT SHIELD
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(PrivacyMaskBg, Color(0xFF220808))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SafetyAmber.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(2.dp, SafetyAmber)
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.NoPhotography,
                                    contentDescription = "Privacy Active",
                                    tint = SafetyAmber,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "PRIVACY SHIELD ACTIVE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SafetyAmber,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Video Transmission Suspended: ${telemetry.privacyReason}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SafetyAmber.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = StatusGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HOME SYSTEM SIGNALED (HMAC SIGNED)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            }
                        }
                    }
                }
            } else {
                // LIVE STREAM SIMULATION CANVAS
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Synthetic camera background pattern (Dark Tactical Grid)
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF0F2027),
                                Color(0xFF203A43),
                                Color(0xFF0F172A)
                            ),
                            center = Offset(width * 0.5f, height * 0.5f),
                            radius = width * 0.7f
                        )
                    )

                    // Draw tactical grid crosshairs
                    val gridColor = Color(0xFF00E5FF).copy(alpha = 0.15f)
                    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                    // Vertical grid
                    drawLine(gridColor, Offset(width * 0.33f, 0f), Offset(width * 0.33f, height), pathEffect = dashPathEffect)
                    drawLine(gridColor, Offset(width * 0.66f, 0f), Offset(width * 0.66f, height), pathEffect = dashPathEffect)

                    // Horizontal grid
                    drawLine(gridColor, Offset(0f, height * 0.33f), Offset(width, height * 0.33f), pathEffect = dashPathEffect)
                    drawLine(gridColor, Offset(0f, height * 0.66f), Offset(width, height * 0.66f), pathEffect = dashPathEffect)

                    // Animated Scanline
                    if (telemetry.isTransmitting) {
                        val yPos = scanLineY * height
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    CyanAccent.copy(alpha = 0.4f),
                                    CyanAccent.copy(alpha = 0.8f),
                                    CyanAccent.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            start = Offset(0f, yPos),
                            end = Offset(width, yPos),
                            strokeWidth = 3f
                        )
                    }

                    // Tactical reticle corners
                    val cornerLen = 28f
                    val strokeW = 4f
                    val reticleColor = CyanAccent.copy(alpha = 0.8f)

                    // Top Left Corner
                    drawLine(reticleColor, Offset(20f, 20f), Offset(20f + cornerLen, 20f), strokeWidth = strokeW)
                    drawLine(reticleColor, Offset(20f, 20f), Offset(20f, 20f + cornerLen), strokeWidth = strokeW)

                    // Top Right Corner
                    drawLine(reticleColor, Offset(width - 20f, 20f), Offset(width - 20f - cornerLen, 20f), strokeWidth = strokeW)
                    drawLine(reticleColor, Offset(width - 20f, 20f), Offset(width - 20f, 20f + cornerLen), strokeWidth = strokeW)

                    // Bottom Left Corner
                    drawLine(reticleColor, Offset(20f, height - 20f), Offset(20f + cornerLen, height - 20f), strokeWidth = strokeW)
                    drawLine(reticleColor, Offset(20f, height - 20f), Offset(20f, height - 20f - cornerLen), strokeWidth = strokeW)

                    // Bottom Right Corner
                    drawLine(reticleColor, Offset(width - 20f, height - 20f), Offset(width - 20f - cornerLen, height - 20f), strokeWidth = strokeW)
                    drawLine(reticleColor, Offset(width - 20f, height - 20f), Offset(width - 20f, height - 20f - cornerLen), strokeWidth = strokeW)
                }

                // HUD OVERLAY DATA
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Top Bar HUD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // LIVE RECORDING INDICATOR
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (telemetry.isTransmitting) LiveRed.copy(alpha = 0.25f) else Color.Gray.copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (telemetry.isTransmitting) LiveRed else Color.Gray
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (telemetry.isTransmitting) LiveRed.copy(alpha = pulseAlpha) else Color.Gray
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (telemetry.isTransmitting) "LIVE STREAMING" else "STREAM PAUSED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (telemetry.isTransmitting) LiveRed else Color.LightGray,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // ENCRYPTION BADGE
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TacticalSurface.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AES-256",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom Bar HUD Metadata
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DEV: ${connectedDevice.name}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyanAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LATENCY: ${telemetry.latencyMs} ms",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = StatusGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "FPS: ${telemetry.fps}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = StatusGreen
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "TX: ${telemetry.bytesTransmittedMb} MB",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.LightGray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "37°46'29.8\"N 122°25'09.1\"W",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SafetyAmber
                            )
                        }
                    }
                }
            }
        }
    }
}
