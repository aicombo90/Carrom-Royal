package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.PieceType
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedCardBg
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun ScoreboardHeader(
    state: GameUiState,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("scoreboard_header")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = SophisticatedCardBg.copy(alpha = 0.95f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                    listOf(SophisticatedPrimary.copy(alpha = 0.7f), SophisticatedBorder, SophisticatedPrimary.copy(alpha = 0.7f))
                ),
                width = 1.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Player 1 Card (Bottom Player)
                PlayerScoreBadge(
                    name = state.player1Name,
                    score = state.player1Score,
                    isActiveTurn = state.currentPlayer == 1 && !state.isGameOver,
                    whiteCoins = state.player1PocketedWhite,
                    blackCoins = state.player1PocketedBlack,
                    queenPocketed = state.queenPocketedBy == 1,
                    assignedColor = if (state.gameMode == GameMode.DISC_POOL) state.player1AssignedColor else null,
                    isBottom = true
                )

                // Match Mode & Pause / Star counter
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.gameMode.displayName.uppercase(),
                        color = SophisticatedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    if (state.gameMode == GameMode.TRICK_SHOTS && state.trickShotLevel != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SophisticatedPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Shot ${state.trickShotShotsTaken}/${state.trickShotLevel.maxShots}",
                                color = SophisticatedTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    IconButton(
                        onClick = onPauseClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("pause_menu_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(SophisticatedCardBg)
                                .border(1.dp, SophisticatedBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pause Game",
                                tint = SophisticatedPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Player 2 Card (Top Player / AI)
                PlayerScoreBadge(
                    name = state.player2Name,
                    score = state.player2Score,
                    isActiveTurn = state.currentPlayer == 2 && !state.isGameOver,
                    whiteCoins = state.player2PocketedWhite,
                    blackCoins = state.player2PocketedBlack,
                    queenPocketed = state.queenPocketedBy == 2,
                    assignedColor = if (state.gameMode == GameMode.DISC_POOL) state.player2AssignedColor else null,
                    isBottom = false
                )
            }
        }

        // Queen Cover or Alert Banner
        AnimatedVisibility(
            visible = state.alertMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (state.alertMessage?.contains("FOUL", ignoreCase = true) == true)
                            Color(0xFF8C1D18).copy(alpha = 0.95f)
                        else if (state.isQueenCoverRequired)
                            Color(0xFF601410).copy(alpha = 0.95f)
                        else Color(0xFF2E7D32).copy(alpha = 0.85f)
                    )
                    .padding(vertical = 5.dp, horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.alertMessage ?: "",
                    color = SophisticatedTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlayerScoreBadge(
    name: String,
    score: Int,
    isActiveTurn: Boolean,
    whiteCoins: Int,
    blackCoins: Int,
    queenPocketed: Boolean,
    assignedColor: PieceType?,
    isBottom: Boolean
) {
    Column(
        horizontalAlignment = if (isBottom) Alignment.Start else Alignment.End
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isActiveTurn) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SophisticatedPrimary)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = name,
                color = if (isActiveTurn) SophisticatedPrimary else SophisticatedTextSecondary,
                fontSize = 13.sp,
                fontWeight = if (isActiveTurn) FontWeight.Bold else FontWeight.Normal
            )
        }

        Text(
            text = "$score PTS",
            color = SophisticatedTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        // Pocketed coins counter row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // White coin icon & count
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE6E1E5))
                        .border(0.5.dp, SophisticatedBorder, CircleShape)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "$whiteCoins", color = SophisticatedTextSecondary, fontSize = 10.sp)
            }

            // Black coin icon & count
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF212121))
                        .border(0.5.dp, SophisticatedBorder, CircleShape)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "$blackCoins", color = SophisticatedTextSecondary, fontSize = 10.sp)
            }

            if (queenPocketed) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD8E4))
                        .border(0.5.dp, SophisticatedPrimary, CircleShape)
                )
            }

            if (assignedColor != null) {
                Text(
                    text = "(${if (assignedColor == PieceType.WHITE) "W" else "B"})",
                    color = SophisticatedPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
