package com.alex34906991.nutritrack_a3.data.repository

import android.content.Context
import com.alex34906991.nutritrack_a3.data.database.FoodIntakeEntity
import com.alex34906991.nutritrack_a3.data.database.NutriTrackDatabase
import kotlinx.coroutines.flow.Flow

class FoodIntakeRepository(context: Context) {
    private val database = NutriTrackDatabase.getDatabase(context)
    private val foodIntakeDao = database.foodIntakeDao()
    
    fun getFoodIntakesByPatient(patientId: String): Flow<List<FoodIntakeEntity>> {
        return foodIntakeDao.getFoodIntakesByPatient(patientId)
    }
    
    suspend fun insertFoodIntake(foodIntake: FoodIntakeEntity): Long {
        return foodIntakeDao.insertFoodIntake(foodIntake)
    }
    
    suspend fun insertFoodIntakes(foodIntakes: List<FoodIntakeEntity>) {
        foodIntakeDao.insertFoodIntakes(foodIntakes)
    }
} 