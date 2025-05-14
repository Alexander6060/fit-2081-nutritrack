package com.alex34906991.nutritrack_a3.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alex34906991.nutritrack_a3.data.UserData
import com.alex34906991.nutritrack_a3.data.database.FoodIntakeEntity
import com.alex34906991.nutritrack_a3.data.repository.FoodIntakeRepository
import com.alex34906991.nutritrack_a3.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class NutriTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val patientRepository = PatientRepository(application.applicationContext)
    private val foodIntakeRepository = FoodIntakeRepository(application.applicationContext)

    private val _users = mutableListOf<UserData>()
    val users: List<UserData> get() = _users

    private var _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser

    private val _foodIntakes = MutableStateFlow<List<FoodIntakeEntity>>(emptyList())
    val foodIntakes: StateFlow<List<FoodIntakeEntity>> = _foodIntakes

    private var personaSelection: String? = null
    private var biggestMealTime: String? = null
    private var sleepTime: String? = null
    private var wakeTime: String? = null

    init {
        // Load initial data if needed
        viewModelScope.launch {
            patientRepository.loadInitialDataIfNeeded()
        }
    }

    // Getter method for patient repository
    fun getPatientRepository(): PatientRepository {
        return patientRepository
    }

    fun setUsers(users: List<UserData>) {
        _users.clear()
        _users.addAll(users)
    }

    fun getCurrentUser(): UserData? {
        return _currentUser.value
    }

    fun login(userId: String, phoneNumber: String): Boolean {
        val foundUser = _users.find { it.userID == userId && it.phoneNumber == phoneNumber }
        if (foundUser != null) {
            _currentUser.value = foundUser
            
            // Load food intakes for this user
            viewModelScope.launch {
                foodIntakeRepository.getFoodIntakesByPatient(userId).collect { intakes ->
                    _foodIntakes.value = intakes
                }
            }
            
            return true
        }
        return false
    }

    fun saveQuestionnaireData(
        persona: String,
        biggestMeal: String,
        sleep: String,
        wake: String
    ) {
        personaSelection = persona
        biggestMealTime = biggestMeal
        sleepTime = sleep
        wakeTime = wake
        
        // Save questionnaire as food intake entry
        val currentUserId = _currentUser.value?.userID
        if (currentUserId != null) {
            viewModelScope.launch {
                val foodIntake = FoodIntakeEntity(
                    patientId = currentUserId,
                    foodName = "Questionnaire Response",
                    quantity = null,
                    servingSize = null,
                    timeConsumed = Date().time,
                    category = "Persona: $persona, Biggest Meal: $biggestMeal, Sleep: $sleep, Wake: $wake"
                )
                foodIntakeRepository.insertFoodIntake(foodIntake)
            }
        }
    }
    
    fun addFoodIntake(foodName: String, quantity: Double?, servingSize: String?, category: String?) {
        val currentUserId = _currentUser.value?.userID
        if (currentUserId != null) {
            viewModelScope.launch {
                val foodIntake = FoodIntakeEntity(
                    patientId = currentUserId,
                    foodName = foodName,
                    quantity = quantity,
                    servingSize = servingSize,
                    timeConsumed = Date().time,
                    category = category
                )
                foodIntakeRepository.insertFoodIntake(foodIntake)
            }
        }
    }
}
