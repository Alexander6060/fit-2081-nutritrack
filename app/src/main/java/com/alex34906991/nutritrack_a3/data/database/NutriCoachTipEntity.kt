package com.alex34906991.nutritrack_a3.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "nutricoach_tips",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NutriCoachTipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) 