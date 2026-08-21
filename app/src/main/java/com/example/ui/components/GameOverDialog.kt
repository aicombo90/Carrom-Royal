package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameMode
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedBorderSubtle
import com.example.ui.theme.SophisticatedCardBg
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun GameOverDialog(
    state: GameUiState,
    onPlayAgain: () -> Unit,
    onNextLevel: (() -> Unit)?,
    onReturnHome: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("game_over_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(SophisticatedPrimary, SophisticatedBorder)
                ),
                width = 1.5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy or Star Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(SophisticatedPrimary, SophisticatedCardBg)
                            )
                        )
                        .border(1.5.dp, SophisticatedPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = Color(0xFF381E72),
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = state.winnerName ?: "Match Ended",
                    color = SophisticatedPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (state.gameMode == GameMode.TRICK_SHOTS) "Challenge Completed" else "Final Score",
                    color = SophisticatedTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Score comparison or Stars
                if (state.gameMode == GameMode.TRICK_SHOTS) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            val earned = i <= state.trickShotStarsEarned
                            Icon(
                                imageVector = if (earned) Icons.Default.Star else Icons.Default.StarOutline,
                                contentDescription = null,
                                tint = if (earned) SophisticatedPrimary else SophisticatedBorderSubtle,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SophisticatedCardBg)
                            .border(1.dp, SophisticatedBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.player1Name, color = SophisticatedTextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${state.player1Score}",
                                color = SophisticatedPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = "VS", color = SophisticatedBorder, fontWeight = FontWeight.Bold)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.player2Name, color = SophisticatedTextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${state.player2Score}",
                                color = SophisticatedPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                if (onNextLevel != null && state.gameMode == GameMode.TRICK_SHOTS && state.trickShotStarsEarned > 0) {
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("next_level_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = Color(0xFF381E72)
                        )
                    ) {
                        Text(text = "Next Challenge", color = Color(0xFF381E72), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("play_again_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (onNextLevel != null && state.gameMode == GameMode.TRICK_SHOTS && state.trickShotStarsEarned > 0)
                            SophisticatedCardBg else SophisticatedPrimary,
                        contentColor = if (onNextLevel != null && state.gameMode == GameMode.TRICK_SHOTS && state.trickShotStarsEarned > 0)
                            SophisticatedTextPrimary else Color(0xFF381E72)
                    )
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Play Again", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onReturnHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("return_home_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SophisticatedPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = SophisticatedPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Main Menu", color = SophisticatedPrimary)
                }
            }
        }
    }
}
