package com.example.focusapp.ui

import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * MiniGameActivity loads HTML games from assets folder using WebView
 * Games are played for 1-2 minutes before showing motivational message
 */
class MiniGameActivity : ComponentActivity() {
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FocusAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GameWebViewScreen { exitGame() }
                }
            }
        }

        // Handle back press - don't allow exiting until game is done
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Game decides when to exit
                // WebView can call exitGame() via JavaScript interface
            }
        })
    }

    fun exitGame() {
        finish()
    }

    override fun onDestroy() {
        webView?.destroy()
        super.onDestroy()
    }
}

@Composable
fun GameWebViewScreen(onExit: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webView = this
                    
                    // Configure WebView settings
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }

                    // Set WebViewClient to handle loading
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Inject JavaScript interface
                            addJavascriptInterface(
                                GameJavaScriptInterface(onExit),
                                "Android"
                            )
                        }
                    }

                    // Load a random game
                    val games = listOf(
                        "file:///android_asset/games/memory_game.html",
                        "file:///android_asset/games/math_game.html",
                        "file:///android_asset/games/flappy_bird.html"
                    )
                    val randomGame = games[(Math.random() * games.size).toInt()]
                    loadUrl(randomGame)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * JavaScript interface to communicate between WebView and Android
 */
class GameJavaScriptInterface(private val onExit: () -> Unit) {
    @android.webkit.JavascriptInterface
    fun exitGame() {
        // Called when game is finished
        onExit()
    }

    @android.webkit.JavascriptInterface
    fun showMessage(message: String) {
        // Can be used by games to show native notifications
        println("Game Message: $message")
    }

    @android.webkit.JavascriptInterface
    fun logGameData(data: String) {
        // Log game progress
        println("Game Data: $data")
    }
}
