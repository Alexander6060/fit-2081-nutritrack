package com.alex34906991.nutritrack_a3.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alex34906991.nutritrack_a3.data.UserData
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel

@Composable
fun HomeScreen(
    viewModel: NutriTrackViewModel,
    onEditClick: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val foodScore = calculateFoodScore(user)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Hello, ${user?.name ?: user?.userID ?: "Guest"}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = rememberAsyncImagePainter("file:///android_asset/foods.png"),
                contentDescription = "Delicious Food",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Food Quality Score: $foodScore / 100",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Detailed explanation about Food Quality Score
            Text(
                text = "Food Quality Score is a comprehensive measure of your dietary health. " +
                        "It is derived from the HEIFA index, which evaluates the balance of your nutrient " +
                        "intake, the inclusion of fruits and vegetables, whole grains, and overall eating patterns. " +
                        "A higher score suggests that your diet is more balanced and nutritionally robust.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onEditClick) {
                Text("Edit Questionnaire")
            }
        }
    }
}

private fun calculateFoodScore(user: UserData?): Int {
    if (user == null) return 0
    
    // Simple mock score calculation - in a real app this would be more sophisticated
    val baseScore = if (user.sex.equals("Male", ignoreCase = true)) {
        user.totalHeifaScoreMale
    } else {
        user.totalHeifaScoreFemale
    } ?: 50.0
    
    return (baseScore * 100 / 100.0).toInt().coerceIn(0, 100)
}
