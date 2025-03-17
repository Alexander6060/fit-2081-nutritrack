package com.alex34906991.nutritrack_a1.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alex34906991.nutritrack_a1.ui.NutriTrackViewModel

@Composable
fun HomeScreen(
    viewModel: NutriTrackViewModel,
    onEditClick: () -> Unit
) {
    val user = viewModel.getCurrentUser()

    // Depending on the user's sex, retrieve the correct HEIFA total
    val foodScore = remember(user) {
        if (user?.sex?.lowercase() == "male") {
            user.heifaTotalScoreMale ?: 0
        } else {
            user?.heifaTotalScoreFemale ?: 0
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Hello, ${user?.sex ?: "Guest"}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Your Food Quality Score: $foodScore")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "This score is based on the HEIFA measure\nand indicates overall diet quality.")

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onEditClick) {
                Text("Edit Questionnaire")
            }
        }
    }
}
