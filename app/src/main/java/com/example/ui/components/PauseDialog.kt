package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedBorderSubtle
import com.example.ui.theme.SophisticatedCardBg
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedPrimaryContainer
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary

@Composable
fun PauseDialog(
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onReturnHome: () -> Unit,
    onToggleAudio: (Boolean, Boolean) -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("pause_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(SophisticatedPrimary, SophisticatedBorder)
                ),
                width = 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Paused",
                    color = SophisticatedPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sound toggle row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = null,
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Sound Effects",
                        color = SophisticatedTextPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { onToggleAudio(it, hapticsEnabled) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SophisticatedPrimary,
                            checkedTrackColor = SophisticatedPrimaryContainer,
                            uncheckedTrackColor = SophisticatedBorderSubtle
                        )
                    )
                }

                // Haptics toggle row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Vibration & Haptics",
                        color = SophisticatedTextPrimary,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { onToggleAudio(soundEnabled, it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SophisticatedPrimary,
                            checkedTrackColor = SophisticatedPrimaryContainer,
                            uncheckedTrackColor = SophisticatedBorderSubtle
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("resume_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedPrimary,
                        contentColor = Color(0xFF381E72)
                    )
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF381E72))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Resume", color = Color(0xFF381E72), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("restart_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SophisticatedTextPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null, tint = SophisticatedTextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Restart Match")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onReturnHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("exit_to_menu_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEFB8C8))
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color(0xFFEFB8C8))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Exit to Main Menu", color = Color(0xFFEFB8C8))
                }
            }
        }
    }
}
