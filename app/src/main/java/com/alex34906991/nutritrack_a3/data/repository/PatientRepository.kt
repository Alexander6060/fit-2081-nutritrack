package com.alex34906991.nutritrack_a3.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.alex34906991.nutritrack_a3.data.CSVDataParser
import com.alex34906991.nutritrack_a3.data.UserData
import com.alex34906991.nutritrack_a3.data.database.DataMapper
import com.alex34906991.nutritrack_a3.data.database.NutriTrackDatabase
import com.alex34906991.nutritrack_a3.data.database.PatientEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PatientRepository(private val context: Context) {
    private val database = NutriTrackDatabase.getDatabase(context)
    private val patientDao = database.patientDao()
    private val foodIntakeDao = database.foodIntakeDao()
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    
    // Check if data has been loaded before
    private val KEY_DATA_LOADED = "data_loaded_from_csv"
    
    suspend fun loadInitialDataIfNeeded() {
        val dataLoaded = prefs.getBoolean(KEY_DATA_LOADED, false)
        
        if (!dataLoaded) {
            val patientCount = patientDao.getPatientCount()
            
            if (patientCount == 0) {
                // Load data from CSV
                val userDataList = CSVDataParser.parseUserData(context)
                
                // Convert to Patient entities and save to database
                val patientEntities = userDataList.map { userData ->
                    DataMapper.mapUserDataToPatientEntity(userData)
                }
                
                // Insert into database
                patientDao.insertPatients(patientEntities)
                
                // Mark data as loaded
                prefs.edit().putBoolean(KEY_DATA_LOADED, true).apply()
            }
        }
    }
    
    fun getAllPatients(): Flow<List<UserData>> {
        return patientDao.getAllPatients().map { patientEntities ->
            patientEntities.map { patientEntity ->
                DataMapper.mapPatientEntityToUserData(patientEntity)
            }
        }
    }
    
    suspend fun getPatientById(userId: String): UserData? {
        val patientEntity = patientDao.getPatientById(userId)
        return patientEntity?.let { DataMapper.mapPatientEntityToUserData(it) }
    }
} 