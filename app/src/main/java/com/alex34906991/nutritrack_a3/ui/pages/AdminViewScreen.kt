package com.alex34906991.nutritrack_a3.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun AdminViewScreen(
    viewModel: NutriTrackViewModel,
    onDone: () -> Unit
) {
    // Add timer state for UI feedback
    val (timeElapsed, setTimeElapsed) = androidx.compose.runtime.remember { mutableStateOf(0) }
    
    // Observe state
    val maleHeifaAverage by viewModel.maleHeifaAverage.collectAsState()
    val femaleHeifaAverage by viewModel.femaleHeifaAverage.collectAsState()
    val dataPatterns by viewModel.dataPatterns.collectAsState()
    val isGeneratingPatterns by viewModel.isGeneratingPatterns.collectAsState()
    
    // Load HEIFA score data when screen is shown, but don't automatically generate patterns
    LaunchedEffect(key1 = Unit) {
        viewModel.calculateAverageHeifaScores()
        // Remove automatic pattern generation from here
    }
    
    // Timer for the loading indicator
    LaunchedEffect(key1 = isGeneratingPatterns) {
        var currentTime = 0
        if (isGeneratingPatterns) {
            while (currentTime < 10 && isGeneratingPatterns) { // Match with timeout in ViewModel
                setTimeElapsed(currentTime)
                kotlinx.coroutines.delay(1000) // Wait 1 second
                currentTime += 1
            }
        } else {
            setTimeElapsed(0)
        }
    }
    
    val scrollState = rememberScrollState()
    
    // Add keyboard and focus controllers
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Clinician Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // HEIFA Score Averages
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "HEIFA Score Averages",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Average HEIFA (Male):",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = String.format("%.1f", maleHeifaAverage),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Average HEIFA (Female):",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = String.format("%.1f", femaleHeifaAverage),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // AI-Powered Data Analysis
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "AI-Powered Data Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Find Data Pattern Button
                Button(
                    onClick = { 
                        // Clear previous state and start fresh analysis
                        if (!isGeneratingPatterns) {
                            viewModel.generateDataPatterns()
                            // Clear focus and hide keyboard to avoid IME callback issues
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGeneratingPatterns
                ) {
                    if (isGeneratingPatterns) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Find Data Pattern")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Key patterns identified in the dataset:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Loading indicator or patterns
                if (isGeneratingPatterns) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Analyzing data patterns... (${10 - timeElapsed}s)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else if (dataPatterns.isEmpty()) {
                    // Display a message when no patterns are available
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No patterns available. Click 'Find Data Pattern' to analyze.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    // Display data patterns
                    dataPatterns.forEachIndexed { index, pattern ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                val patternParts = pattern.split(":", limit = 2)
                                if (patternParts.size == 2) {
                                    Text(
                                        text = patternParts[0] + ":",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Text(
                                        text = patternParts[1].trim(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    Text(
                                        text = pattern,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Done button
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Done")
        }
    }
} 