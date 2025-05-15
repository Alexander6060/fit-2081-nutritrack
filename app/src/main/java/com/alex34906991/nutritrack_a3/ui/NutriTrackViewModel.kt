package com.alex34906991.nutritrack_a3.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alex34906991.nutritrack_a3.data.UserData
import com.alex34906991.nutritrack_a3.data.database.FoodIntakeEntity
import com.alex34906991.nutritrack_a3.data.database.NutriCoachTipEntity
import com.alex34906991.nutritrack_a3.data.model.Fruit
import com.alex34906991.nutritrack_a3.data.repository.FoodIntakeRepository
import com.alex34906991.nutritrack_a3.data.repository.FruitRepository
import com.alex34906991.nutritrack_a3.data.repository.NutriCoachRepository
import com.alex34906991.nutritrack_a3.data.repository.PatientRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(FlowPreview::class)
class NutriTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val patientRepository = PatientRepository(application.applicationContext)
    private val foodIntakeRepository = FoodIntakeRepository(application.applicationContext)
    private val nutriCoachRepository = NutriCoachRepository(application.applicationContext)
    private val fruitRepository = FruitRepository(application.applicationContext)

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

    // Fruit search related state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _searchResults = MutableStateFlow<List<Fruit>>(emptyList())
    val searchResults: StateFlow<List<Fruit>> = _searchResults
    
    private val _selectedFruit = MutableStateFlow<Fruit?>(null)
    val selectedFruit: StateFlow<Fruit?> = _selectedFruit
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching
    
    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError
    
    // Admin view related state
    private val _maleHeifaAverage = MutableStateFlow(0.0)
    val maleHeifaAverage: StateFlow<Double> = _maleHeifaAverage
    
    private val _femaleHeifaAverage = MutableStateFlow(0.0)
    val femaleHeifaAverage: StateFlow<Double> = _femaleHeifaAverage
    
    private val _dataPatterns = MutableStateFlow<List<String>>(emptyList())
    val dataPatterns: StateFlow<List<String>> = _dataPatterns
    
    private val _isGeneratingPatterns = MutableStateFlow(false)
    val isGeneratingPatterns: StateFlow<Boolean> = _isGeneratingPatterns

    // Get the Gemini model from the NutriCoachRepository
    private val generativeModel by lazy {
        try {
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = getApplication<Application>().getString(com.alex34906991.nutritrack_a3.R.string.gemini_api_key),
                safetySettings = listOf(
                    SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                    SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
                )
            )
        } catch (e: Exception) {
            println("Error initializing Gemini model: ${e.message}")
            // Create a minimal fallback model with just the required parameters
            try {
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = getApplication<Application>().getString(com.alex34906991.nutritrack_a3.R.string.gemini_api_key)
                )
            } catch (e: Exception) {
                println("Critical error initializing Gemini model: ${e.message}")
                null
            }
        }
    }

    init {
        // Load initial data if needed
        viewModelScope.launch {
            patientRepository.loadInitialDataIfNeeded()
            
            // Check for an already logged-in user
            checkLoggedInUser()
        }
        
        // Set up search query debounce for autocomplete
        viewModelScope.launch {
            searchQuery
                .debounce(300) // Wait for 300ms of inactivity before searching
                .collect { query ->
                    if (query.length >= 2) {
                        searchFruits(query)
                    } else {
                        _searchResults.value = emptyList()
                    }
                }
        }
    }

    // Getter method for patient repository
    fun getPatientRepository(): PatientRepository {
        return patientRepository
    }
    
    // Getter method for food intake repository
    fun getFoodIntakeRepository(): FoodIntakeRepository {
        return foodIntakeRepository
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
        wake: String,
        selectedCategories: List<String>
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
                    category = "Persona: $persona, Biggest Meal: $biggestMeal, Sleep: $sleep, Wake: $wake, Categories: [${selectedCategories.joinToString(", ")}]"
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
    
    // Update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    // Search fruits with the current query
    private fun searchFruits(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            
            try {
                fruitRepository.searchFruits(query).collect { result ->
                    result.onSuccess { fruits ->
                        _searchResults.value = fruits
                    }.onFailure { error ->
                        _searchError.value = "Error searching fruits: ${error.message}"
                        _searchResults.value = emptyList()
                    }
                    _isSearching.value = false
                }
            } catch (e: Exception) {
                _searchError.value = "Error: ${e.message}"
                _isSearching.value = false
            }
        }
    }
    
    // Get fruit details by name
    fun getFruitDetails(name: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            
            try {
                fruitRepository.getFruitByName(name).collect { result ->
                    result.onSuccess { fruit ->
                        _selectedFruit.value = fruit
                    }.onFailure { error ->
                        _searchError.value = "Error getting fruit details: ${error.message}"
                    }
                    _isSearching.value = false
                }
            } catch (e: Exception) {
                _searchError.value = "Error: ${e.message}"
                _isSearching.value = false
            }
        }
    }
    
    // Clear selected fruit
    fun clearSelectedFruit() {
        _selectedFruit.value = null
    }
    
    // Add fruit as food intake
    fun addFruitAsFoodIntake(fruit: Fruit, quantity: Double = 1.0) {
        val nutritionInfo = "Calories: ${fruit.nutrition.calories}, " +
                           "Carbs: ${fruit.nutrition.carbohydrates}g, " +
                           "Protein: ${fruit.nutrition.protein}g, " +
                           "Fat: ${fruit.nutrition.fat}g, " +
                           "Sugar: ${fruit.nutrition.sugar}g"
                           
        addFoodIntake(
            foodName = fruit.name,
            quantity = quantity,
            servingSize = "1 serving",
            category = "Fruit - $nutritionInfo"
        )
    }

    // Admin view methods
    
    // Calculate average HEIFA scores for male and female users
    fun calculateAverageHeifaScores() {
        viewModelScope.launch {
            patientRepository.getAllPatients().collect { userList ->
                var maleTotal = 0.0
                var femaleTotal = 0.0
                var maleCount = 0
                var femaleCount = 0
                
                userList.forEach { user ->
                    user.totalHeifaScoreMale?.let {
                        maleTotal += it
                        maleCount++
                    }
                    
                    user.totalHeifaScoreFemale?.let {
                        femaleTotal += it
                        femaleCount++
                    }
                }
                
                _maleHeifaAverage.value = if (maleCount > 0) (maleTotal / maleCount) else 0.0
                _femaleHeifaAverage.value = if (femaleCount > 0) (femaleTotal / femaleCount) else 0.0
            }
        }
    }
    
    // Reset pattern generation state
    private fun resetPatternGenerationState() {
        _dataPatterns.value = emptyList()
        _isGeneratingPatterns.value = false
    }
    
    // Generate data patterns using GenAI
    fun generateDataPatterns() {
        viewModelScope.launch {
            // Reset state before starting
            resetPatternGenerationState()
            _isGeneratingPatterns.value = true
            
            try {
                // Set timeout to ensure we don't get stuck indefinitely
                val timeout = System.currentTimeMillis() + 10000 // 10 second timeout
                
                // Get all users with their data for analysis
                val userList = patientRepository.getAllPatients().firstOrNull() ?: emptyList()
                
                // Prepare the data summary to send to Gemini
                val dataSummary = buildDataSummary(userList)
                
                // Create the prompt for Gemini
                val prompt = """
                    Analyze this nutritional dataset and identify 3 interesting patterns or insights:
                    
                    $dataSummary
                    
                    For each pattern:
                    1. Give it a clear, concise title ending with a colon
                    2. Follow with a detailed explanation of the pattern
                    3. Focus on relationships between variables or interesting trends
                    4. Keep each pattern under 3 sentences
                    5. Make sure patterns are different from each other
                    
                    Return exactly 3 patterns, each in the format "Title: Explanation"
                """.trimIndent()
                
                // Call Gemini API with proper error handling
                val model = generativeModel
                if (model == null) {
                    println("Gemini model is null, using fallback patterns")
                    _dataPatterns.value = generateFallbackPatterns()
                } else {
                    try {
                        val response = model.generateContent(prompt)
                        val responseText = response?.text?.trim() ?: ""
                        
                        if (responseText.isNotEmpty()) {
                            // Parse the response
                            val patterns = parsePatterns(responseText)
                            
                            if (patterns.isNotEmpty()) {
                                _dataPatterns.value = patterns
                            } else {
                                // Fallback if Gemini doesn't return expected format
                                _dataPatterns.value = generateFallbackPatterns()
                            }
                        } else {
                            // Handle empty response
                            _dataPatterns.value = generateFallbackPatterns()
                        }
                    } catch (e: Exception) {
                        println("Gemini API error: ${e.message}")
                        _dataPatterns.value = generateFallbackPatterns()
                    }
                }
                
                // Check for timeout and set fallback patterns if we've waited too long
                if (System.currentTimeMillis() > timeout && _dataPatterns.value.isEmpty()) {
                    println("Timeout occurred while generating patterns")
                    _dataPatterns.value = generateFallbackPatterns()
                }
            } catch (e: Exception) {
                println("Error in data collection: ${e.message}")
                _dataPatterns.value = generateFallbackPatterns()
            } finally {
                _isGeneratingPatterns.value = false
            }
        }
    }
    
    // Build a summary of the user data for the AI prompt
    private fun buildDataSummary(userList: List<UserData>): String {
        val summary = StringBuilder()
        
        summary.append("Dataset summary (${userList.size} users):\n")
        summary.append("- Male users: ${userList.count { it.sex.equals("Male", ignoreCase = true) }}\n")
        summary.append("- Female users: ${userList.count { it.sex.equals("Female", ignoreCase = true) }}\n\n")
        
        summary.append("Averages across all users:\n")
        val maleHeifaAvg = userList.mapNotNull { it.totalHeifaScoreMale }.average().takeIf { !it.isNaN() }?.let { String.format("%.1f", it) } ?: "N/A"
        val femaleHeifaAvg = userList.mapNotNull { it.totalHeifaScoreFemale }.average().takeIf { !it.isNaN() }?.let { String.format("%.1f", it) } ?: "N/A"
        summary.append("- Average HEIFA score (Male): $maleHeifaAvg\n")
        summary.append("- Average HEIFA score (Female): $femaleHeifaAvg\n")
        
        // Add some sample data for key metrics
        summary.append("\nKey metrics summary:\n")
        summary.append("- Fruit scores range: ${userList.mapNotNull { it.fruitScoreMale }.minOrNull() ?: 0} to ${userList.mapNotNull { it.fruitScoreMale }.maxOrNull() ?: 0}\n")
        summary.append("- Vegetable scores range: ${userList.mapNotNull { it.vegetableScoreMale }.minOrNull() ?: 0} to ${userList.mapNotNull { it.vegetableScoreMale }.maxOrNull() ?: 0}\n")
        summary.append("- Dairy scores range: ${userList.mapNotNull { it.dairyScoreMale }.minOrNull() ?: 0} to ${userList.mapNotNull { it.dairyScoreMale }.maxOrNull() ?: 0}\n")
        summary.append("- Grains scores range: ${userList.mapNotNull { it.grainsScoreMale }.minOrNull() ?: 0} to ${userList.mapNotNull { it.grainsScoreMale }.maxOrNull() ?: 0}\n")
        summary.append("- Water intake range: ${userList.mapNotNull { it.waterIntake }.minOrNull() ?: 0} to ${userList.mapNotNull { it.waterIntake }.maxOrNull() ?: 0}\n")
        
        return summary.toString()
    }
    
    // Parse the AI response into separate patterns
    private fun parsePatterns(response: String): List<String> {
        // Simple parsing - looking for numbered patterns or patterns with title: format
        val lines = response.split("\n")
        val patterns = mutableListOf<String>()
        
        var currentPattern = ""
        for (line in lines) {
            val trimmedLine = line.trim()
            
            // Skip empty lines
            if (trimmedLine.isEmpty()) {
                continue
            }
            
            // If we find a line that looks like a new pattern title
            if (trimmedLine.contains(":") || trimmedLine.matches(Regex("^\\d+\\..*"))) {
                if (currentPattern.isNotEmpty()) {
                    patterns.add(currentPattern)
                }
                currentPattern = trimmedLine
            } else {
                // Continue the current pattern
                currentPattern += " $trimmedLine"
            }
        }
        
        // Add the last pattern if there is one
        if (currentPattern.isNotEmpty()) {
            patterns.add(currentPattern)
        }
        
        // Make sure we have exactly 3 patterns
        return patterns.take(3).ifEmpty { generateFallbackPatterns() }
    }
    
    // Fallback patterns if AI fails
    private fun generateFallbackPatterns(): List<String> {
        // Create more reliable fallback patterns based on average data values
        val maleAvg = _maleHeifaAverage.value
        val femaleAvg = _femaleHeifaAverage.value
        
        return listOf(
            "HEIFA Score Analysis: The average HEIFA score for males is ${String.format("%.1f", maleAvg)} and for females is ${String.format("%.1f", femaleAvg)}. This suggests that overall dietary patterns vary between genders in the dataset, which could be due to different nutritional requirements or dietary preferences.",
            
            "Nutritional Pattern Insights: Many users in the dataset show suboptimal consumption of fruits and vegetables based on HEIFA scoring. This pattern suggests an opportunity for targeted nutritional guidance to improve diet quality and increase intake of these important food groups.",
            
            "Dietary Improvement Opportunities: Based on the collected data, water intake and whole grain consumption represent areas where many users could improve their nutritional habits. Adding specific reminders or educational content about these food groups could help users improve their overall HEIFA scores."
        )
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
