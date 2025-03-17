package com.alex34906991.nutritrack_a1.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alex34906991.nutritrack_a1.ui.NutriTrackViewModel

@Composable
fun QuestionnaireScreen(
    viewModel: NutriTrackViewModel,
    onSave: () -> Unit
) {
    // Food categories with their checked state
    val categories = listOf("Fruits", "Vegetables", "Grains", "Meat", "Dairy", "Sugary Drinks")
    val checkedState = remember { mutableStateMapOf<String, Boolean>() }
    categories.forEach { category ->
        if (!checkedState.containsKey(category)) {
            checkedState[category] = false
        }
    }

    // Persona selection options
    val personas = listOf("Athlete", "Busy Professional", "Student", "Vegetarian", "Vegan", "Pescatarian")
    var selectedPersona by remember { mutableStateOf("") }

    // Time fields (simplified to text input)
    var biggestMealTime by remember { mutableStateOf("") }
    var sleepTime by remember { mutableStateOf("") }
    var wakeTime by remember { mutableStateOf("") }

    // Create a scroll state for the vertical scrollable column
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select your food categories:")

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
                    selected = (selectedPersona == persona),
                    onClick = { selectedPersona = persona }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = persona)
            }
        }

        // Time input fields using custom composable
        OutlinedTimeField(
            label = "Biggest Meal Time",
            timeValue = biggestMealTime,
            onTimeChange = { biggestMealTime = it }
        )
        OutlinedTimeField(
            label = "Sleep Time",
            timeValue = sleepTime,
            onTimeChange = { sleepTime = it }
        )
        OutlinedTimeField(
            label = "Wake Time",
            timeValue = wakeTime,
            onTimeChange = { wakeTime = it }
        )

        // Centered Save button
        Button(
            onClick = {
                viewModel.saveQuestionnaireData(selectedPersona, biggestMealTime, sleepTime, wakeTime)
                onSave()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save & Continue")
        }
    }
}

@Composable
fun OutlinedTimeField(label: String, timeValue: String, onTimeChange: (String) -> Unit) {
    OutlinedTextField(
        value = timeValue,
        onValueChange = { onTimeChange(it) },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}
