package com.alex34906991.nutritrack_a3.data.repository

import android.content.Context
import com.alex34906991.nutritrack_a3.R
import com.alex34906991.nutritrack_a3.data.UserData
import com.alex34906991.nutritrack_a3.data.database.NutriCoachTipDao
import com.alex34906991.nutritrack_a3.data.database.NutriCoachTipEntity
import com.alex34906991.nutritrack_a3.data.database.NutriTrackDatabase
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import kotlinx.coroutines.flow.Flow

class NutriCoachRepository(private val context: Context) {
    private val nutriCoachTipDao: NutriCoachTipDao = NutriTrackDatabase.getDatabase(context).nutriCoachTipDao()
    
    // Get API key from resources
    private val apiKey = context.getString(R.string.gemini_api_key)
    
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            )
        )
    }
    
    suspend fun generateMotivationalTip(user: UserData): String {
        val prompt = buildPrompt(user)
        val response = generativeModel.generateContent(prompt)
        return response.text?.trim() ?: "Remember to eat fruits daily for better health!"
    }
    
    private fun buildPrompt(user: UserData): String {
        val basePrompt = "Generate a short encouraging message to help someone improve their fruit intake."
        
        // Get the user's fruit score based on gender
        val fruitScore = if (user.sex.equals("Male", ignoreCase = true)) {
            user.fruitScoreMale
        } else {
            user.fruitScoreFemale
        }
        
        // Enhanced prompt with user context
        return """
            $basePrompt
            
            User context:
            - Name: ${user.name ?: "User"}
            - Gender: ${user.sex}
            - Current fruit intake score: $fruitScore
            - Fruit serving size: ${user.fruitServeSize ?: "unknown"}
            
            Make the message personal, specific to their fruit intake level, and keep it under 2 sentences.
            Include an emoji or two related to different fruits.
        """.trimIndent()
    }
    
    suspend fun saveTip(userId: String, message: String): Long {
        val tipEntity = NutriCoachTipEntity(
            userId = userId,
            message = message
        )
        return nutriCoachTipDao.insertTip(tipEntity)
    }
    
    fun getTipsForUser(userId: String): Flow<List<NutriCoachTipEntity>> {
        return nutriCoachTipDao.getTipsByUser(userId)
    }
    
    suspend fun getLatestTipForUser(userId: String): NutriCoachTipEntity? {
        return nutriCoachTipDao.getLatestTipForUser(userId)
    }
    
    fun isFruitScoreOptimal(user: UserData): Boolean {
        // Get the user's fruit score based on gender
        val fruitScore = if (user.sex.equals("Male", ignoreCase = true)) {
            user.fruitScoreMale
        } else {
            user.fruitScoreFemale
        }
        
        // Check if score is optimal based on HEIFA guidelines
        // HEIFA scores for fruit are typically on a scale where maximum is considered optimal
        val maxPossibleScore = 10.0 // Typical maximum HEIFA score for a food group
        
        return fruitScore != null && fruitScore >= maxPossibleScore * 0.8
    }
} 