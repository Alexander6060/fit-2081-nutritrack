package com.alex34906991.nutritrack_a3.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel

@Composable
fun InsightsScreen(
    viewModel: NutriTrackViewModel,
    onImproveDietClick: () -> Unit
) {
    val user = viewModel.getCurrentUser()
    // State for showing the share dialog
    var showShareDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    // Fetch values based on gender
    fun getScore(maleValue: Double?, femaleValue: Double?): Float {
        return if (user?.sex?.lowercase() == "male") {
            maleValue?.toFloat() ?: 0f
        } else {
            femaleValue?.toFloat() ?: 0f
        }
    }

    // Define category scores & max values based on dietary guidelines
    val categoryScores = listOf(
        "Discretionary Foods" to Pair(getScore(user?.discretionaryHeifaScoreMale, user?.discretionaryHeifaScoreFemale), 10f),
        "Vegetables" to Pair(getScore(user?.vegetableScoreMale, user?.vegetableScoreFemale), 5f),
        "Fruits" to Pair(getScore(user?.fruitScoreMale, user?.fruitScoreFemale), 5f),
        "Grains & Cereals" to Pair(getScore(user?.grainsScoreMale, user?.grainsScoreFemale), 5f),
        "Meat & Alternatives" to Pair(getScore(user?.meatScoreMale, user?.meatScoreFemale), 10f),
        "Dairy & Alternatives" to Pair(getScore(user?.dairyScoreMale, user?.dairyScoreFemale), 10f),
        "Water" to Pair(user?.waterIntake?.toFloat() ?: 0f, 5f),
        "Saturated Fats" to Pair(getScore(user?.fatSaturatedScoreMale, user?.fatSaturatedScoreFemale), 5f),
        "Unsaturated Fats" to Pair(getScore(user?.fatUnsaturatedScoreMale, user?.fatUnsaturatedScoreFemale), 5f),
        "Sodium" to Pair(getScore(user?.sodiumScoreMale, user?.sodiumScoreFemale), 10f),
        "Added Sugars" to Pair(getScore(user?.sugarScoreMale, user?.sugarScoreFemale), 10f),
        "Alcohol" to Pair(getScore(user?.alcoholScoreMale, user?.alcoholScoreFemale), 5f)
    )

    val totalScore = remember(user) {
        if (user?.sex?.lowercase() == "male") {
            user?.totalHeifaScoreMale ?: 0.0
        } else {
            user?.totalHeifaScoreFemale ?: 0.0
        }
    }

    // Create a scroll state for the vertical scroll
    val scrollState = rememberScrollState()

    // Generate shareable text
    val shareableText = buildString {
        append("My NutriTrack Dietary Score: ${"%.2f".format(totalScore)}\n\n")
        append("Detailed Breakdown:\n")
        categoryScores.forEach { (label, scorePair) ->
            val (score, maxScore) = scorePair
            append("$label: ${"%.1f".format(score)}/$maxScore\n")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)  // <-- This makes the Column scrollable
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Detailed Score Breakdown", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Display scores dynamically
        categoryScores.forEach { (label, scorePair) ->
            val (score, maxScore) = scorePair
            ScoreProgressBar(label, score, maxScore)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Total HEIFA Score: ${"%.2f".format(totalScore)}", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showShareDialog = true }) {
            Text(text = "Share with someone")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onImproveDietClick) {
            Text(text = "Improve my diet")
        }
    }
    
    // Share Dialog
    if (showShareDialog) {
        Dialog(onDismissRequest = { showShareDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Share Your Nutrition Data",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = shareableText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showShareDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(shareableText))
                                showShareDialog = false
                            }
                        ) {
                            Text("Copy")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreProgressBar(label: String, score: Float, maxScore: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        LinearProgressIndicator(
            progress = { score / maxScore },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
        )
        Text(text = "${"%.1f".format(score)} / $maxScore", style = MaterialTheme.typography.bodySmall)
    }
}
