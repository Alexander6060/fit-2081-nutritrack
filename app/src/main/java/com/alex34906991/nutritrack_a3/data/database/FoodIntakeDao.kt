package com.alex34906991.nutritrack_a3.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodIntakeDao {
    @Query("SELECT * FROM food_intakes WHERE patientId = :patientId")
    fun getFoodIntakesByPatient(patientId: String): Flow<List<FoodIntakeEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntakeEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntakes(foodIntakes: List<FoodIntakeEntity>)
} 