package com.example.model

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

object CarromPhysics {
    const val BOARD_SIZE = 1000f
    const val CUSHION_MARGIN = 75f
    const val PLAY_AREA_MIN = CUSHION_MARGIN
    const val PLAY_AREA_MAX = BOARD_SIZE - CUSHION_MARGIN

    const val PIECE_RADIUS = 23f
    const val STRIKER_RADIUS = 32f
    const val PIECE_MASS = 1.0f
    const val STRIKER_MASS = 3.2f

    const val POCKET_RADIUS = 40f
    const val POCKET_CAPTURE_RADIUS = 62f
    const val BASELINE_BOTTOM_Y = 790f
    const val BASELINE_TOP_Y = 210f
    const val BASELINE_MIN_X = 230f
    const val BASELINE_MAX_X = 770f

    val POCKETS = listOf(
        Vector2D(100f, 100f), // Top-Left
        Vector2D(900f, 100f), // Top-Right
        Vector2D(100f, 900f), // Bottom-Left
        Vector2D(900f, 900f)  // Bottom-Right
    )

    fun createStandardPieces(): List<CarromPiece> {
        val pieces = mutableListOf<CarromPiece>()
        var idCounter = 1
        val center = Vector2D(500f, 500f)

        // 1. Queen at center
        pieces.add(
            CarromPiece(
                id = idCounter++,
                type = PieceType.QUEEN,
                position = center,
                radius = PIECE_RADIUS,
                mass = PIECE_MASS
            )
        )

        val r = PIECE_RADIUS * 2.02f
        // 2. Inner circle of 6 pieces (Alternating White and Black)
        for (i in 0 until 6) {
            val angle = i * (PI.toFloat() / 3f)
            val pos = center + Vector2D(cos(angle) * r, sin(angle) * r)
            val type = if (i % 2 == 0) PieceType.WHITE else PieceType.BLACK
            pieces.add(
                CarromPiece(
                    id = idCounter++,
                    type = type,
                    position = pos,
                    radius = PIECE_RADIUS,
                    mass = PIECE_MASS
                )
            )
        }

        // 3. Outer circle of 12 pieces
        val rOuter = r * 1.732f
        for (i in 0 until 12) {
            val angle = i * (PI.toFloat() / 6f) + (PI.toFloat() / 12f)
            val pos = center + Vector2D(cos(angle) * rOuter, sin(angle) * rOuter)
            val type = if (i % 2 == 0) PieceType.WHITE else PieceType.BLACK
            pieces.add(
                CarromPiece(
                    id = idCounter++,
                    type = type,
                    position = pos,
                    radius = PIECE_RADIUS,
                    mass = PIECE_MASS
                )
            )
        }

        return pieces
    }

    fun stepPhysics(
        pieces: List<CarromPiece>,
        dt: Float = 0.016f,
        subSteps: Int = 8,
        frictionBase: Float = 1.15f,
        cushionRestitution: Float = 0.88f,
        pieceRestitution: Float = 0.94f,
        onPieceHit: ((Float) -> Unit)? = null,
        onCushionHit: ((Float) -> Unit)? = null,
        onPocketSink: ((CarromPiece) -> Unit)? = null
    ): List<CarromPiece> {
        val working = pieces.map { it.copy() }.toMutableList()
        val subDt = dt / subSteps

        for (step in 0 until subSteps) {
            // 1. Move & Pocket Suction
            for (i in working.indices) {
                val p = working[i]
                if (p.isPocketed) continue

                // Check pocket pull
                var beingPulled = false
                for (pocket in POCKETS) {
                    val dist = p.position.distanceTo(pocket)
                    if (dist < POCKET_CAPTURE_RADIUS) {
                        beingPulled = true
                        val dir = (pocket - p.position).normalized()
                        val pullForce = (POCKET_CAPTURE_RADIUS - dist) * 180f
                        val newVel = (p.velocity + dir * (pullForce * subDt)) * 0.92f
                        val newPos = p.position + newVel * subDt
                        val animProg = min(1f, p.pocketAnimProgress + subDt * 4.5f)

                        if (dist < POCKET_RADIUS * 0.75f || animProg >= 0.95f) {
                            working[i] = p.copy(
                                position = pocket,
                                velocity = Vector2D(0f, 0f),
                                isPocketed = true,
                                pocketAnimProgress = 1f,
                                pocketTarget = pocket
                            )
                            onPocketSink?.invoke(p)
                        } else {
                            working[i] = p.copy(
                                position = newPos,
                                velocity = newVel,
                                pocketAnimProgress = animProg,
                                pocketTarget = pocket
                            )
                        }
                        break
                    }
                }

                if (beingPulled) continue

                // Normal movement
                val newPos = p.position + p.velocity * subDt
                val friction = frictionBase * (1.0f + p.velocity.length() * 0.0003f)
                var newVel = p.velocity * (1.0f - (friction * subDt)).coerceIn(0f, 1f)

                if (newVel.lengthSquared() < 0.12f) {
                    newVel = Vector2D(0f, 0f)
                }

                working[i] = p.copy(position = newPos, velocity = newVel)
            }

            // 2. Cushion Boundary Collisions
            for (i in working.indices) {
                val p = working[i]
                if (p.isPocketed) continue

                var posX = p.position.x
                var posY = p.position.y
                var velX = p.velocity.x
                var velY = p.velocity.y
                var bounced = false
                var impactSpeed = 0f

                val minX = PLAY_AREA_MIN + p.radius
                val maxX = PLAY_AREA_MAX - p.radius
                val minY = PLAY_AREA_MIN + p.radius
                val maxY = PLAY_AREA_MAX - p.radius

                // Check Left
                if (posX < minX) {
                    posX = minX
                    impactSpeed = max(impactSpeed, abs(velX))
                    velX = -velX * cushionRestitution
                    bounced = true
                }
                // Check Right
                if (posX > maxX) {
                    posX = maxX
                    impactSpeed = max(impactSpeed, abs(velX))
                    velX = -velX * cushionRestitution
                    bounced = true
                }
                // Check Top
                if (posY < minY) {
                    posY = minY
                    impactSpeed = max(impactSpeed, abs(velY))
                    velY = -velY * cushionRestitution
                    bounced = true
                }
                // Check Bottom
                if (posY > maxY) {
                    posY = maxY
                    impactSpeed = max(impactSpeed, abs(velY))
                    velY = -velY * cushionRestitution
                    bounced = true
                }

                if (bounced) {
                    working[i] = p.copy(
                        position = Vector2D(posX, posY),
                        velocity = Vector2D(velX, velY)
                    )
                    if (impactSpeed > 40f) {
                        onCushionHit?.invoke((impactSpeed / 300f).coerceIn(0.2f, 1.0f))
                    }
                }
            }

            // 3. Piece-to-Piece Collisions
            for (i in 0 until working.size) {
                val p1 = working[i]
                if (p1.isPocketed) continue

                for (j in i + 1 until working.size) {
                    val p2 = working[j]
                    if (p2.isPocketed) continue

                    val delta = p2.position - p1.position
                    val dist = delta.length()
                    val minDist = p1.radius + p2.radius

                    if (dist < minDist && dist > 0.0001f) {
                        val normal = delta / dist
                        val overlap = minDist - dist

                        // Position separation based on mass
                        val totalMass = p1.mass + p2.mass
                        val p1Shift = normal * (overlap * (p2.mass / totalMass))
                        val p2Shift = normal * (overlap * (p1.mass / totalMass))

                        val newP1Pos = p1.position - p1Shift
                        val newP2Pos = p2.position + p2Shift

                        // Relative velocity along collision normal
                        val relVel = p1.velocity - p2.velocity
                        val velAlongNormal = relVel.dot(normal)

                        if (velAlongNormal > 0) {
                            val impulseScalar = (-(1 + pieceRestitution) * velAlongNormal) /
                                    ((1f / p1.mass) + (1f / p2.mass))

                            val impulse = normal * impulseScalar
                            val newP1Vel = p1.velocity + impulse / p1.mass
                            val newP2Vel = p2.velocity - impulse / p2.mass

                            working[i] = p1.copy(position = newP1Pos, velocity = newP1Vel)
                            working[j] = p2.copy(position = newP2Pos, velocity = newP2Vel)

                            val collisionPower = velAlongNormal
                            if (collisionPower > 30f) {
                                onPieceHit?.invoke((collisionPower / 350f).coerceIn(0.2f, 1.0f))
                            }
                        } else {
                            working[i] = p1.copy(position = newP1Pos)
                            working[j] = p2.copy(position = newP2Pos)
                        }
                    }
                }
            }
        }

        return working
    }

    fun calculateAimTrajectory(
        strikerPos: Vector2D,
        aimDirection: Vector2D,
        powerPercent: Float,
        pieces: List<CarromPiece>,
        guideMultiplier: Float = 1.0f
    ): AimTrajectory {
        val maxDist = 450f * guideMultiplier * (0.5f + (powerPercent / 100f) * 0.5f)
        val normDir = aimDirection.normalized()
        var closestDist = maxDist
        var hitPiece: CarromPiece? = null
        var impactPoint: Vector2D? = null
        var targetDeflection: Vector2D? = null
        var strikerDeflection: Vector2D? = null

        // Test ray against other pieces
        for (p in pieces) {
            if (p.isPocketed || p.type == PieceType.STRIKER) continue
            val d = p.position - strikerPos
            val proj = d.dot(normDir)
            if (proj > 0 && proj < closestDist + STRIKER_RADIUS + PIECE_RADIUS) {
                val perpDistSq = d.lengthSquared() - proj * proj
                val radiusSum = STRIKER_RADIUS + p.radius
                if (perpDistSq < radiusSum * radiusSum) {
                    val hitDist = proj - sqrt(max(0f, radiusSum * radiusSum - perpDistSq))
                    if (hitDist > 0 && hitDist < closestDist) {
                        closestDist = hitDist
                        hitPiece = p
                        val hitPos = strikerPos + normDir * hitDist
                        impactPoint = hitPos
                        val normal = (p.position - hitPos).normalized()
                        targetDeflection = normal * 120f
                        val tangent = Vector2D(-normal.y, normal.x)
                        val strikerTangentComponent = normDir.dot(tangent)
                        strikerDeflection = tangent * (strikerTangentComponent * 70f)
                    }
                }
            }
        }

        // If no piece hit, test cushion boundary bounce
        if (hitPiece == null) {
            val endPoint = strikerPos + normDir * maxDist
            var bounceDir: Vector2D? = null

            val minX = PLAY_AREA_MIN + STRIKER_RADIUS
            val maxX = PLAY_AREA_MAX - STRIKER_RADIUS
            val minY = PLAY_AREA_MIN + STRIKER_RADIUS
            val maxY = PLAY_AREA_MAX - STRIKER_RADIUS

            var tMin = 1.0f
            if (normDir.x < 0 && endPoint.x < minX) {
                val t = (minX - strikerPos.x) / (normDir.x * maxDist)
                if (t in 0f..tMin) { tMin = t; bounceDir = Vector2D(-normDir.x, normDir.y) }
            } else if (normDir.x > 0 && endPoint.x > maxX) {
                val t = (maxX - strikerPos.x) / (normDir.x * maxDist)
                if (t in 0f..tMin) { tMin = t; bounceDir = Vector2D(-normDir.x, normDir.y) }
            }
            if (normDir.y < 0 && endPoint.y < minY) {
                val t = (minY - strikerPos.y) / (normDir.y * maxDist)
                if (t in 0f..tMin) { tMin = t; bounceDir = Vector2D(normDir.x, -normDir.y) }
            } else if (normDir.y > 0 && endPoint.y > maxY) {
                val t = (maxY - strikerPos.y) / (normDir.y * maxDist)
                if (t in 0f..tMin) { tMin = t; bounceDir = Vector2D(normDir.x, -normDir.y) }
            }

            if (tMin < 1.0f) {
                val hitPos = strikerPos + normDir * (maxDist * tMin)
                return AimTrajectory(
                    startPos = strikerPos,
                    direction = normDir,
                    power = powerPercent,
                    impactPoint = hitPos,
                    hitPieceId = null,
                    bounceDirection = bounceDir?.normalized()
                )
            }
        }

        return AimTrajectory(
            startPos = strikerPos,
            direction = normDir,
            power = powerPercent,
            impactPoint = impactPoint ?: (strikerPos + normDir * closestDist),
            hitPieceId = hitPiece?.id,
            bounceDirection = strikerDeflection,
            targetDeflection = targetDeflection
        )
    }

    fun computeAiShot(
        pieces: List<CarromPiece>,
        targetTypes: Set<PieceType>,
        difficulty: AiDifficulty,
        isTopPlayer: Boolean = true
    ): Pair<Float, Vector2D> {
        val baselineY = if (isTopPlayer) BASELINE_TOP_Y else BASELINE_BOTTOM_Y
        val candidates = pieces.filter { !it.isPocketed && it.type in targetTypes }
        if (candidates.isEmpty()) {
            return Pair(BASELINE_MIN_X + (BASELINE_MAX_X - BASELINE_MIN_X) * 0.5f, Vector2D(0f, 1f) * 60f)
        }

        var bestScore = -9999f
        var bestSliderX = 500f
        var bestAimVector = Vector2D(0f, if (isTopPlayer) 1f else -1f)

        // Evaluate each candidate target piece towards each of the 4 pockets
        for (target in candidates) {
            for (pocket in POCKETS) {
                // If top player, prefer bottom pockets or reachable top pockets
                val dirToPocket = (pocket - target.position).normalized()
                // Ideal impact position behind the piece
                val idealImpactPos = target.position - dirToPocket * (PIECE_RADIUS + STRIKER_RADIUS)

                // Striker baseline placement: clamp to valid baseline segment
                val idealStrikerX = idealImpactPos.x.coerceIn(BASELINE_MIN_X + 20f, BASELINE_MAX_X - 20f)
                val testStrikerPos = Vector2D(idealStrikerX, baselineY)

                val shotDir = (idealImpactPos - testStrikerPos)
                val dist = shotDir.length()
                val normShotDir = shotDir.normalized()

                // If shooting backwards behind baseline, skip
                if (isTopPlayer && normShotDir.y < 0.1f) continue
                if (!isTopPlayer && normShotDir.y > -0.1f) continue

                // Check angle alignment between shot and pocket direction
                val alignment = normShotDir.dot(dirToPocket)
                if (alignment < 0.2f) continue

                var score = alignment * 100f - (dist * 0.05f)
                if (target.type == PieceType.QUEEN) score += 40f

                if (score > bestScore) {
                    bestScore = score
                    bestSliderX = idealStrikerX
                    bestAimVector = normShotDir
                }
            }
        }

        // Apply difficulty jitter
        val jitterAngle = (Random.nextFloat() * 2f - 1f) * difficulty.errorJitter
        val cosJ = cos(jitterAngle)
        val sinJ = sin(jitterAngle)
        val jitteredAim = Vector2D(
            bestAimVector.x * cosJ - bestAimVector.y * sinJ,
            bestAimVector.x * sinJ + bestAimVector.y * cosJ
        ).normalized()

        val power = (70f + Random.nextFloat() * 25f) * difficulty.maxPowerBonus
        return Pair(bestSliderX, jitteredAim * power)
    }
}
