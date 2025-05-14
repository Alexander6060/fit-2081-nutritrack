package com.alex34906991.nutritrack_a3.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<PatientEntity>>
    
    @Query("SELECT * FROM patients WHERE userId = :userId")
    suspend fun getPatientById(userId: String): PatientEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<PatientEntity>)
    
    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int
} 