package com.alex34906991.nutritrack_a3.ui.pages

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.firstOrNull

// Define a Persona data class to hold the details and image resource.
data class Persona(
    val name: String,
    val description: String,
    val fileName: String
)

@Composable
fun QuestionnaireScreen(
    viewModel: NutriTrackViewModel,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    val categories = listOf(
        "Vegetables",
        "Fruits",
        "Grains",
        "Meat & Protein",
        "Dairy",
        "Sugary Drinks",
        "Alcohol"
    )
    val checkedState = remember { mutableStateMapOf<String, Boolean>() }
    // Initialize checkedState for each category
    categories.forEach { category -> checkedState.putIfAbsent(category, false) }

    // Updated list of personas with details and image resources.
    val personas = listOf(
        Persona(
            name = "Health Devotee",
            description = "I'm passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
            fileName = "health_devotee.jpg"
        ),
        Persona(
            name = "Mindful Eater",
            description = "I'm health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
            fileName = "mindful_eater.jpg"
        ),
        Persona(
            name = "Wellness Striver",
            description = "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I've tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I'll give it a go.",
            fileName = "wellness_striver.jpg"
        ),
        Persona(
            name = "Balance Seeker",
            description = "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn't have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
            fileName = "balance_.jpg"
        ),
        Persona(
            name = "Health Procrastinator",
            description = "I'm contemplating healthy eating but it's not a priority for me right now. I know the basics about what it means to be healthy, but it doesn't seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.",
            fileName = "health_procrastinator.jpg"
        ),
        Persona(
            name = "Food Carefree",
            description = "I'm not bothered about healthy eating. I don't really see the point and I don't think about it. I don't really notice healthy eating tips or recipes and I don't care what I eat.",
            fileName = "food_carefree.jpg"
        )
    )

    // State to hold the currently selected persona for the questionnaire.
    var selectedPersona by remember { mutableStateOf<Persona?>(null) }
    // State to control which persona details dialog is showing.
    var showPersonaDialog by remember { mutableStateOf<Persona?>(null) }

    // Time fields using LocalTime
    var biggestMealTime by remember { mutableStateOf(LocalTime.of(12, 0)) }
    var sleepTime by remember { mutableStateOf(LocalTime.of(22, 0)) }
    var wakeTime by remember { mutableStateOf(LocalTime.of(6, 30)) }

    // Validation states
    var showValidationError by remember { mutableStateOf(false) }
    var validationErrorMessage by remember { mutableStateOf("") }
    
    // Function to load saved questionnaire data
    suspend fun loadQuestionnaireData() {
        // Get food intakes for current user
        val userId = currentUser?.userID ?: return
        val foodIntakes = viewModel.getFoodIntakeRepository().getFoodIntakesByPatient(userId).firstOrNull() ?: return
        
        // Find the latest questionnaire response
        val questionnaireResponse = foodIntakes.find { it.foodName == "Questionnaire Response" }
        
        if (questionnaireResponse != null) {
            // Parse the category field to extract data
            val categoryData = questionnaireResponse.category ?: return
            
            // Extract persona
            val personaPattern = "Persona: ([^,]+)".toRegex()
            val personaMatch = personaPattern.find(categoryData)
            val personaName = personaMatch?.groupValues?.get(1)
            
            personaName?.let { name ->
                personas.find { it.name == name }?.let {
                    selectedPersona = it
                }
            }
            
            // Extract times
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
            
            val biggestMealPattern = "Biggest Meal: ([^,]+)".toRegex()
            val biggestMealMatch = biggestMealPattern.find(categoryData)
            biggestMealMatch?.groupValues?.get(1)?.let {
                try {
                    biggestMealTime = LocalTime.parse(it, timeFormatter)
                } catch (e: Exception) {
                    // Handle parsing error
                }
            }
            
            val sleepPattern = "Sleep: ([^,]+)".toRegex()
            val sleepMatch = sleepPattern.find(categoryData)
            sleepMatch?.groupValues?.get(1)?.let {
                try {
                    sleepTime = LocalTime.parse(it, timeFormatter)
                } catch (e: Exception) {
                    // Handle parsing error
                }
            }
            
            val wakePattern = "Wake: ([^,]+)".toRegex()
            val wakeMatch = wakePattern.find(categoryData)
            wakeMatch?.groupValues?.get(1)?.let {
                try {
                    wakeTime = LocalTime.parse(it, timeFormatter)
                } catch (e: Exception) {
                    // Handle parsing error
                }
            }
            
            // Extract selected categories if they exist
            val categoriesPattern = "Categories: \\[([^\\]]+)\\]".toRegex()
            val categoriesMatch = categoriesPattern.find(categoryData)
            categoriesMatch?.groupValues?.get(1)?.let { categoriesString ->
                val selectedCategories = categoriesString.split(", ")
                categories.forEach { category ->
                    checkedState[category] = selectedCategories.contains(category)
                }
            }
        }
    }

    // Load questionnaire data when screen is first displayed or when user changes
    LaunchedEffect(currentUser?.userID) {
        if (currentUser != null) {
            loadQuestionnaireData()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "The food categories you can eat:")

        categories.forEach { category ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkedState[category] == true,
                    onCheckedChange = { checked ->
                        checkedState[category] = checked
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = category)
            }
        }

        Text(text = "Select your persona:")

        personas.forEach { persona ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedPersona?.name == persona.name),
                    onClick = { selectedPersona = persona }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = persona.name)
                Spacer(modifier = Modifier.width(8.dp))
                // Info button to show the persona details in a pop-up dialog.
                IconButton(onClick = { showPersonaDialog = persona }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "View Persona Details"
                    )
                }
            }
        }

        // Time input fields
        OutlinedTimePickerField(
            label = "Biggest Meal Time",
            time = biggestMealTime,
            onTimeSelected = { biggestMealTime = it }
        )
        OutlinedTimePickerField(
            label = "Sleep Time",
            time = sleepTime,
            onTimeSelected = { sleepTime = it }
        )
        OutlinedTimePickerField(
            label = "Wake Time",
            time = wakeTime,
            onTimeSelected = { wakeTime = it }
        )

        // Save button
        Button(
            onClick = {
                // Validation: At least one category must be checked and a persona must be selected
                val isAnyCategorySelected = checkedState.values.any { it }
                if (!isAnyCategorySelected) {
                    validationErrorMessage = "Please select at least one food category."
                    showValidationError = true
                } else if (selectedPersona == null) {
                    validationErrorMessage = "Please select a persona."
                    showValidationError = true
                } else {
                    // Get selected categories
                    val chosenCategories = checkedState.filterValues { it }.keys.toList()
                    
                    // Format times
                    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                    val biggestMealFormatted = biggestMealTime.format(formatter)
                    val sleepTimeFormatted = sleepTime.format(formatter)
                    val wakeTimeFormatted = wakeTime.format(formatter)
                    
                    // Store in Room via ViewModel
                    viewModel.saveQuestionnaireData(
                        selectedPersona?.name.orEmpty(),
                        biggestMealFormatted,
                        sleepTimeFormatted,
                        wakeTimeFormatted,
                        chosenCategories
                    )

                    onSave()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save & Continue")
        }
    }

    // Display the dialog if a persona's info button has been clicked.
    showPersonaDialog?.let { persona ->
        AlertDialog(
            onDismissRequest = { showPersonaDialog = null },
            confirmButton = {
                Button(onClick = { showPersonaDialog = null }) {
                    Text("Close")
                }
            },
            title = { Text(text = persona.name) },
            text = {
                Column {
                    Image(
                        painter = rememberAsyncImagePainter("file:///android_asset/${persona.fileName}"),
                        contentDescription = persona.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = persona.description)
                }
            }
        )
    }

    // Display a validation error dialog if needed.
    if (showValidationError) {
        AlertDialog(
            onDismissRequest = { showValidationError = false },
            title = { Text(text = "Validation Error") },
            text = { Text(text = validationErrorMessage) },
            confirmButton = {
                Button(onClick = { showValidationError = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun OutlinedTimePickerField(label: String, time: LocalTime, onTimeSelected: (LocalTime) -> Unit) {
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    OutlinedTextField(
        value = time.format(timeFormatter),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        trailingIcon = {
            IconButton(onClick = {
                val timePicker = TimePickerDialog(
                    context,
                    { _, hour, minute -> onTimeSelected(LocalTime.of(hour, minute)) },
                    time.hour,
                    time.minute,
                    true
                )
                timePicker.show()
            }) {
                Icon(Icons.Default.Search, contentDescription = "Select Time")
            }
        }
    )
}
