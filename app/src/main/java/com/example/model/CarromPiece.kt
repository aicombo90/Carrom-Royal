package com.example.model

import kotlin.math.sqrt

enum class PieceType {
    STRIKER,
    WHITE,
    BLACK,
    QUEEN
}

data class Vector2D(val x: Float = 0f, val y: Float = 0f) {
    operator fun plus(v: Vector2D) = Vector2D(x + v.x, y + v.y)
    operator fun minus(v: Vector2D) = Vector2D(x - v.x, y - v.y)
    operator fun times(scalar: Float) = Vector2D(x * scalar, y * scalar)
    operator fun div(scalar: Float) = if (scalar != 0f) Vector2D(x / scalar, y / scalar) else Vector2D(0f, 0f)

    fun length(): Float = sqrt(x * x + y * y)
    fun lengthSquared(): Float = x * x + y * y

    fun normalized(): Vector2D {
        val len = length()
        return if (len > 0.0001f) Vector2D(x / len, y / len) else Vector2D(0f, 0f)
    }

    fun dot(v: Vector2D): Float = x * v.x + y * v.y

    fun distanceTo(other: Vector2D): Float = (this - other).length()
}

data class CarromPiece(
    val id: Int,
    val type: PieceType,
    val position: Vector2D,
    val velocity: Vector2D = Vector2D(0f, 0f),
    val radius: Float,
    val mass: Float = 1.0f,
    val isPocketed: Boolean = false,
    val pocketAnimProgress: Float = 0f, // 0f = full size, 1f = fully sunk into pocket
    val pocketTarget: Vector2D? = null,
    val rotationAngle: Float = 0f
) {
    val isMoving: Boolean
        get() = velocity.lengthSquared() > 0.15f
}

data class Particle(
    val position: Vector2D,
    val velocity: Vector2D,
    val color: Long,
    val radius: Float,
    val alpha: Float = 1.0f,
    val life: Float = 1.0f // 1.0 -> 0.0
)

data class AimTrajectory(
    val startPos: Vector2D,
    val direction: Vector2D,
    val power: Float,
    val impactPoint: Vector2D? = null,
    val hitPieceId: Int? = null,
    val bounceDirection: Vector2D? = null,
    val targetDeflection: Vector2D? = null
)
