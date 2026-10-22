package com.smartwheelchair.app.ui.voice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwheelchair.app.domain.CommandRecord
import com.smartwheelchair.app.ui.components.CommandBadge
import com.smartwheelchair.app.ui.components.StatusPill
import com.smartwheelchair.app.ui.components.StopButton
import com.smartwheelchair.app.ui.components.TabSwitcher
import com.smartwheelchair.app.ui.theme.WheelchairTheme

@Composable
fun VoiceScreen(
    viewModel: VoiceViewModel,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isInPreview = androidx.compose.ui.platform.LocalInspectionMode.current
    val connectionState by viewModel.connectionState.collectAsState()
    val recentCommands by viewModel.recentCommands.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val colors = WheelchairTheme.colors

    val speechController = remember {
        if (!isInPreview) {
            SpeechController(
                context = context,
                onPartialResult = viewModel::onPartialResult,
                onFinalResult = viewModel::onFinalResult,
                onError = viewModel::onError,
                onStateChanged = viewModel::onStateChanged
            )
        } else {
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechController?.destroy()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            speechController?.startListening()
        } else {
            viewModel.onError("Microphone permission is required for voice control.")
        }
    }

    val isListening = uiState.voiceState == SpeechController.VoiceState.LISTENING
    val isProcessing = uiState.voiceState == SpeechController.VoiceState.PROCESSING

    val stateLabel = when {
        isListening -> "Listening…"
        isProcessing -> "Processing…"
        else -> "Tap to speak"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
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

            // Segmented tab control (Voice selected -> index 1)
            TabSwitcher(
                selectedTabIndex = 1,
                onTabSelected = onTabSelected
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mic Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val scaleAnim = if (isListening) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.12f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        ).value
                    } else {
                        1f
                    }

                    Box(
                        modifier = Modifier
                            .size(132.dp)
                            .scale(scaleAnim)
                            .clip(CircleShape)
                            .background(colors.accentTintBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(colors.accentButton)
                                .clickable {
                                    speechController?.stopListening()
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                        speechController?.startListening()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                                .semantics { contentDescription = "Start listening" },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎤", fontSize = 40.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stateLabel,
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Say Forward, Backward, Left, Right or Stop",
                        color = colors.textMuted,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // You said card
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
                        text = "You said",
                        color = colors.textMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val recognizedText = uiState.recognizedText
                    if (recognizedText.isNullOrBlank()) {
                        Text(
                            text = "Nothing yet",
                            color = colors.textMuted,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    } else {
                        Text(
                            text = "“$recognizedText”",
                            color = colors.textPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (uiState.isMatched && uiState.matchedCommand != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "➔ ", color = colors.accentText, fontSize = 16.sp)
                                Text(
                                    text = "Matched ${uiState.matchedCommand?.label} · sent ${uiState.matchedCommand?.byteCode}",
                                    color = colors.accentText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Text(
                                text = uiState.errorMessage ?: "No command matched. Try again.",
                                color = colors.offlineText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Recent commands",
                color = colors.textMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Recent commands list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentCommands) { record ->
                    RecentCommandRow(record = record)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Always-visible STOP button
        StopButton(
            onClick = {
                speechController?.stopListening()
                viewModel.sendStop()
            },
            height = 60.dp,
            cornerRadius = 18.dp
        )
    }
}

@Composable
private fun RecentCommandRow(record: CommandRecord) {
    val colors = WheelchairTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CommandBadge(command = record.command, size = 32.dp, cornerRadius = 8.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = record.command.label,
                        color = colors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!record.recognizedText.isNullOrBlank()) {
                        Text(
                            text = "“${record.recognizedText}”",
                            color = colors.textMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Text(
                text = "just now",
                color = colors.textMuted,
                fontSize = 13.sp
            )
        }
    }
}
