package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CarromRepository private constructor(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        CarromDatabase::class.java,
        "carrom_royale.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.carromDao()

    val userStats: Flow<UserStatsEntity?> = dao.getUserStats()
    val recentMatches: Flow<List<MatchHistoryEntity>> = dao.getRecentMatches()

    suspend fun getOrCreateStats(): UserStatsEntity {
        var stats = dao.getUserStatsDirect()
        if (stats == null) {
            stats = UserStatsEntity()
            dao.insertOrUpdateUserStats(stats)
        }
        return stats
    }

    suspend fun updateStats(stats: UserStatsEntity) {
        dao.insertOrUpdateUserStats(stats)
    }

    suspend fun recordMatch(
        mode: String,
        p1Name: String,
        p2Name: String,
        p1Score: Int,
        p2Score: Int,
        winnerName: String,
        totalTurns: Int,
        coinsEarned: Int,
        isWin: Boolean,
        queenPocketed: Boolean,
        piecesPocketed: Int
    ) {
        val match = MatchHistoryEntity(
            gameMode = mode,
            player1Name = p1Name,
            player2Name = p2Name,
            player1Score = p1Score,
            player2Score = p2Score,
            winnerName = winnerName,
            totalTurns = totalTurns,
            coinsEarned = coinsEarned
        )
        dao.insertMatchHistory(match)

        val current = getOrCreateStats()
        val newWinStreak = if (isWin) current.winStreak + 1 else 0
        val bestStreak = maxOf(current.bestWinStreak, newWinStreak)
        val newHighestScore = maxOf(current.highestScore, p1Score)

        val updated = current.copy(
            coins = current.coins + coinsEarned,
            matchesPlayed = current.matchesPlayed + 1,
            matchesWon = current.matchesWon + (if (isWin) 1 else 0),
            queenPocketedCount = current.queenPocketedCount + (if (queenPocketed) 1 else 0),
            totalPiecesPocketed = current.totalPiecesPocketed + piecesPocketed,
            winStreak = newWinStreak,
            bestWinStreak = bestStreak,
            highestScore = newHighestScore,
            xp = current.xp + (if (isWin) 100 else 40),
            playerLevel = 1 + (current.xp + (if (isWin) 100 else 40)) / 500
        )
        dao.insertOrUpdateUserStats(updated)
    }

    suspend fun addCoins(amount: Int) {
        dao.addCoins(amount)
    }

    suspend fun selectBoard(boardId: String) {
        dao.setSelectedBoard(boardId)
    }

    suspend fun selectStriker(strikerId: String) {
        dao.setSelectedStriker(strikerId)
    }

    suspend fun unlockBoard(boardId: String, cost: Int): Boolean {
        val current = getOrCreateStats()
        if (current.coins >= cost) {
            val list = current.unlockedBoardsCsv.split(",").toMutableSet()
            list.add(boardId)
            dao.insertOrUpdateUserStats(
                current.copy(
                    coins = current.coins - cost,
                    unlockedBoardsCsv = list.joinToString(","),
                    selectedBoardId = boardId
                )
            )
            return true
        }
        return false
    }

    suspend fun unlockStriker(strikerId: String, cost: Int): Boolean {
        val current = getOrCreateStats()
        if (current.coins >= cost) {
            val list = current.unlockedStrikersCsv.split(",").toMutableSet()
            list.add(strikerId)
            dao.insertOrUpdateUserStats(
                current.copy(
                    coins = current.coins - cost,
                    unlockedStrikersCsv = list.joinToString(","),
                    selectedStrikerId = strikerId
                )
            )
            return true
        }
        return false
    }

    suspend fun saveTrickShotStars(levelId: Int, stars: Int, rewardCoins: Int) {
        val current = getOrCreateStats()
        val starsMap = current.trickShotStarsCsv.split(",")
            .filter { it.contains(":") }
            .associate {
                val parts = it.split(":")
                parts[0].toInt() to parts[1].toInt()
            }.toMutableMap()

        val prevStars = starsMap[levelId] ?: 0
        if (stars > prevStars) {
            starsMap[levelId] = stars
            val newCsv = starsMap.entries.joinToString(",") { "${it.key}:${it.value}" }
            val bonus = (stars - prevStars) * rewardCoins
            dao.insertOrUpdateUserStats(
                current.copy(
                    trickShotStarsCsv = newCsv,
                    coins = current.coins + bonus
                )
            )
        }
    }

    suspend fun toggleAudio(sound: Boolean, haptics: Boolean) {
        val current = getOrCreateStats()
        dao.insertOrUpdateUserStats(current.copy(soundEnabled = sound, hapticsEnabled = haptics))
    }

    companion object {
        @Volatile
        private var INSTANCE: CarromRepository? = null

        fun getInstance(context: Context): CarromRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CarromRepository(context).also { INSTANCE = it }
            }
        }
    }
}
