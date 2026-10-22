package com.smartwheelchair.app.ui.control

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwheelchair.app.domain.Command
import com.smartwheelchair.app.ui.components.CommandBadge
import com.smartwheelchair.app.ui.components.StatusPill
import com.smartwheelchair.app.ui.components.TabSwitcher
import com.smartwheelchair.app.ui.theme.WheelchairTheme

@Composable
fun ControlScreen(
    viewModel: ControlViewModel,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val recentCommands by viewModel.recentCommands.collectAsState()
    val colors = WheelchairTheme.colors
    val haptic = LocalHapticFeedback.current

    val lastCommand = recentCommands.firstOrNull()?.command

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Drive",
                    color = colors.textPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusPill(connectionState = connectionState)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Segmented tab control (Buttons selected -> index 0)
            TabSwitcher(
                selectedTabIndex = 0,
                onTabSelected = onTabSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            // D-pad 3x3 Grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Row 1: [Empty, Forward, Empty]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    DpadButton(
                        label = "Forward",
                        arrow = "▲",
                        modifier = Modifier.weight(1f),
                        haptic = haptic,
                        onClick = { viewModel.sendCommand(Command.FORWARD) }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Row 2: [Left, Stop, Right]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DpadButton(
                        label = "Left",
                        arrow = "◀",
                        modifier = Modifier.weight(1f),
                        haptic = haptic,
                        onClick = { viewModel.sendCommand(Command.LEFT) }
                    )
                    StopDpadButton(
                        modifier = Modifier.weight(1f),
                        haptic = haptic,
                        onClick = { viewModel.sendCommand(Command.STOP) }
                    )
                    DpadButton(
                        label = "Right",
                        arrow = "▶",
                        modifier = Modifier.weight(1f),
                        haptic = haptic,
                        onClick = { viewModel.sendCommand(Command.RIGHT) }
                    )
                }

                // Row 3: [Empty, Backward, Empty]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    DpadButton(
                        label = "Backward",
                        arrow = "▼",
                        modifier = Modifier.weight(1f),
                        haptic = haptic,
                        onClick = { viewModel.sendCommand(Command.BACKWARD) }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Last command card at the bottom
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = BorderStroke(1.dp, colors.border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Last command sent",
                    color = colors.textMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CommandBadge(command = lastCommand, size = 44.dp, cornerRadius = 12.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = lastCommand?.label ?: "No command yet",
                            color = colors.textPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (lastCommand != null) "Sent over Bluetooth" else "Tap a direction button above",
                            color = colors.textMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DpadButton(
    label: String,
    arrow: String,
    modifier: Modifier = Modifier,
    haptic: HapticFeedback,
    onClick: () -> Unit
) {
    val colors = WheelchairTheme.colors

    Box(
        modifier = modifier
            .height(104.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(colors.accentButton)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .semantics { contentDescription = "$label button" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = arrow,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StopDpadButton(
    modifier: Modifier = Modifier,
    haptic: HapticFeedback,
    onClick: () -> Unit
) {
    val colors = WheelchairTheme.colors

    Box(
        modifier = modifier
            .height(104.dp)
            .clip(CircleShape)
            .background(colors.stopButton)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .semantics { contentDescription = "Stop button" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.stopButtonContent)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "STOP",
                color = colors.stopButtonContent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
