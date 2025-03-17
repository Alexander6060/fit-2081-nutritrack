package com.alex34906991.nutritrack_a1.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alex34906991.nutritrack_a1.ui.NutriTrackViewModel

@Composable
fun LoginScreen(
    viewModel: NutriTrackViewModel,
    onLoginSuccess: () -> Unit
) {
    var selectedUserId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dropdown for user IDs from CSV
        Text(text = "Select User ID:")
        DropdownMenuBox(
            userList = viewModel.users.map { it.userID },
            selectedUserId = selectedUserId,
            onUserSelected = { selectedUserId = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone number input
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val success = viewModel.login(selectedUserId, phoneNumber)
                if (success) {
                    onLoginSuccess()
                } else {
                    errorMessage = "Invalid User ID or Phone Number"
                }
            }
        ) {
            Text(text = "Continue")
        }
    }
}

@Composable
fun DropdownMenuBox(
    userList: List<String>,
    selectedUserId: String,
    onUserSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selectedUserId,
            onValueChange = { },
            label = { Text("User ID") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Expand")
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            userList.forEach { userId ->
                DropdownMenuItem(
                    text = { Text(userId) },
                    onClick = {
                        onUserSelected(userId)
                        expanded = false
                    }
                )
            }
        }
    }
}
