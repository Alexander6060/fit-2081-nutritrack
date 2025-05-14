package com.alex34906991.nutritrack_a3.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey
    val userId: String,
    val phoneNumber: String,
    val sex: String,
    val name: String? = null,
    val password: String? = null,
    val isLoggedIn: Boolean = false,
    val totalHeifaScoreMale: Double?,
    val totalHeifaScoreFemale: Double?,
    val discretionaryHeifaScoreMale: Double?,
    val discretionaryHeifaScoreFemale: Double?,
    val discretionaryServeSize: Double?,
    val vegetableScoreMale: Double?,
    val vegetableScoreFemale: Double?,
    val vegetableServeSize: Double?,
    val fruitScoreMale: Double?,
    val fruitScoreFemale: Double?,
    val fruitServeSize: Double?,
    val grainsScoreMale: Double?,
    val grainsScoreFemale: Double?,
    val grainsServeSize: Double?,
    val meatScoreMale: Double?,
    val meatScoreFemale: Double?,
    val meatServeSize: Double?,
    val dairyScoreMale: Double?,
    val dairyScoreFemale: Double?,
    val dairyServeSize: Double?,
    val waterIntake: Double?,
    val fatSaturatedScoreMale: Double?,
    val fatSaturatedScoreFemale: Double?,
    val fatSaturatedIntake: Double?,
    val fatUnsaturatedScoreMale: Double?,
    val fatUnsaturatedScoreFemale: Double?,
    val fatUnsaturatedServeSize: Double?,
    val sodiumScoreMale: Double?,
    val sodiumScoreFemale: Double?,
    val sodiumIntake: Double?,
    val sugarScoreMale: Double?,
    val sugarScoreFemale: Double?,
    val addedSugarIntake: Double?,
    val alcoholScoreMale: Double?,
    val alcoholScoreFemale: Double?,
    val alcoholIntake: Double?
) 