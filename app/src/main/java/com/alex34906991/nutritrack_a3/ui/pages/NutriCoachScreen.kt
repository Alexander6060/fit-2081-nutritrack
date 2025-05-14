package com.alex34906991.nutritrack_a3.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alex34906991.nutritrack_a3.data.database.NutriCoachTipEntity
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriCoachScreen(
    viewModel: NutriTrackViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    
    var fruitName by remember { mutableStateOf("") }
    var fruitDetails by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    
    val latestTip by viewModel.latestTip.collectAsState()
    val tips by viewModel.nutriCoachTips.collectAsState()
    val isFruitScoreOptimal by viewModel.isFruitScoreOptimal.collectAsState()
    
    val showTipsDialog = remember { mutableStateOf(false) }
    
    // Load the latest tip and check fruit score when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadLatestTip()
        viewModel.checkFruitScore()
        viewModel.loadTipsForCurrentUser()
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NutriCoach",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            // Conditional section based on fruit score
            if (!isFruitScoreOptimal) {
                // Fruit Information Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Fruit Name",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = fruitName,
                            onValueChange = { fruitName = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("banana") }
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    // In a real implementation, this would call the FruityVice API
                                    // For this demo, we're simulating the response
                                    val mockResponse = mapOf(
                                        "family" to "Musaceae",
                                        "calories" to "96",
                                        "fat" to "0.2",
                                        "sugar" to "17.2",
                                        "carbohydrates" to "22",
                                        "protein" to "1"
                                    )
                                    fruitDetails = mockResponse
                                    isLoading = false
                                }
                            },
                            enabled = fruitName.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                            Text("Details")
                        }
                    }
                    
                    // Display fruit details if available
                    if (fruitDetails.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                fruitDetails.forEach { (key, value) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = ": $value",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Display random image for optimal fruit score
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Load a random image from Lorem Picsum
                    Image(
                        painter = rememberAsyncImagePainter("https://picsum.photos/400/200"),
                        contentDescription = "Random image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Motivational Message Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = latestTip ?: "Click the button below for a motivational message!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Button(
                        onClick = {
                            viewModel.generateMotivationalTip()
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Generate AI Message",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Motivational Message (AI)")
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Show All Tips Button
            Button(
                onClick = { 
                    showTipsDialog.value = true 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Shows All Tips")
            }
        }
        
        // Tips Dialog
        if (showTipsDialog.value) {
            AlertDialog(
                onDismissRequest = { showTipsDialog.value = false },
                title = { Text("AI Tips") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        if (tips.isEmpty()) {
                            Text("No tips yet. Generate some motivational messages!")
                        } else {
                            tips.forEach { tip ->
                                TipItem(tip)
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showTipsDialog.value = false }
                    ) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
fun TipItem(tip: NutriCoachTipEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(tip.timestamp))
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = tip.message,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 