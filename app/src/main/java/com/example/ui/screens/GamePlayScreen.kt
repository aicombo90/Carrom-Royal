package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.TrickShotLevel
import com.example.ui.components.CarromBoardCanvas
import com.example.ui.components.GameControls
import com.example.ui.components.GameOverDialog
import com.example.ui.components.PauseDialog
import com.example.ui.components.ScoreboardHeader
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.viewmodel.CarromGameViewModel
import com.example.viewmodel.GameUiState

@Composable
fun GamePlayScreen(
    state: GameUiState,
    viewModel: CarromGameViewModel,
    onReturnHome: () -> Unit,
    onNextTrickLevel: ((TrickShotLevel) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedDarkBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Scoreboard Header
            ScoreboardHeader(
                state = state,
                onPauseClick = { viewModel.togglePause() }
            )

            // 2. Central Carrom Board Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                CarromBoardCanvas(
                    boardTheme = state.boardTheme,
                    strikerTheme = state.strikerTheme,
                    pieces = state.pieces,
                    striker = state.striker,
                    isAiming = state.isAiming,
                    aimVector = state.strikerAimVector,
                    shotPower = state.shotPowerPercent,
                    particles = state.particles,
                    isPhysicsRunning = state.isPhysicsRunning,
                    isAiThinking = state.isAiThinking,
                    isTopPlayerTurn = state.currentPlayer == 2,
                    onAimChange = { dir, power -> viewModel.onAimVectorChange(dir, power) },
                    onAimRelease = { viewModel.onAimRelease() },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. Bottom Baseline Slider & Strike Controls
            GameControls(
                sliderFraction = state.strikerSliderFraction,
                shotPower = state.shotPowerPercent,
                isAiming = state.isAiming,
                isPhysicsRunning = state.isPhysicsRunning,
                isAiThinking = state.isAiThinking,
                isTopPlayerTurn = state.currentPlayer == 2,
                strikerTheme = state.strikerTheme,
                onSliderChange = { fraction -> viewModel.onStrikerSliderChange(fraction) },
                onReleaseShot = { viewModel.onAimRelease() }
            )
        }

        // Pause Menu Dialog
        if (state.isPaused) {
            val stats = viewModel.userStats.value
            PauseDialog(
                soundEnabled = stats?.soundEnabled ?: true,
                hapticsEnabled = stats?.hapticsEnabled ?: true,
                onResume = { viewModel.togglePause() },
                onRestart = {
                    viewModel.togglePause()
                    viewModel.startNewGame(
                        mode = state.gameMode,
                        p1Name = state.player1Name,
                        p2Name = state.player2Name,
                        difficulty = state.aiDifficulty,
                        trickLevel = state.trickShotLevel
                    )
                },
                onReturnHome = onReturnHome,
                onToggleAudio = { sound, haptics -> viewModel.toggleAudioSettings(sound, haptics) }
            )
        }

        // Game Over Dialog
        if (state.isGameOver) {
            val allLevels = TrickShotLevel.getLevels()
            val currentLevelId = state.trickShotLevel?.id ?: 0
            val nextLevel = allLevels.find { it.id == currentLevelId + 1 }

            GameOverDialog(
                state = state,
                onPlayAgain = {
                    viewModel.startNewGame(
                        mode = state.gameMode,
                        p1Name = state.player1Name,
                        p2Name = state.player2Name,
                        difficulty = state.aiDifficulty,
                        trickLevel = state.trickShotLevel
                    )
                },
                onNextLevel = if (nextLevel != null && onNextTrickLevel != null) {
                    { onNextTrickLevel(nextLevel) }
                } else null,
                onReturnHome = onReturnHome
            )
        }
    }
}
