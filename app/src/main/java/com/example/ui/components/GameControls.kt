package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.model.StrikerTheme
import com.example.ui.theme.SophisticatedBorder
import com.example.ui.theme.SophisticatedBorderSubtle
import com.example.ui.theme.SophisticatedCardBg
import com.example.ui.theme.SophisticatedDarkBackground
import com.example.ui.theme.SophisticatedPrimary
import com.example.ui.theme.SophisticatedSecondary
import com.example.ui.theme.SophisticatedTextMuted
import com.example.ui.theme.SophisticatedTextPrimary
import com.example.ui.theme.SophisticatedTextSecondary

@Composable
fun GameControls(
    sliderFraction: Float,
    shotPower: Float,
    isAiming: Boolean,
    isPhysicsRunning: Boolean,
    isAiThinking: Boolean,
    isTopPlayerTurn: Boolean,
    strikerTheme: StrikerTheme,
    onSliderChange: (Float) -> Unit,
    onReleaseShot: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("game_controls_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SophisticatedCardBg.copy(alpha = 0.95f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(SophisticatedPrimary.copy(alpha = 0.6f), SophisticatedBorder, SophisticatedPrimary.copy(alpha = 0.6f))
            ),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Striker Position Slider on Baseline
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(strikerTheme.primaryColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Striker Baseline",
                        color = SophisticatedTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Fine-tune nudge buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onSliderChange((sliderFraction - 0.05f).coerceIn(0f, 1f)) },
                        enabled = !isPhysicsRunning && !isAiThinking,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("nudge_left_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Nudge Left",
                            tint = if (!isPhysicsRunning && !isAiThinking) SophisticatedPrimary else SophisticatedTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { onSliderChange((sliderFraction + 0.05f).coerceIn(0f, 1f)) },
                        enabled = !isPhysicsRunning && !isAiThinking,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("nudge_right_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Nudge Right",
                            tint = if (!isPhysicsRunning && !isAiThinking) SophisticatedPrimary else SophisticatedTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Slider(
                value = sliderFraction,
                onValueChange = onSliderChange,
                enabled = !isPhysicsRunning && !isAiThinking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .testTag("striker_position_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = SophisticatedPrimary,
                    activeTrackColor = SophisticatedPrimary,
                    inactiveTrackColor = SophisticatedBorderSubtle
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Shot Power Indicator & Drag to aim instruction
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAiming) "Power: ${shotPower.toInt()}%" else "Drag striker on board to aim & pull!",
                            color = if (isAiming) SophisticatedPrimary else SophisticatedTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isAiming) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (shotPower / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (shotPower > 75f) Color(0xFFEFB8C8) else SophisticatedPrimary,
                        trackColor = SophisticatedBorderSubtle
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Direct Strike Button
                Button(
                    onClick = onReleaseShot,
                    enabled = !isPhysicsRunning && !isAiThinking,
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("strike_action_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SophisticatedPrimary,
                        contentColor = Color(0xFF381E72),
                        disabledContainerColor = SophisticatedBorderSubtle,
                        disabledContentColor = SophisticatedTextMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAiThinking) "Thinking..." else "STRIKE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
