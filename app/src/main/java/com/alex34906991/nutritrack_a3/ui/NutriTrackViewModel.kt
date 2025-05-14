package com.alex34906991.nutritrack_a3.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alex34906991.nutritrack_a3.data.UserData
import com.alex34906991.nutritrack_a3.data.database.FoodIntakeEntity
import com.alex34906991.nutritrack_a3.data.database.NutriCoachTipEntity
import com.alex34906991.nutritrack_a3.data.repository.FoodIntakeRepository
import com.alex34906991.nutritrack_a3.data.repository.NutriCoachRepository
import com.alex34906991.nutritrack_a3.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class NutriTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val patientRepository = PatientRepository(application.applicationContext)
    private val foodIntakeRepository = FoodIntakeRepository(application.applicationContext)
    private val nutriCoachRepository = NutriCoachRepository(application.applicationContext)

    private val _users = mutableListOf<UserData>()
    val users: List<UserData> get() = _users

    private var _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser

    private val _foodIntakes = MutableStateFlow<List<FoodIntakeEntity>>(emptyList())
    val foodIntakes: StateFlow<List<FoodIntakeEntity>> = _foodIntakes

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isNewUser = MutableStateFlow(false)
    val isNewUser: StateFlow<Boolean> = _isNewUser
    
    private val _authStatus = MutableStateFlow<AuthStatus>(AuthStatus.Idle)
    val authStatus: StateFlow<AuthStatus> = _authStatus

    private var personaSelection: String? = null
    private var biggestMealTime: String? = null
    private var sleepTime: String? = null
    private var wakeTime: String? = null
    
    // NutriCoach related state
    private val _nutriCoachTips = MutableStateFlow<List<NutriCoachTipEntity>>(emptyList())
    val nutriCoachTips: StateFlow<List<NutriCoachTipEntity>> = _nutriCoachTips
    
    private val _latestTip = MutableStateFlow<String?>(null)
    val latestTip: StateFlow<String?> = _latestTip
    
    private val _isFruitScoreOptimal = MutableStateFlow(false)
    val isFruitScoreOptimal: StateFlow<Boolean> = _isFruitScoreOptimal

    init {
        // Load initial data if needed
        viewModelScope.launch {
            patientRepository.loadInitialDataIfNeeded()
            
            // Check for an already logged-in user
            checkLoggedInUser()
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
    
    // Check if there's an already logged-in user
    private suspend fun checkLoggedInUser() {
        val loggedInUser = patientRepository.getLoggedInUser()
        if (loggedInUser != null) {
            _currentUser.value = loggedInUser
            _isLoggedIn.value = true
            
            // Load food intakes for this user
            loadFoodIntakesForUser(loggedInUser.userID)
        }
    }
    
    // Load food intakes for a user
    private fun loadFoodIntakesForUser(userId: String) {
        viewModelScope.launch {
            foodIntakeRepository.getFoodIntakesByPatient(userId).collect { intakes ->
                _foodIntakes.value = intakes
            }
        }
    }
    
    // Verify user credentials (first screen)
    fun verifyUserCredentials(userId: String, phoneNumber: String) {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            
            val verified = patientRepository.verifyPatient(userId, phoneNumber)
            if (verified) {
                val user = patientRepository.getPatientById(userId)
                if (user != null) {
                    if (user.password == null) {
                        // New user that needs to register
                        _isNewUser.value = true
                        _authStatus.value = AuthStatus.NeedRegistration(userId, phoneNumber)
                    } else {
                        _isNewUser.value = false
                        _authStatus.value = AuthStatus.NeedPassword(userId)
                    }
                } else {
                    _authStatus.value = AuthStatus.Error("User not found")
                }
            } else {
                _authStatus.value = AuthStatus.Error("Invalid User ID or Phone Number")
            }
        }
    }
    
    // Register a new user (set name and password)
    fun registerUser(userId: String, phoneNumber: String, name: String, password: String) {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            
            val success = patientRepository.registerAccount(userId, phoneNumber, name, password)
            if (success) {
                val user = patientRepository.getPatientById(userId)
                if (user != null) {
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    loadFoodIntakesForUser(userId)
                    _authStatus.value = AuthStatus.Success
                } else {
                    _authStatus.value = AuthStatus.Error("Error loading user after registration")
                }
            } else {
                _authStatus.value = AuthStatus.Error("Registration failed")
            }
        }
    }
    
    // Login an existing user
    fun login(userId: String, password: String) {
        viewModelScope.launch {
            _authStatus.value = AuthStatus.Loading
            
            val user = patientRepository.login(userId, password)
            if (user != null) {
                _currentUser.value = user
                _isLoggedIn.value = true
                loadFoodIntakesForUser(userId)
                _authStatus.value = AuthStatus.Success
            } else {
                _authStatus.value = AuthStatus.Error("Invalid credentials")
            }
        }
    }
    
    // Logout the current user
    fun logout() {
        viewModelScope.launch {
            patientRepository.logout()
            _currentUser.value = null
            _isLoggedIn.value = false
            _foodIntakes.value = emptyList()
            _authStatus.value = AuthStatus.Idle
        }
    }
    
    // Reset authentication status
    fun resetAuthStatus() {
        _authStatus.value = AuthStatus.Idle
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
    
    // NutriCoach Methods
    
    // Generate a motivational tip using Gemini AI
    fun generateMotivationalTip() {
        val user = _currentUser.value ?: return
        
        viewModelScope.launch {
            try {
                val tip = nutriCoachRepository.generateMotivationalTip(user)
                _latestTip.value = tip
                
                // Save the tip to the database
                nutriCoachRepository.saveTip(user.userID, tip)
                
                // Update the tips list
                loadTipsForCurrentUser()
            } catch (e: Exception) {
                // Handle errors - could set an error state here
                _latestTip.value = "Enjoy a variety of fruits daily for better health! 🍎 🍌"
            }
        }
    }
    
    // Load all tips for the current user
    fun loadTipsForCurrentUser() {
        val userId = _currentUser.value?.userID ?: return
        
        viewModelScope.launch {
            nutriCoachRepository.getTipsForUser(userId).collect { tips ->
                _nutriCoachTips.value = tips
            }
        }
    }
    
    // Check if the user's fruit score is optimal
    fun checkFruitScore() {
        val user = _currentUser.value ?: return
        _isFruitScoreOptimal.value = nutriCoachRepository.isFruitScoreOptimal(user)
    }
    
    // Get the latest tip for the current user
    fun loadLatestTip() {
        val userId = _currentUser.value?.userID ?: return
        
        viewModelScope.launch {
            val latestTipEntity = nutriCoachRepository.getLatestTipForUser(userId)
            _latestTip.value = latestTipEntity?.message
        }
    }
}

// Authentication status sealed class
sealed class AuthStatus {
    object Idle : AuthStatus()
    object Loading : AuthStatus()
    object Success : AuthStatus()
    data class NeedRegistration(val userId: String, val phoneNumber: String) : AuthStatus()
    data class NeedPassword(val userId: String) : AuthStatus()
    data class Error(val message: String) : AuthStatus()
}
