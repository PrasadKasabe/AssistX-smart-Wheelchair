package com.smartwheelchair.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartwheelchair.app.data.bluetooth.FakeBluetoothRepository
import com.smartwheelchair.app.data.bluetooth.RealBluetoothRepository
import com.smartwheelchair.app.domain.CommandLog
import com.smartwheelchair.app.domain.ConnectionState
import com.smartwheelchair.app.ui.connect.ConnectScreen
import com.smartwheelchair.app.ui.connect.ConnectViewModel
import com.smartwheelchair.app.ui.control.ControlScreen
import com.smartwheelchair.app.ui.control.ControlViewModel
import com.smartwheelchair.app.ui.theme.WheelchairTheme
import com.smartwheelchair.app.ui.voice.VoiceScreen
import com.smartwheelchair.app.ui.voice.VoiceViewModel
import com.smartwheelchair.app.ui.welcome.WelcomeScreen

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Connect : Screen("connect")
    data object Control : Screen("control")
    data object Voice : Screen("voice")
}

@Composable
fun WheelchairApp() {
    val context = LocalContext.current
    // Single shared repository and command log across the app lifecycle
    val bluetoothRepo = remember {
        // Use FakeBluetoothRepository if running in emulator or debug mode if needed, else RealBluetoothRepository
        // For testing/development convenience, we instantiate RealBluetoothRepository (which handles emulator gracefully or fallback)
        RealBluetoothRepository(context.applicationContext)
    }
    val commandLog = remember { CommandLog() }

    val connectViewModel = remember { ConnectViewModel(bluetoothRepo) }
    val controlViewModel = remember { ControlViewModel(bluetoothRepo, commandLog) }
    val voiceViewModel = remember { VoiceViewModel(bluetoothRepo, commandLog) }

    val navController = rememberNavController()
    val connectionState by bluetoothRepo.connectionState.collectAsState()

    var showDisconnectDialog by remember { mutableStateOf(false) }

    WheelchairTheme {
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStartedClick = {
                        navController.navigate(Screen.Connect.route)
                    }
                )
            }

            composable(Screen.Connect.route) {
                ConnectScreen(
                    viewModel = connectViewModel,
                    onContinueClick = {
                        navController.navigate(Screen.Control.route) {
                            popUpTo(Screen.Welcome.route)
                        }
                    }
                )
            }

            composable(Screen.Control.route) {
                BackHandler(enabled = true) {
                    if (connectionState is ConnectionState.Connected) {
                        showDisconnectDialog = true
                    } else {
                        navController.popBackStack(Screen.Connect.route, inclusive = false)
                    }
                }

                ControlScreen(
                    viewModel = controlViewModel,
                    onTabSelected = { tabIndex ->
                        if (tabIndex == 1) {
                            navController.navigate(Screen.Voice.route) {
                                popUpTo(Screen.Control.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.Voice.route) {
                BackHandler(enabled = true) {
                    if (connectionState is ConnectionState.Connected) {
                        showDisconnectDialog = true
                    } else {
                        navController.popBackStack(Screen.Connect.route, inclusive = false)
                    }
                }

                VoiceScreen(
                    viewModel = voiceViewModel,
                    onTabSelected = { tabIndex ->
                        if (tabIndex == 0) {
                            navController.navigate(Screen.Control.route) {
                                popUpTo(Screen.Voice.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        if (showDisconnectDialog) {
            AlertDialog(
                onDismissRequest = { showDisconnectDialog = false },
                title = { Text("Disconnect and go back?") },
                text = { Text("This will disconnect the Bluetooth connection to the wheelchair.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDisconnectDialog = false
                            bluetoothRepo.disconnect()
                            navController.popBackStack(Screen.Connect.route, inclusive = false)
                        }
                    ) {
                        Text("Disconnect")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDisconnectDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
