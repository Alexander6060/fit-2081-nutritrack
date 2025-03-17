package com.alex34906991.nutritrack_a1.data

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

data class UserData(
    val userID: String,
    val phoneNumber: String,
    val sex: String,
    val heifaTotalScoreMale: Double?,
    val heifaTotalScoreFemale: Double?,
    val vegetableScoreMale: Double?,
    val vegetableScoreFemale: Double?,
)

object CSVDataParser {

    fun parseUserData(context: Context, fileName: String = "data.csv"): List<UserData> {
        val userList = mutableListOf<UserData>()

        val inputStream = context.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        // Read header line first (to skip it)
        val headerLine = bufferedReader.readLine()
        val headers = headerLine.split(",")

        bufferedReader.forEachLine { line ->
            val tokens = line.split(",")

            if (tokens.size >= 10) { // Adjusting for required fields
                val phoneNumber = tokens[0].trim()
                val userID = tokens[1].trim()
                val sex = tokens[2].trim()

                val heifaMale = tokens.getOrNull(3)?.toDoubleOrNull()
                val heifaFemale = tokens.getOrNull(4)?.toDoubleOrNull()
                val vegetableMale = tokens.getOrNull(8)?.toDoubleOrNull()
                val vegetableFemale = tokens.getOrNull(9)?.toDoubleOrNull()

                userList.add(
                    UserData(
                        userID = userID,
                        phoneNumber = phoneNumber,
                        sex = sex,
                        heifaTotalScoreMale = heifaMale,
                        heifaTotalScoreFemale = heifaFemale,
                        vegetableScoreMale = vegetableMale,
                        vegetableScoreFemale = vegetableFemale
                    )
                )
            }
        }

        bufferedReader.close()
        return userList
    }
}
