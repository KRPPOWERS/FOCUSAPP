package com.example.focusapp.ui

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * WarningActivity shows one of 6 motivational warning screens
 * Each time user clicks a restricted app during focus time, they see a different warning
 * After the 6th warning, they see the mini-game instead
 */
class WarningActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on and show above lock screen
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        // Get warning number (1-6) from intent
        val warningNumber = intent.getIntExtra("warning_number", 1)
        val packageName = intent.getStringExtra("package_name") ?: "Unknown App"

        setContent {
            FocusAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212)
                ) {
                    WarningScreen(warningNumber, packageName) {
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun WarningScreen(
    warningNumber: Int,
    packageName: String,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    // Auto-show animation
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Auto-close after 5 seconds
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(5000)
            onDismiss()
        }
    }

    val warningData = getWarningData(warningNumber)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.foundation.background.brush
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn() + fadeIn()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        color = Color(0xFF1E1E1E),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    )
                    .padding(30.dp)
            ) {
                // Warning number indicator
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = warningData.backgroundColor,
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = warningData.emoji,
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Warning level
                Text(
                    text = "Warning ${warningNumber}/6",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Main message
                Text(
                    text = warningData.message,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Motivation text
                Text(
                    text = warningData.motivation,
                    fontSize = 16.sp,
                    color = Color(0xFFBBBBBB),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Warning counter
                LinearProgressIndicator(
                    progress = { (warningNumber.toFloat() / 6) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = warningData.backgroundColor,
                    trackColor = Color(0xFF2A2A2A)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A2A)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "💡 After 6 warnings, you'll get a quick puzzle game.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Dismiss button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = warningData.backgroundColor
                    )
                ) {
                    Text("Got it! Let me focus 💪", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Auto-close info
                Text(
                    text = "This screen closes automatically in 5 seconds",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

data class WarningData(
    val emoji: String,
    val message: String,
    val motivation: String,
    val backgroundColor: Color
)

fun getWarningData(warningNumber: Int): WarningData {
    return when (warningNumber) {
        1 -> WarningData(
            emoji = "📱",
            message = "Hold on! 🛑",
            motivation = "You're in focus time right now. These distracting apps are off-limits. You can do this!",
            backgroundColor = Color(0xFFFF6B6B)
        )
        2 -> WarningData(
            emoji = "⏱️",
            message = "Time is precious!",
            motivation = "You have limited focus time. Every second counts. Stay strong and focused! 💪",
            backgroundColor = Color(0xFFFF9A3D)
        )
        3 -> WarningData(
            emoji = "🎯",
            message = "Remember your goal?",
            motivation = "You set this focus time for a reason. You're capable of amazing things. Keep going!",
            backgroundColor = Color(0xFFFFA94D)
        )
        4 -> WarningData(
            emoji = "💪",
            message = "You're stronger than this!",
            motivation = "You've overcome distractions before. This is just 4 more warnings away from success!",
            backgroundColor = Color(0xFF74C0FC)
        )
        5 -> WarningData(
            emoji = "🚀",
            message = "Almost there!",
            motivation = "Just 1 more warning and you unlock the focus zone. You're so close! Push through!",
            backgroundColor = Color(0xFF7950F2)
        )
        6 -> WarningData(
            emoji = "✨",
            message = "Last chance!",
            motivation = "One more click and you'll play a fun game. Then focus harder than ever! You've got this!",
            backgroundColor = Color(0xFFF06595)
        )
        else -> WarningData(
            emoji = "⚠️",
            message = "Warning!",
            motivation = "Stay focused on your goals.",
            backgroundColor = Color(0xFF868E96)
        )
    }
}
