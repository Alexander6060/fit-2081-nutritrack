package com.alex34906991.nutritrack_a1.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo or Image can go here
        Text(text = "NutriTrack", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Disclaimer text
        Text(text = "Disclaimer: This app is for educational purposes only.\nVisit Monash Nutrition Clinic for professional advice.")
        Spacer(modifier = Modifier.height(16.dp))

        // Link or mention
        Text(text = "www.monash.edu/nutrition")

        Spacer(modifier = Modifier.height(32.dp))

        // Student name + ID
        Text(text = "Alex Scott (14578373)")
        // Replace with your own student name and ID

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onLoginClick) {
            Text(text = "Login")
        }
    }
}
