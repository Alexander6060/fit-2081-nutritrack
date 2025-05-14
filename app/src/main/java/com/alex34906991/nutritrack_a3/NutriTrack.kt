package com.alex34906991.nutritrack_a3
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alex34906991.nutritrack_a3.data.UserData
import com.alex34906991.nutritrack_a3.data.repository.PatientRepository
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel
import com.alex34906991.nutritrack_a3.navigation.AppNavHost
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun NutriTrackApp(viewModel: NutriTrackViewModel = viewModel()) {
    // Use the existing repository from the ViewModel instead of creating a new one
    val patientRepository = viewModel.getPatientRepository()
    val userList = remember { mutableStateListOf<UserData>() }

    // Load data from database (which will load from CSV if first run)
    LaunchedEffect(key1 = true) {
        // First, ensure data is loaded from CSV to database if needed
        patientRepository.loadInitialDataIfNeeded()
        
        // Then fetch all patients from the database
        patientRepository.getAllPatients().firstOrNull()?.let { patients ->
            userList.clear()
            userList.addAll(patients)
        }
    }

    // Pass the userList into the viewModel
    viewModel.setUsers(userList)

    // Start the Navigation
    AppNavHost(viewModel = viewModel)
}
