# Smart Wheelchair Controller ("Wheelchair Control")

College Project (B.Tech IT, SITRC Nashik) - Smart Wheelchair Navigation System with Obstacle Avoidance and Voice Control.

## Overview
This Android app allows users to control a Bluetooth-enabled wheelchair via an Arduino Uno, HC-05 Bluetooth module, and L298N motor driver. It provides both a manual D-pad button interface and a hands-free Voice Control interface using Android's SpeechRecognizer.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Navigation**: Navigation Compose
- **Architecture**: Single Activity, MVVM, ViewModel + StateFlow, Coroutines
- **Min SDK**: 26, Target SDK: 35/36

## Bluetooth Protocol
Each command is ONE ASCII byte (no newline):
- **Forward**: `F`
- **Backward**: `B`
- **Left**: `L`
- **Right**: `R`
- **Stop**: `S`

## HC-05 Pairing Guide
1. Power on the Arduino and HC-05 module.
2. Open Android Bluetooth settings and pair the HC-05 (default PIN: `1234` or `0000`).
3. Open the **Wheelchair Control** app, tap **Scan for devices**, and select your HC-05 module from the paired list (highlighted).

## Minimal Arduino Sketch (Bench Testing)
```cpp
void setup() {
  Serial.begin(9600); // HC-05 default baud rate
}

void loop() {
  if (Serial.available() > 0) {
    char cmd = Serial.read();
    Serial.print("Received command: ");
    Serial.println(cmd);
    
    // Control motors here based on cmd ('F', 'B', 'L', 'R', 'S')
  }
}
```

## Testing Checklist
- [x] App launches to Welcome; flow Welcome -> Connect -> Control <-> Voice works.
- [x] Layouts, colors, radii and text sizes match specification in both light and dark themes.
- [x] Scan lists paired devices and discovered devices; HC-05 is highlighted.
- [x] Connecting to HC-05 shows Connected in the pill across all screens.
- [x] Each of the 5 manual buttons sends the correct byte; Stop works instantly.
- [x] Voice commands ("forward", "backward", "left", "right", "stop") correctly map and send bytes.
- [x] Safety-first Stop priority: any recognized phrase containing "stop" immediately sends `S`.
- [x] Rotation and tab switching preserve connection state.
- [x] Unit tests pass (`./gradlew test`).

## Known Limitations & Next Steps (Out of Scope for v1)
- Reading obstacle distance telemetry back from Arduino (HC-08 / ultrasonic sensor feedback).
- PWM speed control slider.
- Hold-to-move continuous mode.
- Auto-reconnect upon signal loss.
- Emergency SOS / caregiver notification features.
