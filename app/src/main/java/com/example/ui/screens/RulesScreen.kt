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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedCardBg
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedTertiary
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary

@Composable
fun RulesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SophisticatedDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("rules_back_button")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = SophisticatedPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CARROM RULES & PRO TIPS",
                        color = SophisticatedPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // 1. Piece Values & Scoring
            item {
                RuleSectionCard(
                    title = "1. Coin Values & Points",
                    icon = Icons.Default.Sports,
                    accentColor = SophisticatedPrimary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CoinValueItem("White Coin", "20 Pts", Color(0xFFE6E1E5), SophisticatedBorder)
                        CoinValueItem("Black Coin", "10 Pts", Color(0xFF212121), SophisticatedBorder)
                        CoinValueItem("Red Queen", "50 Pts", Color(0xFFFFD8E4), SophisticatedPrimary)
                    }
                }
            }

            // 2. The Queen & Cover Rule
            item {
                RuleSectionCard(
                    title = "2. The Queen & Cover Rule",
                    icon = Icons.Default.Info,
                    accentColor = SophisticatedTertiary
                ) {
                    Text(
                        text = "• The Queen (Red Coin) can be pocketed at any turn after sinking at least one coin.\n" +
                                "• CRITICAL COVER RULE: Immediately on the same shot or the very next consecutive shot, you MUST pocket another coin (White or Black) to 'Cover' the Queen.\n" +
                                "• If you miss the cover shot, the Queen is returned to the center circle of the board!",
                        color = SophisticatedTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            // 3. Foul Penalties
            item {
                RuleSectionCard(
                    title = "3. Fouls & Penalties",
                    icon = Icons.Default.Warning,
                    accentColor = Color(0xFFEFB8C8)
                ) {
                    Text(
                        text = "• Pocketing the Striker is a FOUL. You lose your turn, receive a -5 points deduction, and one of your pocketed coins is returned to the center as penalty.\n" +
                                "• Baseline placement: The striker must touch both parallel base lines or sit fully inside the red baseline circles.",
                        color = SophisticatedTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            // 4. Pro Shooting Tips
            item {
                RuleSectionCard(
                    title = "4. Pro Shooting Techniques",
                    icon = Icons.Default.Lightbulb,
                    accentColor = SophisticatedSecondary
                ) {
                    Text(
                        text = "• Rebound Bank Shot: Strike against the opposite side cushion at an angle to bounce into your baseline pocket.\n" +
                                "• Cut Shot: Strike the target piece at a thin glancing angle to slice it sideways along the cushion.\n" +
                                "• Double Cannon: Hit a blocker piece to propel a second piece directly into a corner pocket.",
                        color = SophisticatedTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SophisticatedCardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(accentColor.copy(alpha = 0.5f), SophisticatedBorder)
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = SophisticatedTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun CoinValueItem(name: String, points: String, color: Color, borderColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.5.dp, borderColor, CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = name, color = SophisticatedTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(text = points, color = SophisticatedPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
