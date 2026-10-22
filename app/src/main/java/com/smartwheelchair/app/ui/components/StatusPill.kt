package com.smartwheelchair.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwheelchair.app.domain.ConnectionState
import com.smartwheelchair.app.ui.theme.WheelchairTheme

@Composable
fun StatusPill(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    val colors = WheelchairTheme.colors
    val (bgColor, textColor, label) = when (connectionState) {
        is ConnectionState.Connected -> Triple(colors.connectedBg, colors.connectedText, "Connected · ${connectionState.deviceName}")
        is ConnectionState.Connecting -> Triple(colors.connectedBg, colors.connectedText, "Connecting…")
        is ConnectionState.Scanning -> Triple(colors.accentTintBg, colors.accentText, "Scanning…")
        is ConnectionState.Error -> Triple(colors.offlineBg, colors.offlineText, "Error")
        is ConnectionState.Disconnected -> Triple(colors.offlineBg, colors.offlineText, "Offline")
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(textColor)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
