package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CarromDao {
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStatsDirect(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStatsEntity)

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 30")
    fun getRecentMatches(): Flow<List<MatchHistoryEntity>>

    @Insert
    suspend fun insertMatchHistory(match: MatchHistoryEntity)

    @Query("UPDATE user_stats SET coins = coins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE user_stats SET selectedBoardId = :boardId WHERE id = 1")
    suspend fun setSelectedBoard(boardId: String)

    @Query("UPDATE user_stats SET selectedStrikerId = :strikerId WHERE id = 1")
    suspend fun setSelectedStriker(strikerId: String)
}

@Database(
    entities = [UserStatsEntity::class, MatchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CarromDatabase : RoomDatabase() {
    abstract fun carromDao(): CarromDao
}
