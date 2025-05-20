package com.alex34906991.nutritrack_a3.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import coil.compose.rememberAsyncImagePainter
import com.alex34906991.nutritrack_a3.data.database.NutriCoachTipEntity
import com.alex34906991.nutritrack_a3.data.model.Fruit
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Add
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NutriCoachScreen(
    viewModel: NutriTrackViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Fruit search state
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedFruit by viewModel.selectedFruit.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchError by viewModel.searchError.collectAsState()
    
    // Dropdown state
    var isExpanded by remember { mutableStateOf(false) }
    
    // Existing state
    val latestTip by viewModel.latestTip.collectAsState()
    val tips by viewModel.nutriCoachTips.collectAsState()
    val isFruitScoreOptimal by viewModel.isFruitScoreOptimal.collectAsState()
    
    val showTipsDialog = remember { mutableStateOf(false) }
    
    // Keyboard and focus controllers
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    
    // Scroll state for the main screen
    val scrollState = rememberScrollState()
    
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
                .padding(16.dp)
                .verticalScroll(scrollState),
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Find Fruit Nutrition Information",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Search field with autocomplete
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { 
                                    viewModel.updateSearchQuery(it)
                                    isExpanded = it.length >= 2
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { 
                                        isExpanded = it.isFocused && searchQuery.length >= 2 && searchResults.isNotEmpty() 
                                    },
                                placeholder = { Text("Enter fruit name (e.g. banana)") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = "Search"
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = {
                                            viewModel.updateSearchQuery("")
                                            isExpanded = false
                                        }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = "Clear search"
                                            )
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (searchQuery.isNotBlank()) {
                                            coroutineScope.launch {
                                                viewModel.getFruitDetails(searchQuery)
                                            }
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                            isExpanded = false
                                        }
                                    }
                                )
                            )
                            
                            // Autocomplete dropdown
                            DropdownMenu(
                                expanded = isExpanded && searchResults.isNotEmpty(),
                                onDismissRequest = { isExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp),
                                properties = PopupProperties(focusable = false)
                            ) {
                                searchResults.forEach { fruit ->
                                    DropdownMenuItem(
                                        text = { Text(fruit.name) },
                                        onClick = {
                                            viewModel.updateSearchQuery(fruit.name)
                                            viewModel.getFruitDetails(fruit.name)
                                            isExpanded = false
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Loading indicator
                        if (isSearching) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        
                        // Error message
                        if (searchError != null) {
                            Text(
                                text = searchError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        // Display fruit details if available
                        selectedFruit?.let { fruit ->
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = fruit.name,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        
                                        IconButton(
                                            onClick = {
                                                viewModel.addFruitAsFoodIntake(fruit)
                                                // Show a snackbar or some indication
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Add,
                                                contentDescription = "Add to food intake"
                                            )
                                        }
                                    }
                                    
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    
                                    Text(
                                        text = "Family: ${fruit.family}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    
                                    Text(
                                        text = "Genus: ${fruit.genus}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    
                                    Text(
                                        text = "Nutrition Facts (per 100g):",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    
                                    Text(
                                        text = "Calories: ${fruit.nutrition.calories}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    
                                    Text(
                                        text = "Carbohydrates: ${fruit.nutrition.carbohydrates}g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    
                                    Text(
                                        text = "Protein: ${fruit.nutrition.protein}g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    
                                    Text(
                                        text = "Fat: ${fruit.nutrition.fat}g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    
                                    Text(
                                        text = "Sugar: ${fruit.nutrition.sugar}g",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                    
                                    fruit.nutrition.fiber?.let {
                                        Text(
                                            text = "Fiber: ${it}g",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(vertical = 2.dp)
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
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
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
                    if (tips.isEmpty()) {
                        Text("No tips yet. Generate some motivational messages!")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                        ) {
                            items(tips) { tip ->
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