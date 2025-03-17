package com.alex34906991.nutritrack_a1.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alex34906991.nutritrack_a1.ui.NutriTrackViewModel

@Composable
fun InsightsScreen(
    viewModel: NutriTrackViewModel,
    onImproveDietClick: () -> Unit
) {
    val user = viewModel.getCurrentUser()

    // We assume each category is out of a maximum score = 20, for example
    val maxCategoryScore = 20f

    val vegetableScore = remember(user) {
        if (user?.sex?.lowercase() == "male") {
            user?.vegetableScoreMale ?: 0
        } else {
            user?.vegetableScoreFemale ?: 0
        }
    }.toFloat()

//    val fruitScore = remember(user) {
//        if (user?.sex?.lowercase() == "male") {
//            user?.fruitScoreMale ?: 0
//        } else {
//            user?.fruitScoreFemale ?: 0
//        }
//    }.toFloat()

    val totalScore = remember(user) {
        if (user?.sex?.lowercase() == "male") {
            user?.heifaTotalScoreMale ?: 0
        } else {
            user?.heifaTotalScoreFemale ?: 0
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Detailed Score Breakdown")
        Spacer(modifier = Modifier.height(16.dp))

        // Vegetables
        Text("Vegetables")
        LinearProgressIndicator(
            progress = { (vegetableScore / maxCategoryScore) },
        )
        Text(text = "$vegetableScore / $maxCategoryScore")

        Spacer(modifier = Modifier.height(16.dp))

//        // Fruits
//        Text("Fruits")
//        LinearProgressIndicator(
//            progress = { (fruitScore / maxCategoryScore) },
//        )
//        Text(text = "$fruitScore / $maxCategoryScore")

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Total HEIFA Score: $totalScore")

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // For example, share or open share dialog
        }) {
            Text(text = "Share with someone")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onImproveDietClick) {
            Text(text = "Improve my diet")
        }
    }
}
