package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchHistoryEntity
import com.example.data.UserStatsEntity
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedBorderSubtle
import com.example.ui.theme.SophisticatedCardBg
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedTertiary
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    userStats: UserStatsEntity?,
    recentMatches: List<MatchHistoryEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalMatches = userStats?.matchesPlayed ?: 0
    val totalWins = userStats?.matchesWon ?: 0
    val winRate = if (totalMatches > 0) ((totalWins.toFloat() / totalMatches) * 100).toInt() else 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Top App Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("stats_back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = SophisticatedPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CAREER & ACHIEVEMENTS",
                        color = SophisticatedPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Stat Summary Cards Grid
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatTile(
                            title = "Win Rate",
                            value = "$winRate%",
                            icon = Icons.Default.MilitaryTech,
                            accentColor = SophisticatedPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "Matches Won",
                            value = "$totalWins / $totalMatches",
                            icon = Icons.Default.EmojiEvents,
                            accentColor = SophisticatedSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatTile(
                            title = "Win Streak",
                            value = "${userStats?.winStreak ?: 0} (Best ${userStats?.bestWinStreak ?: 0})",
                            icon = Icons.Default.Timeline,
                            accentColor = SophisticatedTertiary,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "Queens Pocketed",
                            value = "${userStats?.queenPocketedCount ?: 0}",
                            icon = Icons.Default.Star,
                            accentColor = Color(0xFFFFD8E4),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recent Match History Header
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "RECENT MATCH HISTORY",
                    color = SophisticatedPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            if (recentMatches.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(SophisticatedBorder, SophisticatedBorderSubtle)),
                            width = 1.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No match records yet. Start playing Pass & Play or Vs AI to build your history!",
                                color = SophisticatedTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(recentMatches) { match ->
                    MatchHistoryCard(match = match)
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(accentColor.copy(alpha = 0.5f), SophisticatedBorder)
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, color = SophisticatedTextSecondary, fontSize = 12.sp)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = SophisticatedTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MatchHistoryCard(match: MatchHistoryEntity) {
    val dateStr = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(match.timestamp))
    val isP1Winner = match.winnerName == match.player1Name

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .testTag("match_item_${match.matchId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    if (isP1Winner) SophisticatedPrimary.copy(alpha = 0.6f) else SophisticatedBorder,
                    SophisticatedBorderSubtle
                )
            ),
            width = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = match.gameMode,
                    color = SophisticatedPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${match.player1Name} (${match.player1Score}) vs ${match.player2Name} (${match.player2Score})",
                    color = SophisticatedTextPrimary,
                    fontSize = 12.sp
                )
                Text(
                    text = dateStr,
                    color = SophisticatedTextMuted,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isP1Winner) "VICTORY" else "DEFEAT",
                    color = if (isP1Winner) SophisticatedPrimary else SophisticatedTertiary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+${match.coinsEarned}",
                        color = SophisticatedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
