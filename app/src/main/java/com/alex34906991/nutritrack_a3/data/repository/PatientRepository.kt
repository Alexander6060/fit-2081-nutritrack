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
    
    // New authentication methods
    
    /**
     * Verify a patient by user ID and phone number
     */
    suspend fun verifyPatient(userId: String, phoneNumber: String): Boolean {
        val patient = patientDao.getPatientByIdAndPhone(userId, phoneNumber)
        return patient != null
    }
    
    /**
     * Register a new account with name and password
     */
    suspend fun registerAccount(userId: String, phoneNumber: String, name: String, password: String): Boolean {
        val patient = patientDao.getPatientByIdAndPhone(userId, phoneNumber) ?: return false
        
        // Create a new entity with the updated fields
        val updatedPatient = PatientEntity(
            userId = patient.userId,
            phoneNumber = patient.phoneNumber,
            sex = patient.sex,
            name = name,
            password = password,
            isLoggedIn = true,
            totalHeifaScoreMale = patient.totalHeifaScoreMale,
            totalHeifaScoreFemale = patient.totalHeifaScoreFemale,
            discretionaryHeifaScoreMale = patient.discretionaryHeifaScoreMale,
            discretionaryHeifaScoreFemale = patient.discretionaryHeifaScoreFemale,
            discretionaryServeSize = patient.discretionaryServeSize,
            vegetableScoreMale = patient.vegetableScoreMale,
            vegetableScoreFemale = patient.vegetableScoreFemale,
            vegetableServeSize = patient.vegetableServeSize,
            fruitScoreMale = patient.fruitScoreMale,
            fruitScoreFemale = patient.fruitScoreFemale,
            fruitServeSize = patient.fruitServeSize,
            grainsScoreMale = patient.grainsScoreMale,
            grainsScoreFemale = patient.grainsScoreFemale,
            grainsServeSize = patient.grainsServeSize,
            meatScoreMale = patient.meatScoreMale,
            meatScoreFemale = patient.meatScoreFemale,
            meatServeSize = patient.meatServeSize,
            dairyScoreMale = patient.dairyScoreMale,
            dairyScoreFemale = patient.dairyScoreFemale,
            dairyServeSize = patient.dairyServeSize,
            waterIntake = patient.waterIntake,
            fatSaturatedScoreMale = patient.fatSaturatedScoreMale,
            fatSaturatedScoreFemale = patient.fatSaturatedScoreFemale,
            fatSaturatedIntake = patient.fatSaturatedIntake,
            fatUnsaturatedScoreMale = patient.fatUnsaturatedScoreMale,
            fatUnsaturatedScoreFemale = patient.fatUnsaturatedScoreFemale,
            fatUnsaturatedServeSize = patient.fatUnsaturatedServeSize,
            sodiumScoreMale = patient.sodiumScoreMale,
            sodiumScoreFemale = patient.sodiumScoreFemale,
            sodiumIntake = patient.sodiumIntake,
            sugarScoreMale = patient.sugarScoreMale,
            sugarScoreFemale = patient.sugarScoreFemale,
            addedSugarIntake = patient.addedSugarIntake,
            alcoholScoreMale = patient.alcoholScoreMale,
            alcoholScoreFemale = patient.alcoholScoreFemale,
            alcoholIntake = patient.alcoholIntake
        )
        
        patientDao.updatePatient(updatedPatient)
        return true
    }
    
    /**
     * Login with user ID and password
     */
    suspend fun login(userId: String, password: String): UserData? {
        val patient = patientDao.getPatientByIdAndPassword(userId, password) ?: return null
        
        // Update logged in status
        val updatedPatient = PatientEntity(
            userId = patient.userId,
            phoneNumber = patient.phoneNumber,
            sex = patient.sex,
            name = patient.name,
            password = patient.password,
            isLoggedIn = true,
            totalHeifaScoreMale = patient.totalHeifaScoreMale,
            totalHeifaScoreFemale = patient.totalHeifaScoreFemale,
            discretionaryHeifaScoreMale = patient.discretionaryHeifaScoreMale,
            discretionaryHeifaScoreFemale = patient.discretionaryHeifaScoreFemale,
            discretionaryServeSize = patient.discretionaryServeSize,
            vegetableScoreMale = patient.vegetableScoreMale,
            vegetableScoreFemale = patient.vegetableScoreFemale,
            vegetableServeSize = patient.vegetableServeSize,
            fruitScoreMale = patient.fruitScoreMale,
            fruitScoreFemale = patient.fruitScoreFemale,
            fruitServeSize = patient.fruitServeSize,
            grainsScoreMale = patient.grainsScoreMale,
            grainsScoreFemale = patient.grainsScoreFemale,
            grainsServeSize = patient.grainsServeSize,
            meatScoreMale = patient.meatScoreMale,
            meatScoreFemale = patient.meatScoreFemale,
            meatServeSize = patient.meatServeSize,
            dairyScoreMale = patient.dairyScoreMale,
            dairyScoreFemale = patient.dairyScoreFemale,
            dairyServeSize = patient.dairyServeSize,
            waterIntake = patient.waterIntake,
            fatSaturatedScoreMale = patient.fatSaturatedScoreMale,
            fatSaturatedScoreFemale = patient.fatSaturatedScoreFemale,
            fatSaturatedIntake = patient.fatSaturatedIntake,
            fatUnsaturatedScoreMale = patient.fatUnsaturatedScoreMale,
            fatUnsaturatedScoreFemale = patient.fatUnsaturatedScoreFemale,
            fatUnsaturatedServeSize = patient.fatUnsaturatedServeSize,
            sodiumScoreMale = patient.sodiumScoreMale,
            sodiumScoreFemale = patient.sodiumScoreFemale,
            sodiumIntake = patient.sodiumIntake,
            sugarScoreMale = patient.sugarScoreMale,
            sugarScoreFemale = patient.sugarScoreFemale,
            addedSugarIntake = patient.addedSugarIntake,
            alcoholScoreMale = patient.alcoholScoreMale,
            alcoholScoreFemale = patient.alcoholScoreFemale,
            alcoholIntake = patient.alcoholIntake
        )
        patientDao.updatePatient(updatedPatient)
        
        return DataMapper.mapPatientEntityToUserData(updatedPatient)
    }
    
    /**
     * Check if there's a user already logged in
     */
    suspend fun getLoggedInUser(): UserData? {
        val patient = patientDao.getLoggedInPatient() ?: return null
        return DataMapper.mapPatientEntityToUserData(patient)
    }
    
    /**
     * Logout the current user
     */
    suspend fun logout() {
        patientDao.logoutAllPatients()
    }
} 