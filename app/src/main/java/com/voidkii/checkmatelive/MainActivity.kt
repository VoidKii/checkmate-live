package com.voidkii.checkmatelive

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.checkmate.capture.ScreenCaptureService
import com.checkmate.overlay.OverlayService

private val Ink = Color(0xFF101114)
private val Panel = Color(0xFF1A1C21)
private val Muted = Color(0xFF9A9DA7)
private val Accent = Color(0xFFE8E9ED)

class MainActivity : ComponentActivity() {
    private var captureState by mutableStateOf(false)
    private var chosenSide = "w"

    private val capturePermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val overlayIntent = Intent(this, OverlayService::class.java)
            startService(overlayIntent)
            val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, data)
                putExtra(ScreenCaptureService.EXTRA_USER_SIDE, chosenSide)
            }
            ContextCompat.startForegroundService(this, serviceIntent)
            captureState = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                background = Ink,
                surface = Panel,
                primary = Accent,
                onPrimary = Ink,
                onBackground = Accent,
                onSurface = Accent
            )) {
                var mode by remember { mutableStateOf("Offline") }
                var side by remember { mutableStateOf("w") }
                Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("CHECKMATE LIVE", color = Muted, fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("Chess, understood.", fontSize = 29.sp, lineHeight = 34.sp,
                            fontWeight = FontWeight.SemiBold)
                        Text("Live board recognition and engine analysis for bot games and solo practice.",
                            color = Muted, fontSize = 14.sp)

                        Column(
                            modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(20.dp)).padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("ANALYSIS MODE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ModeButton("Offline", mode == "Offline") { mode = "Offline" }
                                ModeButton("Online", mode == "Online") { mode = "Online" }
                            }
                            Text(
                                if (mode == "Offline") "Stockfish runs on your phone. No internet or account needed."
                                else "Online analysis is not connected yet. Offline Stockfish remains available.",
                                color = Muted, fontSize = 13.sp
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(20.dp)).padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("YOUR SIDE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ModeButton("White", side == "w") { side = "w"; chosenSide = "w" }
                                ModeButton("Black", side == "b") { side = "b"; chosenSide = "b" }
                            }
                            Text("Choose the colour you play as in the bot game. Start from the normal initial position to calibrate piece recognition.",
                                color = Muted, fontSize = 13.sp)
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(20.dp)).padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("LIVE ANALYSIS", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                if (captureState) "Capture session started. Checkmate Live will look for a chessboard."
                                else "Not active. You control when screen capture starts and stops.",
                                fontSize = 14.sp
                            )
                            OutlinedButton(
                                onClick = {
                                    if (!Settings.canDrawOverlays(this@MainActivity)) {
                                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:$packageName"))
                                        startActivity(intent)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(if (Settings.canDrawOverlays(this@MainActivity)) "Overlay permission granted" else "Grant floating-panel permission") }
                            Button(
                                onClick = {
                                    if (!Settings.canDrawOverlays(this@MainActivity)) {
                                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:$packageName"))
                                        startActivity(intent)
                                    } else {
                                        val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                                        capturePermission.launch(manager.createScreenCaptureIntent())
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Ink)
                            ) { Text("Start live analysis") }
                            OutlinedButton(
                                onClick = {
                                    stopService(Intent(this@MainActivity, ScreenCaptureService::class.java))
                                    stopService(Intent(this@MainActivity, OverlayService::class.java))
                                    captureState = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Stop analysis") }
                        }

                        Spacer(Modifier.height(2.dp))
                        Text("Calibration: show the standard starting position after capture begins.",
                            color = Muted, fontSize = 12.sp)
                        Text("For bot games and solo practice only. Do not use engine assistance in human games where it is prohibited.",
                            color = Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ModeButton(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Ink)) {
            Text(label)
        }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}
