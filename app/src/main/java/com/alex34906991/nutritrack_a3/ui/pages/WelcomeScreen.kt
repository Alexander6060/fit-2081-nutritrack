package com.alex34906991.nutritrack_a3.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    viewModel: NutriTrackViewModel? = null
) {
    // Check if user is already logged in
    if (viewModel != null) {
        val isLoggedIn by viewModel.isLoggedIn.collectAsState()
        
        // Auto-navigate to home if logged in
        LaunchedEffect(isLoggedIn) {
            if (isLoggedIn) {
                onLoginClick()
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "NutriTrack", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberAsyncImagePainter("file:///android_asset/nutri-logo.jpeg"),
            contentDescription = "Delicious Food",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Disclaimer text
        Text(text = "Disclaimer: This app is for educational purposes only.\nVisit Monash Nutrition Clinic for professional advice.")
        Spacer(modifier = Modifier.height(16.dp))

        // Link or mention
        Text(text = "www.monash.edu/nutrition")

        Spacer(modifier = Modifier.height(32.dp))

        // Student name + ID
        Text(text = "Alex Lai (34906991)")
        // Replace with your own student name and ID

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onLoginClick) {
            Text(text = "Login")
        }
    }
}
