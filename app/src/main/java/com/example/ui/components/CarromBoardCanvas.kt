package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.model.BoardTheme
import com.example.model.CarromPhysics
import com.example.model.CarromPiece
import com.example.model.Particle
import com.example.model.PieceType
import com.example.model.StrikerTheme
import com.example.model.Vector2D
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CarromBoardCanvas(
    boardTheme: BoardTheme,
    strikerTheme: StrikerTheme,
    pieces: List<CarromPiece>,
    striker: CarromPiece?,
    isAiming: Boolean,
    aimVector: Vector2D,
    shotPower: Float,
    particles: List<Particle>,
    isPhysicsRunning: Boolean,
    isAiThinking: Boolean,
    isTopPlayerTurn: Boolean,
    onAimChange: (Vector2D, Float) -> Unit,
    onAimRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dashEffect = remember { PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f) }

    BoxWithConstraints(modifier = modifier.aspectRatio(1f)) {
        val canvasSize = constraints.maxWidth.toFloat()
        val scale = canvasSize / CarromPhysics.BOARD_SIZE

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(isPhysicsRunning, isAiThinking, striker) {
                    if (isPhysicsRunning || isAiThinking || striker == null) return@pointerInput

                    detectDragGestures(
                        onDragStart = { offset ->
                            val sPosCanvas = Offset(striker.position.x * scale, striker.position.y * scale)
                            val dragVec = Offset(sPosCanvas.x - offset.x, sPosCanvas.y - offset.y)
                            val dist = dragVec.getDistance()
                            val power = (dist / (80f * scale) * 100f).coerceIn(10f, 100f)
                            val dir = if (dist > 5f) Vector2D(dragVec.x, dragVec.y).normalized()
                            else Vector2D(0f, if (isTopPlayerTurn) 1f else -1f)
                            onAimChange(dir, power)
                        },
                        onDrag = { change, _ ->
                            val sPosCanvas = Offset(striker.position.x * scale, striker.position.y * scale)
                            val dragVec = Offset(sPosCanvas.x - change.position.x, sPosCanvas.y - change.position.y)
                            val dist = dragVec.getDistance()
                            val power = (dist / (80f * scale) * 100f).coerceIn(10f, 100f)
                            val dir = if (dist > 5f) Vector2D(dragVec.x, dragVec.y).normalized()
                            else Vector2D(0f, if (isTopPlayerTurn) 1f else -1f)
                            onAimChange(dir, power)
                        },
                        onDragEnd = {
                            onAimRelease()
                        },
                        onDragCancel = {
                            onAimRelease()
                        }
                    )
                }
        ) {
            // 1. Draw Wooden Border & Frame
            drawBoardBorder(boardTheme, scale)

            // 2. Draw Playing Surface & Classical Markings
            drawBoardSurface(boardTheme, scale)

            // 3. Draw Corner Pockets
            drawPockets(boardTheme, scale)

            // 4. Draw Classical Baselines & Circles
            drawBaselinesAndOrnaments(boardTheme, scale)

            // 5. Draw Aiming Guides (if aiming)
            if (isAiming && striker != null && !isPhysicsRunning) {
                drawAimGuide(
                    striker = striker,
                    aimVector = aimVector,
                    power = shotPower,
                    pieces = pieces,
                    strikerTheme = strikerTheme,
                    scale = scale,
                    dashEffect = dashEffect
                )
            }

            // 6. Draw Carrom Men (Coins)
            for (piece in pieces) {
                if (!piece.isPocketed) {
                    drawCarromPiece(piece, scale)
                }
            }

            // 7. Draw Striker
            if (striker != null && !striker.isPocketed) {
                drawStriker(striker, strikerTheme, isAiming, shotPower, scale)
            }

            // 8. Draw Particle Effects
            for (p in particles) {
                drawCircle(
                    color = Color(p.color).copy(alpha = p.alpha),
                    radius = p.radius * scale,
                    center = Offset(p.position.x * scale, p.position.y * scale)
                )
            }
        }
    }
}

private fun DrawScope.drawBoardBorder(theme: BoardTheme, scale: Float) {
    val totalSize = CarromPhysics.BOARD_SIZE * scale
    val cushionW = CarromPhysics.CUSHION_MARGIN * scale

    // Dark rich frame shadow
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(theme.borderColor.copy(alpha = 0.9f), theme.borderColor),
            center = Offset(totalSize / 2, totalSize / 2),
            radius = totalSize * 0.7f
        ),
        topLeft = Offset(0f, 0f),
        size = Size(totalSize, totalSize)
    )

    // Inner bevel gradient on wood cushion
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0x44FFFFFF), Color(0x00000000), Color(0x66000000)),
            start = Offset(0f, 0f),
            end = Offset(totalSize, totalSize)
        ),
        topLeft = Offset(0f, 0f),
        size = Size(totalSize, totalSize)
    )
}

private fun DrawScope.drawBoardSurface(theme: BoardTheme, scale: Float) {
    val playMin = CarromPhysics.PLAY_AREA_MIN * scale
    val playMax = CarromPhysics.PLAY_AREA_MAX * scale
    val playSize = playMax - playMin

    // Playing surface
    drawRect(
        color = theme.surfaceColor,
        topLeft = Offset(playMin, playMin),
        size = Size(playSize, playSize)
    )

    // Subtle surface sheen / felt texture gradient
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x22FFFFFF), Color(0x00000000), Color(0x33000000)),
            center = Offset(CarromPhysics.BOARD_SIZE * scale * 0.5f, CarromPhysics.BOARD_SIZE * scale * 0.5f),
            radius = playSize * 0.65f
        ),
        topLeft = Offset(playMin, playMin),
        size = Size(playSize, playSize)
    )

    // Inner playing boundary thin border
    drawRect(
        color = theme.innerPatternColor.copy(alpha = 0.45f),
        topLeft = Offset(playMin, playMin),
        size = Size(playSize, playSize),
        style = Stroke(width = 2.5f * scale)
    )
}

private fun DrawScope.drawPockets(theme: BoardTheme, scale: Float) {
    val pRadius = CarromPhysics.POCKET_RADIUS * scale

    for (p in CarromPhysics.POCKETS) {
        val center = Offset(p.x * scale, p.y * scale)

        // Brass/Metal Outer Rim
        drawCircle(
            color = theme.pocketRimColor,
            radius = pRadius + 5f * scale,
            center = center
        )

        // Pocket Net Deep Hole
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF000000), theme.pocketNetColor),
                center = center,
                radius = pRadius
            ),
            radius = pRadius,
            center = center
        )

        // Netting criss-cross lines inside pocket
        val netColor = Color(0x33FFFFFF)
        drawLine(
            color = netColor,
            start = Offset(center.x - pRadius * 0.7f, center.y - pRadius * 0.7f),
            end = Offset(center.x + pRadius * 0.7f, center.y + pRadius * 0.7f),
            strokeWidth = 1.5f * scale
        )
        drawLine(
            color = netColor,
            start = Offset(center.x - pRadius * 0.7f, center.y + pRadius * 0.7f),
            end = Offset(center.x + pRadius * 0.7f, center.y - pRadius * 0.7f),
            strokeWidth = 1.5f * scale
        )
    }
}

private fun DrawScope.drawBaselinesAndOrnaments(theme: BoardTheme, scale: Float) {
    val lineColor = theme.innerPatternColor
    val strokeWidth = 2.2f * scale
    val center = Offset(500f * scale, 500f * scale)

    // 1. Center floral ornament and concentric circles
    // Outer circle
    drawCircle(
        color = lineColor.copy(alpha = 0.7f),
        radius = 80f * scale,
        center = center,
        style = Stroke(width = strokeWidth)
    )
    // Red Center Circle
    drawCircle(
        color = theme.centerCircleColor.copy(alpha = 0.25f),
        radius = 32f * scale,
        center = center,
        style = Fill
    )
    drawCircle(
        color = theme.centerCircleColor,
        radius = 32f * scale,
        center = center,
        style = Stroke(width = 2.5f * scale)
    )
    // Center Red Dot
    drawCircle(
        color = theme.centerCircleColor,
        radius = 6.5f * scale,
        center = center
    )

    // Center star rays
    for (i in 0 until 8) {
        val angle = (i * PI / 4.0).toFloat()
        val p1 = Offset(center.x + cos(angle) * 35f * scale, center.y + sin(angle) * 35f * scale)
        val p2 = Offset(center.x + cos(angle) * 75f * scale, center.y + sin(angle) * 75f * scale)
        drawLine(color = lineColor.copy(alpha = 0.6f), start = p1, end = p2, strokeWidth = 1.5f * scale)
    }

    // 2. Four Baselines (Bottom, Top, Left, Right)
    val bMin = CarromPhysics.BASELINE_MIN_X * scale
    val bMax = CarromPhysics.BASELINE_MAX_X * scale
    val bBotY = CarromPhysics.BASELINE_BOTTOM_Y * scale
    val bTopY = CarromPhysics.BASELINE_TOP_Y * scale
    val bLeftX = CarromPhysics.BASELINE_TOP_Y * scale
    val bRightX = CarromPhysics.BASELINE_BOTTOM_Y * scale

    val circleR = 26f * scale
    val redCircleColor = Color(0xFFD32F2F)

    // Draw baseline pair and endpoints
    fun drawBaselinePair(x1: Float, y1: Float, x2: Float, y2: Float, isHorizontal: Boolean) {
        val offsetDist = 18f * scale
        if (isHorizontal) {
            drawLine(lineColor, Offset(x1, y1 - offsetDist / 2), Offset(x2, y1 - offsetDist / 2), strokeWidth)
            drawLine(lineColor, Offset(x1, y1 + offsetDist / 2), Offset(x2, y1 + offsetDist / 2), strokeWidth)

            // Red End Circles
            drawCircle(redCircleColor, circleR, Offset(x1, y1), style = Stroke(width = strokeWidth))
            drawCircle(redCircleColor.copy(alpha = 0.35f), circleR, Offset(x1, y1), style = Fill)
            drawCircle(redCircleColor, 4.5f * scale, Offset(x1, y1))

            drawCircle(redCircleColor, circleR, Offset(x2, y2), style = Stroke(width = strokeWidth))
            drawCircle(redCircleColor.copy(alpha = 0.35f), circleR, Offset(x2, y2), style = Fill)
            drawCircle(redCircleColor, 4.5f * scale, Offset(x2, y2))
        } else {
            drawLine(lineColor, Offset(x1 - offsetDist / 2, y1), Offset(x2 - offsetDist / 2, y2), strokeWidth)
            drawLine(lineColor, Offset(x1 + offsetDist / 2, y1), Offset(x2 + offsetDist / 2, y2), strokeWidth)

            drawCircle(redCircleColor, circleR, Offset(x1, y1), style = Stroke(width = strokeWidth))
            drawCircle(redCircleColor.copy(alpha = 0.35f), circleR, Offset(x1, y1), style = Fill)
            drawCircle(redCircleColor, 4.5f * scale, Offset(x1, y1))

            drawCircle(redCircleColor, circleR, Offset(x2, y2), style = Stroke(width = strokeWidth))
            drawCircle(redCircleColor.copy(alpha = 0.35f), circleR, Offset(x2, y2), style = Fill)
            drawCircle(redCircleColor, 4.5f * scale, Offset(x2, y2))
        }
    }

    drawBaselinePair(bMin, bBotY, bMax, bBotY, isHorizontal = true)
    drawBaselinePair(bMin, bTopY, bMax, bTopY, isHorizontal = true)
    drawBaselinePair(bLeftX, bMin, bLeftX, bMax, isHorizontal = false)
    drawBaselinePair(bRightX, bMin, bRightX, bMax, isHorizontal = false)

    // 3. Four Corner Arrow Lines pointing to pockets
    val arrowColor = theme.arrowColor.copy(alpha = 0.75f)
    val arrows = listOf(
        Pair(Offset(320f * scale, 320f * scale), Offset(150f * scale, 150f * scale)),
        Pair(Offset(680f * scale, 320f * scale), Offset(850f * scale, 150f * scale)),
        Pair(Offset(320f * scale, 680f * scale), Offset(150f * scale, 850f * scale)),
        Pair(Offset(680f * scale, 680f * scale), Offset(850f * scale, 850f * scale))
    )

    for ((start, end) in arrows) {
        drawLine(
            color = arrowColor,
            start = start,
            end = end,
            strokeWidth = 2.0f * scale
        )
        // Arrow circle head
        drawCircle(
            color = arrowColor,
            radius = 18f * scale,
            center = start,
            style = Stroke(width = 1.8f * scale)
        )
    }
}

private fun DrawScope.drawCarromPiece(piece: CarromPiece, scale: Float) {
    val center = Offset(piece.position.x * scale, piece.position.y * scale)
    val baseRadius = piece.radius * scale
    // If sinking into pocket, shrink smoothly
    val currentRadius = if (piece.pocketAnimProgress > 0f) {
        baseRadius * (1f - piece.pocketAnimProgress * 0.7f)
    } else baseRadius

    if (currentRadius <= 1f) return

    // Drop shadow
    if (piece.pocketAnimProgress == 0f) {
        drawCircle(
            color = Color(0x44000000),
            radius = currentRadius + 2f * scale,
            center = Offset(center.x + 2.5f * scale, center.y + 3.5f * scale)
        )
    }

    when (piece.type) {
        PieceType.WHITE -> {
            // Warm pearl ivory disc with 3D gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF7F3E8), Color(0xFFD6CDB8)),
                    center = Offset(center.x - currentRadius * 0.3f, center.y - currentRadius * 0.3f),
                    radius = currentRadius
                ),
                radius = currentRadius,
                center = center
            )
            // Inner decorative rings
            drawCircle(
                color = Color(0xFFB0A288),
                radius = currentRadius * 0.65f,
                center = center,
                style = Stroke(width = 1.5f * scale)
            )
            drawCircle(
                color = Color(0xFFC4B89F),
                radius = currentRadius * 0.3f,
                center = center,
                style = Stroke(width = 1.2f * scale)
            )
        }

        PieceType.BLACK -> {
            // Ebony dark wood disc
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF424242), Color(0xFF212121), Color(0xFF111111)),
                    center = Offset(center.x - currentRadius * 0.3f, center.y - currentRadius * 0.3f),
                    radius = currentRadius
                ),
                radius = currentRadius,
                center = center
            )
            // Inner glossy rings
            drawCircle(
                color = Color(0xFF616161),
                radius = currentRadius * 0.65f,
                center = center,
                style = Stroke(width = 1.5f * scale)
            )
            drawCircle(
                color = Color(0xFF757575),
                radius = currentRadius * 0.3f,
                center = center,
                style = Stroke(width = 1.2f * scale)
            )
        }

        PieceType.QUEEN -> {
            // Regal crimson Queen with gold center
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF5252), Color(0xFFD32F2F), Color(0xFF8B0000)),
                    center = Offset(center.x - currentRadius * 0.3f, center.y - currentRadius * 0.3f),
                    radius = currentRadius
                ),
                radius = currentRadius,
                center = center
            )
            // Gold Royal Crown Ring
            drawCircle(
                color = Color(0xFFFFD700),
                radius = currentRadius * 0.65f,
                center = center,
                style = Stroke(width = 2.0f * scale)
            )
            // Golden center dot
            drawCircle(
                color = Color(0xFFFFD700),
                radius = currentRadius * 0.28f,
                center = center
            )
        }

        PieceType.STRIKER -> {}
    }
}

private fun DrawScope.drawStriker(
    striker: CarromPiece,
    theme: StrikerTheme,
    isAiming: Boolean,
    power: Float,
    scale: Float
) {
    val center = Offset(striker.position.x * scale, striker.position.y * scale)
    val r = striker.radius * scale

    // Aiming Glow Ring
    if (isAiming) {
        val glowColor = theme.primaryColor.copy(alpha = (power / 100f * 0.6f).coerceIn(0.2f, 0.7f))
        drawCircle(
            color = glowColor,
            radius = r + 8f * scale + (power / 100f * 10f * scale),
            center = center
        )
    }

    // Shadow
    drawCircle(
        color = Color(0x55000000),
        radius = r + 3f * scale,
        center = Offset(center.x + 3f * scale, center.y + 4.5f * scale)
    )

    // Outer Striker Body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(theme.primaryColor, theme.secondaryColor),
            center = Offset(center.x - r * 0.35f, center.y - r * 0.35f),
            radius = r
        ),
        radius = r,
        center = center
    )

    // Outer Grip Ring
    drawCircle(
        color = theme.ringColor,
        radius = r * 0.85f,
        center = center,
        style = Stroke(width = 2.8f * scale)
    )

    // Inner Crest
    drawCircle(
        color = theme.primaryColor.copy(alpha = 0.8f),
        radius = r * 0.5f,
        center = center,
        style = Stroke(width = 2.0f * scale)
    )
    drawCircle(
        color = theme.ringColor,
        radius = r * 0.22f,
        center = center
    )
}

private fun DrawScope.drawAimGuide(
    striker: CarromPiece,
    aimVector: Vector2D,
    power: Float,
    pieces: List<CarromPiece>,
    strikerTheme: StrikerTheme,
    scale: Float,
    dashEffect: PathEffect
) {
    val sPos = striker.position
    val traj = CarromPhysics.calculateAimTrajectory(
        strikerPos = sPos,
        aimDirection = aimVector,
        powerPercent = power,
        pieces = pieces,
        guideMultiplier = strikerTheme.aimLengthMultiplier
    )

    val startOffset = Offset(sPos.x * scale, sPos.y * scale)
    val impact = traj.impactPoint ?: (sPos + aimVector * 300f)
    val impactOffset = Offset(impact.x * scale, impact.y * scale)

    val guideColor = strikerTheme.primaryColor.copy(alpha = 0.85f)

    // 1. Primary Aim Ray
    drawLine(
        color = guideColor,
        start = startOffset,
        end = impactOffset,
        strokeWidth = 3.5f * scale,
        pathEffect = dashEffect,
        cap = StrokeCap.Round
    )

    // 2. Ghost Striker at Impact
    drawCircle(
        color = guideColor.copy(alpha = 0.35f),
        radius = striker.radius * scale,
        center = impactOffset,
        style = Stroke(width = 2f * scale, pathEffect = dashEffect)
    )

    // 3. Deflection Trajectory Ray for Target Coin (if hitting a coin)
    if (traj.targetDeflection != null) {
        val hitPiece = pieces.find { it.id == traj.hitPieceId }
        if (hitPiece != null) {
            val pCenter = Offset(hitPiece.position.x * scale, hitPiece.position.y * scale)
            val pDeflectEnd = Offset(
                (hitPiece.position.x + traj.targetDeflection.x) * scale,
                (hitPiece.position.y + traj.targetDeflection.y) * scale
            )
            drawLine(
                color = Color(0xFFFFD700),
                start = pCenter,
                end = pDeflectEnd,
                strokeWidth = 3.2f * scale,
                cap = StrokeCap.Round
            )
            // Arrow indicator at end
            drawCircle(
                color = Color(0xFFFFD700),
                radius = 5f * scale,
                center = pDeflectEnd
            )
        }
    }

    // 4. Cushion Bounce Ray (if rebounding off wall)
    if (traj.bounceDirection != null) {
        val bounceEnd = Offset(
            (impact.x + traj.bounceDirection.x * 120f) * scale,
            (impact.y + traj.bounceDirection.y * 120f) * scale
        )
        drawLine(
            color = Color(0xFF00E5FF).copy(alpha = 0.8f),
            start = impactOffset,
            end = bounceEnd,
            strokeWidth = 2.8f * scale,
            pathEffect = dashEffect,
            cap = StrokeCap.Round
        )
    }
}
