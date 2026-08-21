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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.BoardTheme
import com.example.model.StrikerTheme
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
import com.example.viewmodel.CarromGameViewModel

@Composable
fun ShopScreen(
    userStats: UserStatsEntity?,
    viewModel: CarromGameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val unlockedBoards = (userStats?.unlockedBoardsCsv ?: "classic_mahogany").split(",")
    val unlockedStrikers = (userStats?.unlockedStrikersCsv ?: "classic_ivory").split(",")
    val currentBoardId = userStats?.selectedBoardId ?: "classic_mahogany"
    val currentStrikerId = userStats?.selectedStrikerId ?: "classic_ivory"
    val coins = userStats?.coins ?: 1000

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("shop_back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = SophisticatedPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ROYALE SHOP",
                        color = SophisticatedPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                // Coin Badge
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
                        text = "$coins",
                        color = SophisticatedTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tabs: Strikers vs Boards
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SophisticatedSurface,
                contentColor = SophisticatedPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SophisticatedPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "STRIKERS",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) SophisticatedPrimary else SophisticatedTextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "BOARDS",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) SophisticatedPrimary else SophisticatedTextSecondary
                        )
                    }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    // Strikers List
                    items(StrikerTheme.ALL_STRIKERS) { striker ->
                        val isUnlocked = unlockedStrikers.contains(striker.id)
                        val isSelected = currentStrikerId == striker.id

                        StrikerShopCard(
                            striker = striker,
                            isUnlocked = isUnlocked,
                            isSelected = isSelected,
                            canAfford = coins >= striker.price,
                            onSelect = { viewModel.selectStrikerTheme(striker) },
                            onUnlock = { viewModel.unlockStriker(striker) {} }
                        )
                    }
                } else {
                    // Boards List
                    items(BoardTheme.ALL_BOARDS) { board ->
                        val isUnlocked = unlockedBoards.contains(board.id)
                        val isSelected = currentBoardId == board.id

                        BoardShopCard(
                            board = board,
                            isUnlocked = isUnlocked,
                            isSelected = isSelected,
                            canAfford = coins >= board.price,
                            onSelect = { viewModel.selectBoardTheme(board) },
                            onUnlock = { viewModel.unlockBoard(board) {} }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StrikerShopCard(
    striker: StrikerTheme,
    isUnlocked: Boolean,
    isSelected: Boolean,
    canAfford: Boolean,
    onSelect: () -> Unit,
    onUnlock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("striker_item_${striker.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isSelected) {
                Brush.horizontalGradient(listOf(SophisticatedPrimary, SophisticatedSecondary))
            } else {
                Brush.horizontalGradient(listOf(SophisticatedBorder, SophisticatedBorderSubtle))
            },
            width = if (isSelected) 1.5.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Striker Visual Disc Preview
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(striker.primaryColor, striker.secondaryColor)
                        )
                    )
                    .border(2.dp, striker.ringColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(striker.ringColor)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = striker.name,
                    color = SophisticatedTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = striker.description,
                    color = SophisticatedTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stats: Power & Aim Guide Length
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = SophisticatedPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Power: ${(striker.powerMultiplier * 100).toInt()}%",
                        color = SophisticatedTextSecondary,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = null,
                        tint = SophisticatedSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Aim: ${(striker.aimLengthMultiplier * 100).toInt()}%",
                        color = SophisticatedTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isSelected) {
                Button(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF381E72))
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SophisticatedPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "EQUIPPED", color = SophisticatedPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isUnlocked) {
                Button(
                    onClick = onSelect,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SophisticatedBorder)
                ) {
                    Text(text = "EQUIP", color = SophisticatedPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onUnlock,
                    enabled = canAfford,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedPrimary,
                        contentColor = Color(0xFF381E72),
                        disabledContainerColor = SophisticatedBorderSubtle,
                        disabledContentColor = SophisticatedTextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = if (canAfford) Color(0xFF381E72) else SophisticatedTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${striker.price}",
                        color = if (canAfford) Color(0xFF381E72) else SophisticatedTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BoardShopCard(
    board: BoardTheme,
    isUnlocked: Boolean,
    isSelected: Boolean,
    canAfford: Boolean,
    onSelect: () -> Unit,
    onUnlock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("board_item_${board.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isSelected) {
                Brush.horizontalGradient(listOf(SophisticatedPrimary, SophisticatedSecondary))
            } else {
                Brush.horizontalGradient(listOf(SophisticatedBorder, SophisticatedBorderSubtle))
            },
            width = if (isSelected) 1.5.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Board Felt Color Thumbnail Preview
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(board.borderColor)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .background(board.surfaceColor),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .border(1.dp, board.innerPatternColor, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = board.name,
                    color = SophisticatedTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = board.description,
                    color = SophisticatedTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isSelected) {
                Button(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF381E72))
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SophisticatedPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "EQUIPPED", color = SophisticatedPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isUnlocked) {
                Button(
                    onClick = onSelect,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SophisticatedBorder)
                ) {
                    Text(text = "EQUIP", color = SophisticatedPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onUnlock,
                    enabled = canAfford,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedPrimary,
                        contentColor = Color(0xFF381E72),
                        disabledContainerColor = SophisticatedBorderSubtle,
                        disabledContentColor = SophisticatedTextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = if (canAfford) Color(0xFF381E72) else SophisticatedTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${board.price}",
                        color = if (canAfford) Color(0xFF381E72) else SophisticatedTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
