package com.alex34906991.nutritrack_a3.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<PatientEntity>>
    
    @Query("SELECT * FROM patients WHERE userId = :userId")
    suspend fun getPatientById(userId: String): PatientEntity?
    
    @Query("SELECT * FROM patients WHERE userId = :userId AND phoneNumber = :phoneNumber")
    suspend fun getPatientByIdAndPhone(userId: String, phoneNumber: String): PatientEntity?
    
    @Query("SELECT * FROM patients WHERE userId = :userId AND password = :password")
    suspend fun getPatientByIdAndPassword(userId: String, password: String): PatientEntity?
    
    @Query("SELECT * FROM patients WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInPatient(): PatientEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<PatientEntity>)
    
    @Update
    suspend fun updatePatient(patient: PatientEntity)
    
    @Query("UPDATE patients SET isLoggedIn = 0")
    suspend fun logoutAllPatients()
    
    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int
} 