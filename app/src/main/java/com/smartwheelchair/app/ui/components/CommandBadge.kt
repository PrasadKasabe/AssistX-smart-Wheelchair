package com.smartwheelchair.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwheelchair.app.domain.Command
import com.smartwheelchair.app.ui.theme.WheelchairTheme

@Composable
fun CommandBadge(
    command: Command?,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    cornerRadius: Dp = 12.dp
) {
    val colors = WheelchairTheme.colors
    val isStop = command == Command.STOP

    val bgColor = if (isStop) colors.offlineBg else colors.accentTintBg
    val textColor = if (isStop) colors.offlineText else colors.accentText
    val letter = command?.byteCode?.toString() ?: "-"

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            color = textColor,
            fontSize = (size.value * 0.45).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
