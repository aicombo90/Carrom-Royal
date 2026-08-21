package com.example.model

enum class GameMode(val displayName: String, val description: String) {
    CLASSIC_POINTS(
        displayName = "Points Match",
        description = "Target 25 points or clear board. White = 20, Black = 10, Queen = 50 with cover rule."
    ),
    DISC_POOL(
        displayName = "Carrom Pool",
        description = "Pot all your assigned colored pieces (White or Black) and Queen to win!"
    ),
    VS_AI(
        displayName = "Vs AI Bot",
        description = "Test your skills against dynamic AI with Easy, Medium, or Hard difficulty."
    ),
    PASS_AND_PLAY(
        displayName = "Pass & Play (2P)",
        description = "Take turns with a friend on the same device with realistic tournament physics."
    ),
    TRICK_SHOTS(
        displayName = "Trick Shots",
        description = "Solve challenging trick shot puzzles and earn up to 3 stars per level."
    ),
    PRACTICE(
        displayName = "Free Practice",
        description = "Sharpen your cuts, rebounds, and power shots with infinite striker shots."
    )
}

enum class AiDifficulty(val label: String, val errorJitter: Float, val maxPowerBonus: Float) {
    EASY("Easy", 0.16f, 0.85f),
    MEDIUM("Medium", 0.07f, 1.0f),
    HARD("Hard", 0.02f, 1.15f)
}

data class TrickShotLevel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val instructions: String,
    val maxShots: Int,
    val requiredPockets: Int,
    val coinReward: Int,
    val targetPieceTypes: List<PieceType>, // What must be pocketed
    val initialPieces: List<CarromPiece>,
    val allowedStrikerXRange: ClosedFloatingPointRange<Float>? = null
) {
    companion object {
        fun getLevels(): List<TrickShotLevel> {
            val boardSize = 1000f
            val pieceRadius = 24f
            val strikerRadius = 34f
            val center = Vector2D(500f, 500f)

            return listOf(
                TrickShotLevel(
                    id = 1,
                    title = "Corner Precision",
                    subtitle = "The Straight Cut",
                    instructions = "Pocket the White carrom disc directly into the top-right corner pocket in 1 shot!",
                    maxShots = 1,
                    requiredPockets = 1,
                    coinReward = 150,
                    targetPieceTypes = listOf(PieceType.WHITE),
                    initialPieces = listOf(
                        CarromPiece(1, PieceType.WHITE, Vector2D(650f, 350f), radius = pieceRadius)
                    )
                ),
                TrickShotLevel(
                    id = 2,
                    title = "Bank Shot Classic",
                    subtitle = "Rebound off top cushion",
                    instructions = "Rebound the White coin off the top cushion to pocket it into the bottom-right pocket!",
                    maxShots = 1,
                    requiredPockets = 1,
                    coinReward = 200,
                    targetPieceTypes = listOf(PieceType.WHITE),
                    initialPieces = listOf(
                        CarromPiece(1, PieceType.WHITE, Vector2D(500f, 250f), radius = pieceRadius)
                    )
                ),
                TrickShotLevel(
                    id = 3,
                    title = "Double Cannon",
                    subtitle = "Two birds with one stone",
                    instructions = "Strike the first piece into the second piece to pocket both in a single strike!",
                    maxShots = 1,
                    requiredPockets = 2,
                    coinReward = 300,
                    targetPieceTypes = listOf(PieceType.WHITE, PieceType.WHITE),
                    initialPieces = listOf(
                        CarromPiece(1, PieceType.WHITE, Vector2D(480f, 600f), radius = pieceRadius),
                        CarromPiece(2, PieceType.WHITE, Vector2D(350f, 350f), radius = pieceRadius)
                    )
                ),
                TrickShotLevel(
                    id = 4,
                    title = "The Royal Cover",
                    subtitle = "Queen & Cover Combo",
                    instructions = "Pocket the red Queen and then the White cover piece in at most 2 shots!",
                    maxShots = 2,
                    requiredPockets = 2,
                    coinReward = 400,
                    targetPieceTypes = listOf(PieceType.QUEEN, PieceType.WHITE),
                    initialPieces = listOf(
                        CarromPiece(1, PieceType.QUEEN, center, radius = pieceRadius),
                        CarromPiece(2, PieceType.WHITE, Vector2D(450f, 400f), radius = pieceRadius)
                    )
                ),
                TrickShotLevel(
                    id = 5,
                    title = "Split Shot",
                    subtitle = "Send discs to opposite pockets",
                    instructions = "Hit between the two white discs to split and pocket both into top-left and top-right pockets!",
                    maxShots = 1,
                    requiredPockets = 2,
                    coinReward = 500,
                    targetPieceTypes = listOf(PieceType.WHITE, PieceType.WHITE),
                    initialPieces = listOf(
                        CarromPiece(1, PieceType.WHITE, Vector2D(475f, 400f), radius = pieceRadius),
                        CarromPiece(2, PieceType.WHITE, Vector2D(525f, 400f), radius = pieceRadius)
                    )
                ),
                TrickShotLevel(
                    id = 6,
                    title = "Needle in the Haystack",
                    subtitle = "Squeeze through blockers",
                    instructions = "Thread the striker between two black blocker discs to pocket the Queen at the back!",
                    maxShots = 1,
                    requiredPockets = 1,
                    coinReward = 600,
                    targetPieceTypes = listOf(PieceType.QUEEN),
                    initialPieces = listOf(
                        CarromPiece(1, PieceType.BLACK, Vector2D(420f, 500f), radius = pieceRadius),
                        CarromPiece(2, PieceType.BLACK, Vector2D(580f, 500f), radius = pieceRadius),
                        CarromPiece(3, PieceType.QUEEN, Vector2D(500f, 300f), radius = pieceRadius)
                    )
                ),
                TrickShotLevel(
                    id = 7,
                    title = "Double Cushion Ricochet",
                    subtitle = "Angle of Reflection",
                    instructions = "Bank off the right side wall and top wall to sink the White coin in the top-left pocket!",
                    maxShots = 1,
                    requiredPockets = 1,
                    coinReward = 700,
                    targetPieceTypes = listOf(PieceType.WHITE),
                    initialPieces = listOf(
                        CarromPiece(1, PieceType.WHITE, Vector2D(300f, 250f), radius = pieceRadius)
                    )
                ),
                TrickShotLevel(
                    id = 8,
                    title = "Grandmaster Clearout",
                    subtitle = "Clear 4 discs in 3 shots",
                    instructions = "Clear all 4 white discs on the board in 3 shots or fewer!",
                    maxShots = 3,
                    requiredPockets = 4,
                    coinReward = 1000,
                    targetPieceTypes = listOf(PieceType.WHITE, PieceType.WHITE, PieceType.WHITE, PieceType.WHITE),
                    initialPieces = listOf(
                        CarromPiece(1, PieceType.WHITE, Vector2D(350f, 350f), radius = pieceRadius),
                        CarromPiece(2, PieceType.WHITE, Vector2D(650f, 350f), radius = pieceRadius),
                        CarromPiece(3, PieceType.WHITE, Vector2D(350f, 650f), radius = pieceRadius),
                        CarromPiece(4, PieceType.WHITE, Vector2D(650f, 650f), radius = pieceRadius)
                    )
                )
            )
        }
    }
}
