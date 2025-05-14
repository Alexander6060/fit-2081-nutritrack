package com.alex34906991.nutritrack_a3.data

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

data class UserData(
    val phoneNumber: String,
    val userID: String,
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

object CSVDataParser {

    fun parseUserData(context: Context, fileName: String = "data.csv"): List<UserData> {
        val userList = mutableListOf<UserData>()

        val inputStream = context.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        // Read and map headers
        val headers = bufferedReader.readLine().split(",").map { it.trim() }

        bufferedReader.forEachLine { line ->
            val tokens = line.split(",").map { it.trim() }

            // Create a map of column headers to their values
            val dataMap = headers.zip(tokens).toMap()

            val userData = UserData(
                phoneNumber = dataMap["PhoneNumber"] ?: "",
                userID = dataMap["User_ID"] ?: "",
                sex = dataMap["Sex"] ?: "",
                totalHeifaScoreMale = dataMap["HEIFAtotalscoreMale"]?.toDoubleOrNull(),
                totalHeifaScoreFemale = dataMap["HEIFAtotalscoreFemale"]?.toDoubleOrNull(),
                discretionaryHeifaScoreMale = dataMap["DiscretionaryHEIFAscoreMale"]?.toDoubleOrNull(),
                discretionaryHeifaScoreFemale = dataMap["DiscretionaryHEIFAscoreFemale"]?.toDoubleOrNull(),
                discretionaryServeSize = dataMap["Discretionaryservesize"]?.toDoubleOrNull(),
                vegetableScoreMale = dataMap["VegetablesHEIFAscoreMale"]?.toDoubleOrNull(),
                vegetableScoreFemale = dataMap["VegetablesHEIFAscoreFemale"]?.toDoubleOrNull(),
                vegetableServeSize = dataMap["Vegetablesservesize"]?.toDoubleOrNull(),
                fruitScoreMale = dataMap["FruitsHEIFAscoreMale"]?.toDoubleOrNull(),
                fruitScoreFemale = dataMap["FruitsHEIFAscoreFemale"]?.toDoubleOrNull(),
                fruitServeSize = dataMap["Fruitsservesize"]?.toDoubleOrNull(),
                grainsScoreMale = dataMap["GrainsHEIFAscoreMale"]?.toDoubleOrNull(),
                grainsScoreFemale = dataMap["GrainsHEIFAscoreFemale"]?.toDoubleOrNull(),
                grainsServeSize = dataMap["Grainsservesize"]?.toDoubleOrNull(),
                meatScoreMale = dataMap["MeatHEIFAscoreMale"]?.toDoubleOrNull(),
                meatScoreFemale = dataMap["MeatHEIFAscoreFemale"]?.toDoubleOrNull(),
                meatServeSize = dataMap["Meatservesize"]?.toDoubleOrNull(),
                dairyScoreMale = dataMap["DairyHEIFAscoreMale"]?.toDoubleOrNull(),
                dairyScoreFemale = dataMap["DairyHEIFAscoreFemale"]?.toDoubleOrNull(),
                dairyServeSize = dataMap["Dairyservesize"]?.toDoubleOrNull(),
                waterIntake = dataMap["WaterIntake"]?.toDoubleOrNull(),
                fatSaturatedScoreMale = dataMap["SaturatedFatHEIFAscoreMale"]?.toDoubleOrNull(),
                fatSaturatedScoreFemale = dataMap["SaturatedFatHEIFAscoreFemale"]?.toDoubleOrNull(),
                fatSaturatedIntake = dataMap["SaturatedFat"]?.toDoubleOrNull(),
                fatUnsaturatedScoreMale = dataMap["UnsaturatedFatHEIFAscoreMale"]?.toDoubleOrNull(),
                fatUnsaturatedScoreFemale = dataMap["UnsaturatedFatHEIFAscoreFemale"]?.toDoubleOrNull(),
                fatUnsaturatedServeSize = dataMap["UnsaturatedFatservesize"]?.toDoubleOrNull(),
                sodiumScoreMale = dataMap["SodiumHEIFAscoreMale"]?.toDoubleOrNull(),
                sodiumScoreFemale = dataMap["SodiumHEIFAscoreFemale"]?.toDoubleOrNull(),
                sodiumIntake = dataMap["Sodium"]?.toDoubleOrNull(),
                sugarScoreMale = dataMap["SugarHEIFAscoreMale"]?.toDoubleOrNull(),
                sugarScoreFemale = dataMap["SugarHEIFAscoreFemale"]?.toDoubleOrNull(),
                addedSugarIntake = dataMap["Sugar"]?.toDoubleOrNull(),
                alcoholScoreMale = dataMap["AlcoholHEIFAscoreMale"]?.toDoubleOrNull(),
                alcoholScoreFemale = dataMap["AlcoholHEIFAscoreFemale"]?.toDoubleOrNull(),
                alcoholIntake = dataMap["Alcohol"]?.toDoubleOrNull()
            )

            userList.add(userData)
        }

        bufferedReader.close()
        return userList
    }
}
