package com.alex34906991.nutritrack_a3.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NutriCoachTipDao {
    @Insert
    suspend fun insertTip(tip: NutriCoachTipEntity): Long
    
    @Query("SELECT * FROM nutricoach_tips WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTipsByUser(userId: String): Flow<List<NutriCoachTipEntity>>
    
    @Query("SELECT * FROM nutricoach_tips WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestTipForUser(userId: String): NutriCoachTipEntity?
} 