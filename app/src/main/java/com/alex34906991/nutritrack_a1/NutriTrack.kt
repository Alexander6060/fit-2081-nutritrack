package com.alex34906991.nutritrack_a1
import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.application
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alex34906991.nutritrack_a1.data.CSVDataParser
import com.alex34906991.nutritrack_a1.data.UserData
import com.alex34906991.nutritrack_a1.ui.NutriTrackViewModel
import com.alex34906991.nutritrack_a1.navigation.AppNavHost

@Composable
fun NutriTrackApp(viewModel: NutriTrackViewModel = viewModel()) {
    // Load CSV data from assets one time
    val context = viewModel.application.applicationContext
    val userList = remember { mutableStateListOf<UserData>() }

    LaunchedEffect(key1 = true) {
        val data = CSVDataParser.parseUserData(context)
        userList.clear()
        userList.addAll(data)
    }

    // Pass the userList into the viewModel or whichever approach you prefer
    viewModel.setUsers(userList)

    // Start the Navigation
    AppNavHost(viewModel = viewModel)
}
