package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.UserStatsEntity
import com.example.model.AiDifficulty
import com.example.model.GameMode
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedCardBg
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedSurfaceElevated
import com.example.ui.theme.SophisticatedTertiary
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary

@Composable
fun MainMenuScreen(
    userStats: UserStatsEntity?,
    onStartGame: (GameMode, AiDifficulty) -> Unit,
    onNavigateToTrickShots: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToRules: () -> Unit,
    onClaimDailyReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(AiDifficulty.MEDIUM) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Top Header Bar: Level, Coins, Daily Bonus
            item {
                TopPlayerHeader(
                    userStats = userStats,
                    onClaimDailyReward = onClaimDailyReward
                )
            }

            // 2. Hero Banner
            item {
                HeroBanner()
            }

            // 3. Game Mode Cards
            item {
                Text(
                    text = "SELECT GAME MODE",
                    color = SophisticatedPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Pass & Play (2 Players)
                    GameModeCard(
                        title = "Pass & Play (2 Players)",
                        subtitle = "Classic 2-player match on single device",
                        icon = Icons.Default.Group,
                        accentColor = SophisticatedPrimary,
                        tag = "mode_pass_and_play",
                        onClick = { onStartGame(GameMode.PASS_AND_PLAY, AiDifficulty.MEDIUM) }
                    )

                    // Vs AI Bot
                    GameModeCard(
                        title = "Vs AI Bot",
                        subtitle = "Play against Smart Bot with customizable difficulty",
                        icon = Icons.Default.Psychology,
                        accentColor = SophisticatedSecondary,
                        tag = "mode_vs_ai",
                        onClick = { showDifficultyDialog = true }
                    )

                    // Disc Pool (Carrom Pool)
                    GameModeCard(
                        title = "Disc Pool (Carrom Pool)",
                        subtitle = "Fast-paced color elimination disc match",
                        icon = Icons.Default.SportsEsports,
                        accentColor = SophisticatedTertiary,
                        tag = "mode_disc_pool",
                        onClick = { onStartGame(GameMode.DISC_POOL, AiDifficulty.MEDIUM) }
                    )

                    // Trick Shots & Puzzles
                    GameModeCard(
                        title = "Trick Shots & Puzzles",
                        subtitle = "Master bank shots, combos & clear puzzles",
                        icon = Icons.Default.Star,
                        accentColor = Color(0xFFFFD8E4),
                        tag = "mode_trick_shots",
                        onClick = onNavigateToTrickShots
                    )

                    // Free Practice Mode
                    GameModeCard(
                        title = "Free Practice Board",
                        subtitle = "Infinite shots with trajectory practice",
                        icon = Icons.Default.PlayArrow,
                        accentColor = Color(0xFFA8E6CF),
                        tag = "mode_practice",
                        onClick = { onStartGame(GameMode.PRACTICE, AiDifficulty.EASY) }
                    )
                }
            }

            // 4. Quick Navigation Hub
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "CUSTOMIZE & STATS",
                    color = SophisticatedPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubButton(
                        title = "Board Shop",
                        icon = Icons.Default.ShoppingBag,
                        modifier = Modifier.weight(1f),
                        tag = "shop_nav_button",
                        onClick = onNavigateToShop
                    )
                    HubButton(
                        title = "Career Stats",
                        icon = Icons.Default.Leaderboard,
                        modifier = Modifier.weight(1f),
                        tag = "stats_nav_button",
                        onClick = onNavigateToStats
                    )
                    HubButton(
                        title = "Rules & Tips",
                        icon = Icons.Default.Book,
                        modifier = Modifier.weight(1f),
                        tag = "rules_nav_button",
                        onClick = onNavigateToRules
                    )
                }
            }
        }
    }

    // AI Difficulty Selection Dialog
    if (showDifficultyDialog) {
        Dialog(onDismissRequest = { showDifficultyDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SophisticatedSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.verticalGradient(
                        listOf(SophisticatedPrimary, SophisticatedBorder)
                    ),
                    width = 1.dp
                ),
                modifier = Modifier.testTag("ai_difficulty_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Bot Difficulty",
                        color = SophisticatedTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    AiDifficulty.values().forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedDifficulty = diff },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SophisticatedCardBg else SophisticatedSurfaceElevated
                            ),
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(SophisticatedPrimary, SophisticatedSecondary)),
                                width = 1.5.dp
                            ) else CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(SophisticatedBorder, SophisticatedBorder)),
                                width = 0.5.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = diff.label,
                                    color = if (isSelected) SophisticatedPrimary else SophisticatedTextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SophisticatedPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showDifficultyDialog = false
                            onStartGame(GameMode.VS_AI, selectedDifficulty)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("start_ai_match_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SophisticatedPrimary,
                            contentColor = SophisticatedDarkBackground
                        )
                    ) {
                        Text(
                            text = "Start AI Match",
                            color = Color(0xFF381E72),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopPlayerHeader(
    userStats: UserStatsEntity?,
    onClaimDailyReward: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Player Level & Avatar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SophisticatedCardBg)
                    .border(1.dp, SophisticatedPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = SophisticatedPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Carrom Master",
                    color = SophisticatedTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Level ${userStats?.playerLevel ?: 1} (${userStats?.winStreak ?: 0} Streak)",
                    color = SophisticatedPrimary,
                    fontSize = 11.sp
                )
            }
        }

        // Coins & Free Gift
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Coins badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SophisticatedCardBg)
                    .border(1.dp, SophisticatedBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Coins",
                    tint = SophisticatedPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${userStats?.coins ?: 1000}",
                    color = SophisticatedTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Daily Gift button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SophisticatedCardBg)
                    .border(1.dp, SophisticatedPrimary, CircleShape)
                    .clickable(onClick = onClaimDailyReward)
                    .testTag("daily_reward_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Daily Reward",
                    tint = SophisticatedPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(SophisticatedPrimary.copy(alpha = 0.6f), SophisticatedBorder)
            ),
            width = 1.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.carrom_hero_bg_1787297443862),
                contentDescription = "Carrom Royale Arena",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay for clear contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                SophisticatedDarkBackground.copy(alpha = 0.95f),
                                SophisticatedDarkBackground.copy(alpha = 0.75f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "CARROM ROYALE",
                    color = SophisticatedPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Experience authentic Indian Carrom with sub-step 2D physics & pro trick shots.",
                    color = SophisticatedTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
    }
}

@Composable
private fun GameModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(accentColor.copy(alpha = 0.5f), SophisticatedBorder)
            ),
            width = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = SophisticatedTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = SophisticatedTextSecondary,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun HubButton(
    title: String,
    icon: ImageVector,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(SophisticatedPrimary.copy(alpha = 0.4f), SophisticatedBorder)
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SophisticatedPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = SophisticatedTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
