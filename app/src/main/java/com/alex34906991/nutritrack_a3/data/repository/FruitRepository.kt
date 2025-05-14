package com.alex34906991.nutritrack_a3.data.repository

import android.content.Context
import com.alex34906991.nutritrack_a3.data.api.ApiClient
import com.alex34906991.nutritrack_a3.data.model.Fruit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class FruitRepository(private val context: Context) {
    private val fruitApi = ApiClient.fruityviceApi
    private var cachedFruits: List<Fruit>? = null

    // Get all fruits from the API
    suspend fun getAllFruits(): Flow<Result<List<Fruit>>> = flow {
        // Return cached fruits if available
        cachedFruits?.let {
            emit(Result.success(it))
            return@flow
        }

        try {
            val fruits = fruitApi.getAllFruits()
            cachedFruits = fruits
            emit(Result.success(fruits))
        } catch (e: IOException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    // Search fruits by name (returns filtered list from cache)
    fun searchFruits(query: String): Flow<Result<List<Fruit>>> = flow {
        if (query.isBlank()) {
            emit(Result.success(emptyList()))
            return@flow
        }

        try {
            // Try to use cache first
            val fruits = cachedFruits ?: fruitApi.getAllFruits().also { cachedFruits = it }
            
            // Filter fruits by name containing query (case insensitive)
            val filteredFruits = fruits.filter { 
                it.name.contains(query, ignoreCase = true) 
            }
            
            emit(Result.success(filteredFruits))
        } catch (e: IOException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    // Get fruit details by name
    suspend fun getFruitByName(name: String): Flow<Result<Fruit>> = flow {
        try {
            // First check if it's in our cache
            cachedFruits?.firstOrNull { it.name.equals(name, ignoreCase = true) }?.let {
                emit(Result.success(it))
                return@flow
            }
            
            val fruit = fruitApi.getFruitByName(name)
            emit(Result.success(fruit))
        } catch (e: IOException) {
            emit(Result.failure(e))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
} 