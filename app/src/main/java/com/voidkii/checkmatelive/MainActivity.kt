package com.voidkii.checkmatelive

import android.app.Activity
import android.media.projection.MediaProjectionManager
import android.content.Intent
import android.os.Bundle
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

private val Ink = Color(0xFF101114)
private val Panel = Color(0xFF1A1C21)
private val Muted = Color(0xFF9A9DA7)
private val Accent = Color(0xFFE8E9ED)

class MainActivity : ComponentActivity() {
    private var captureState by mutableStateOf(false)

    private val capturePermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val serviceIntent = Intent(this, CaptureService::class.java).apply {
                putExtra(CaptureService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(CaptureService.EXTRA_RESULT_DATA, data)
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Ink
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            "CHECKMATE LIVE",
                            color = Muted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            "Your chess practice space.",
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Built for solo training and games against chess bots.",
                            color = Muted,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Panel, RoundedCornerShape(20.dp))
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("ANALYSIS MODE", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ModeButton("Offline", mode == "Offline") { mode = "Offline" }
                                ModeButton("Online", mode == "Online") { mode = "Online" }
                            }
                            Text(
                                if (mode == "Offline") "On-device analysis is the target. Stockfish integration is the next milestone."
                                else "Optional cloud analysis is planned. No account or API key is configured yet.",
                                color = Muted,
                                fontSize = 13.sp
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Panel, RoundedCornerShape(20.dp))
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("SCREEN CAPTURE", fontSize = 11.sp, color = Muted, fontWeight = FontWeight.Bold)
                            Text(
                                if (captureState) "Capture permission granted for this session."
                                else "Not active. Android will ask before any screen is captured.",
                                fontSize = 14.sp
                            )
                            Button(
                                onClick = {
                                    val manager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                                    capturePermission.launch(manager.createScreenCaptureIntent())
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Ink)
                            ) {
                                Text("Allow screen capture")
                            }
                            OutlinedButton(
                                onClick = {
                                    stopService(Intent(this@MainActivity, CaptureService::class.java))
                                    captureState = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Stop capture")
                            }
                        }

                        Spacer(Modifier.weight(1f))
                        Text(
                            "Stockfish: not integrated yet  •  Board recognition: next milestone",
                            color = Muted,
                            fontSize = 11.sp
                        )
                        Text(
                            "Use engine assistance only in bot games or solo practice, never in human games where it is prohibited.",
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun ModeButton(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Ink)
        ) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}
