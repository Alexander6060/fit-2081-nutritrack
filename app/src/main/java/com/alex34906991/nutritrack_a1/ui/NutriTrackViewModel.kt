package com.alex34906991.nutritrack_a1.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alex34906991.nutritrack_a1.data.UserData

class NutriTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val _users = mutableListOf<UserData>()
    val users: List<UserData> get() = _users

    private var currentUser: UserData? = null
    private var personaSelection: String? = null
    private var biggestMealTime: String? = null
    private var sleepTime: String? = null
    private var wakeTime: String? = null

    fun setUsers(users: List<UserData>) {
        _users.clear()
        _users.addAll(users)
    }

    fun getCurrentUser(): UserData? {
        return currentUser
    }

    fun login(userId: String, phoneNumber: String): Boolean {
        val foundUser = _users.find { it.userID == userId && it.phoneNumber == phoneNumber }
        if (foundUser != null) {
            currentUser = foundUser
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
    }
}
