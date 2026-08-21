package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 1000,
    val gems: Int = 50,
    val playerLevel: Int = 1,
    val xp: Int = 0,
    val matchesPlayed: Int = 0,
    val matchesWon: Int = 0,
    val queenPocketedCount: Int = 0,
    val totalPiecesPocketed: Int = 0,
    val selectedBoardId: String = "classic_mahogany",
    val selectedStrikerId: String = "classic_ivory",
    val unlockedBoardsCsv: String = "classic_mahogany",
    val unlockedStrikersCsv: String = "classic_ivory",
    val trickShotStarsCsv: String = "", // e.g. "1:3,2:2,3:1"
    val highestScore: Int = 0,
    val winStreak: Int = 0,
    val bestWinStreak: Int = 0,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val guideLineLengthBonus: Float = 1.0f
)

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val matchId: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val gameMode: String,
    val player1Name: String,
    val player2Name: String,
    val player1Score: Int,
    val player2Score: Int,
    val winnerName: String,
    val totalTurns: Int,
    val coinsEarned: Int
)
