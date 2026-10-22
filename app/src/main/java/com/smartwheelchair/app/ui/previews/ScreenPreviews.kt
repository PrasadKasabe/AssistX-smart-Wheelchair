package com.smartwheelchair.app.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.smartwheelchair.app.data.bluetooth.FakeBluetoothRepository
import com.smartwheelchair.app.domain.CommandLog
import com.smartwheelchair.app.ui.connect.ConnectScreen
import com.smartwheelchair.app.ui.connect.ConnectViewModel
import com.smartwheelchair.app.ui.control.ControlScreen
import com.smartwheelchair.app.ui.control.ControlViewModel
import com.smartwheelchair.app.ui.theme.WheelchairTheme
import com.smartwheelchair.app.ui.voice.VoiceScreen
import com.smartwheelchair.app.ui.voice.VoiceViewModel
import com.smartwheelchair.app.ui.welcome.WelcomeScreen

@Preview(name = "1. Welcome Screen", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun WelcomeScreenPreview() {
    WheelchairTheme {
        WelcomeScreen(onGetStartedClick = {})
    }
}

@Preview(name = "2. Connect Screen", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ConnectScreenPreview() {
    val viewModel = remember { ConnectViewModel(FakeBluetoothRepository()) }
    WheelchairTheme {
        ConnectScreen(viewModel = viewModel, onContinueClick = {})
    }
}

@Preview(name = "3. Manual Control Screen", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ControlScreenPreview() {
    val viewModel = remember { ControlViewModel(FakeBluetoothRepository(), CommandLog()) }
    WheelchairTheme {
        ControlScreen(viewModel = viewModel, onTabSelected = {})
    }
}

@Preview(name = "4. Voice Control Screen", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun VoiceScreenPreview() {
    val viewModel = remember { VoiceViewModel(FakeBluetoothRepository(), CommandLog()) }
    WheelchairTheme {
        VoiceScreen(viewModel = viewModel, onTabSelected = {})
    }
}
