package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AiDifficulty
import com.example.model.GameMode
import com.example.model.TrickShotLevel
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.RulesScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TrickShotScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CarromGameViewModel

enum class AppScreen {
    MAIN_MENU,
    GAME_PLAY,
    TRICK_SHOTS,
    SHOP,
    STATS,
    RULES
}

class MainActivity : ComponentActivity() {
    private val viewModel: CarromGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CarromRoyaleApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CarromRoyaleApp(viewModel: CarromGameViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.MAIN_MENU) }
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val gameState by viewModel.uiState.collectAsStateWithLifecycle()
    val recentMatches by viewModel.recentMatches.collectAsStateWithLifecycle()

    BackHandler(enabled = currentScreen != AppScreen.MAIN_MENU) {
        currentScreen = AppScreen.MAIN_MENU
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {
            AppScreen.MAIN_MENU -> {
                MainMenuScreen(
                    userStats = userStats,
                    onStartGame = { mode, diff ->
                        viewModel.startNewGame(mode = mode, difficulty = diff)
                        currentScreen = AppScreen.GAME_PLAY
                    },
                    onNavigateToTrickShots = { currentScreen = AppScreen.TRICK_SHOTS },
                    onNavigateToShop = { currentScreen = AppScreen.SHOP },
                    onNavigateToStats = { currentScreen = AppScreen.STATS },
                    onNavigateToRules = { currentScreen = AppScreen.RULES },
                    onClaimDailyReward = { viewModel.claimDailyReward() },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.GAME_PLAY -> {
                GamePlayScreen(
                    state = gameState,
                    viewModel = viewModel,
                    onReturnHome = { currentScreen = AppScreen.MAIN_MENU },
                    onNextTrickLevel = { nextLevel ->
                        viewModel.startNewGame(
                            mode = GameMode.TRICK_SHOTS,
                            trickLevel = nextLevel
                        )
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.TRICK_SHOTS -> {
                TrickShotScreen(
                    userStats = userStats,
                    onSelectLevel = { level ->
                        viewModel.startNewGame(
                            mode = GameMode.TRICK_SHOTS,
                            trickLevel = level
                        )
                        currentScreen = AppScreen.GAME_PLAY
                    },
                    onBack = { currentScreen = AppScreen.MAIN_MENU },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.SHOP -> {
                ShopScreen(
                    userStats = userStats,
                    viewModel = viewModel,
                    onBack = { currentScreen = AppScreen.MAIN_MENU },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.STATS -> {
                StatsScreen(
                    userStats = userStats,
                    recentMatches = recentMatches,
                    onBack = { currentScreen = AppScreen.MAIN_MENU },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.RULES -> {
                RulesScreen(
                    onBack = { currentScreen = AppScreen.MAIN_MENU },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
