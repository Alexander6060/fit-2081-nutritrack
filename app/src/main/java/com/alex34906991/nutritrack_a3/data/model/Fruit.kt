package com.alex34906991.nutritrack_a3.data.model

import com.google.gson.annotations.SerializedName

data class Fruit(
    val id: Int,
    val name: String,
    val family: String,
    val genus: String,
    @SerializedName("nutritions")
    val nutrition: Nutrition
)

data class Nutrition(
    val calories: Int,
    val fat: Double,
    val sugar: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fiber: Double? = null
) 