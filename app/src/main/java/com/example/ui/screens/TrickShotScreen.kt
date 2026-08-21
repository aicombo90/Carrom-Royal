package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
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
import com.example.data.UserStatsEntity
import com.example.model.TrickShotLevel
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedBorderSubtle
import com.example.ui.theme.SophisticatedCardBg
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedSurface
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary

@Composable
fun TrickShotScreen(
    userStats: UserStatsEntity?,
    onSelectLevel: (TrickShotLevel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = TrickShotLevel.getLevels()
    val starsMap = (userStats?.trickShotStarsCsv ?: "").split(",")
        .filter { it.contains(":") }
        .associate {
            val parts = it.split(":")
            parts[0].toInt() to parts[1].toInt()
        }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedDarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("trick_shots_back_button")) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = SophisticatedPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "TRICK SHOT PUZZLES",
                        color = SophisticatedPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Master bank shots, ricochets & split combinations",
                        color = SophisticatedTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(levels) { level ->
                    val stars = starsMap[level.id] ?: 0
                    val isUnlocked = level.id == 1 || (starsMap[level.id - 1] ?: 0) > 0

                    TrickShotLevelCard(
                        level = level,
                        stars = stars,
                        isUnlocked = isUnlocked,
                        onClick = { if (isUnlocked) onSelectLevel(level) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrickShotLevelCard(
    level: TrickShotLevel,
    stars: Int,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("level_card_${level.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) SophisticatedCardBg else SophisticatedSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isUnlocked) {
                Brush.verticalGradient(listOf(SophisticatedPrimary.copy(alpha = 0.6f), SophisticatedBorder))
            } else {
                Brush.verticalGradient(listOf(SophisticatedBorderSubtle, SophisticatedBorderSubtle))
            },
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) SophisticatedCardBg else SophisticatedSurface
                        )
                        .border(
                            1.dp,
                            if (isUnlocked) SophisticatedPrimary else SophisticatedBorderSubtle,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${level.id}",
                        color = if (isUnlocked) SophisticatedPrimary else SophisticatedTextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = SophisticatedTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = SophisticatedPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "+${level.coinReward}",
                            color = SophisticatedPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = level.title,
                color = if (isUnlocked) SophisticatedTextPrimary else SophisticatedTextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = level.subtitle,
                color = if (isUnlocked) SophisticatedTextSecondary else SophisticatedTextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Stars row
            Row(horizontalArrangement = Arrangement.Center) {
                for (i in 1..3) {
                    val earned = i <= stars
                    Icon(
                        imageVector = if (earned) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = null,
                        tint = if (earned) SophisticatedPrimary else SophisticatedBorderSubtle,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
