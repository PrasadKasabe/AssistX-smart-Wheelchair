# Implementation Plan - Smart Wheelchair Controller App ("Wheelchair Control")

Build a complete, compiling Android application for controlling a Bluetooth wheelchair via Arduino (HC-05 module) with manual D-pad buttons and voice control (SpeechRecognizer), following the provided specification exactly.

## User Review Required

> [!IMPORTANT]
> - Package name will be updated to `com.smartwheelchair.app` and app name to "Wheelchair Control" per specification.
> - `minSdk` will be set to 26 as required.
> - Navigation Compose will be added to dependencies.

## Open Questions

- None. The specification is comprehensive.

## Proposed Changes

### Project Configuration & Setup
- Update `app/build.gradle.kts`: set `namespace = "com.smartwheelchair.app"`, `applicationId = "com.smartwheelchair.app"`, `minSdk = 26`, add Navigation Compose dependency.
- Update `gradle/libs.versions.toml`: add navigation-compose version and library.
- Update `app/src/main/res/values/strings.xml`: add app name "Wheelchair Control" and string resources.
- Update `app/src/main/AndroidManifest.xml`: add Bluetooth permissions (`BLUETOOTH`, `BLUETOOTH_ADMIN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`), `RECORD_AUDIO`, location permissions, and `<queries>` tag for speech recognition.

### Domain & Bluetooth Layer
- **[NEW]** `domain/Command.kt`: Enum or data representations for commands (`F`, `B`, `L`, `R`, `S`) and connection state.
- **[NEW]** `domain/CommandMapper.kt`: Pure Kotlin keyword/phrase matching algorithm with safety-first Stop priority, case insensitivity, punctuation stripping, and candidate iteration.
- **[NEW]** `domain/CommandLog.kt`: In-memory recent command log (max 5 items, shared).
- **[NEW]** `data/bluetooth/BluetoothRepository.kt`: Interface defining connection state flow, device list, scan, connect, disconnect, and send command.
- **[NEW]** `data/bluetooth/RealBluetoothRepository.kt`: Classic Bluetooth SPP (RFCOMM) implementation using UUID `00001101-0000-1000-8000-00805F9B34FB`, broadcast receiver for discovery, and socket communication on `Dispatchers.IO`.
- **[NEW]** `data/bluetooth/FakeBluetoothRepository.kt`: Simulated repository for testing and emulator use (controlled via `USE_FAKE_BLUETOOTH` flag or debug settings).

### UI Theme & Components
- **[NEW]** `ui/theme/Color.kt`: Define all light and dark theme hex tokens specified in section 7.
- **[NEW]** `ui/theme/Theme.kt`: `WheelchairTheme`, `WheelchairColors`, and `CompositionLocal` providing wheelchair color scheme for both light and dark modes.
- **[NEW]** `ui/theme/Type.kt`: Typography configuration (Inter font support / system fallback).
- **[NEW]** `ui/components/`:
  - `StatusPill.kt`: Connected / Offline status indicator pill.
  - `TabSwitcher.kt`: Segmented tab control ("Buttons" | "Voice").
  - `StopButton.kt`: Reusable STOP button component.
  - `CommandBadge.kt`: Letter badge for recent commands.

### Screens & ViewModels
- **[NEW]** `ui/welcome/WelcomeScreen.kt`: Welcome screen matching the teal background, pill, vector icon, feature rows, and "Get started" button.
- **[NEW]** `ui/connect/ConnectViewModel.kt` & `ConnectScreen.kt`: Scan, list paired and discovered HC-05 devices (highlighted), connect action, and continue button.
- **[NEW]** `ui/control/ControlViewModel.kt` & `ControlScreen.kt`: Manual D-pad controls (Forward, Backward, Left, Right, Stop), last command card, haptic feedback, KeepScreenOn.
- **[NEW]** `ui/voice/SpeechController.kt` & `VoiceViewModel.kt` & `VoiceScreen.kt`: SpeechRecognizer integration, live partial results, CommandMapper integration, recent commands list, always-visible STOP button.
- **[NEW]** `WheelchairApp.kt`: NavHost wiring up Welcome -> Connect -> Control <-> Voice navigation flow with disconnection confirm dialog.
- **[MODIFY]** `MainActivity.kt`: Launch `WheelchairApp`.

### Tests & Documentation
- **[NEW]** `app/src/test/java/com/smartwheelchair/app/domain/CommandMapperTest.kt`: >= 20 unit test cases for `CommandMapper`.
- **[NEW]** `app/src/test/java/com/smartwheelchair/app/ui/control/ControlViewModelTest.kt` (and ViewModel tests): tests for command sending, fake repository, and Stop priority.
- **[NEW]** `README.md`: Setup steps, HC-05 pairing guide, command table, minimal Arduino sketch, testing checklist, and known limitations.

## Verification Plan

### Automated Tests
- Run unit tests: `./gradlew test` (including `CommandMapperTest` and ViewModel tests).
- Build project: `./gradlew assembleDebug`.

### Manual Verification
- Verify app flow on emulator or device: Welcome -> Connect -> Control <-> Voice.
- Test UI layouts, light and dark themes, status pills, and responsiveness.
