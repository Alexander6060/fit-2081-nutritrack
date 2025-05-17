package com.alex34906991.nutritrack_a3.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel

@Composable
fun SettingsScreen(
    viewModel: NutriTrackViewModel,
    onLogout: () -> Unit,
    onNavigateToAdminView: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClinicianLoginDialog by remember { mutableStateOf(false) }
    var clinicianKey by remember { mutableStateOf("") }
    var showInvalidKeyError by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // User information
        Card(

            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Account Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "User ID: ${currentUser?.userID ?: "Unknown"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Text(
                    text = "Name: ${currentUser?.name ?: "Not set"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Text(
                    text = "Phone: ${currentUser?.phoneNumber ?: "Not set"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        // Admin View button
        Button(
            onClick = { showClinicianLoginDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Admin View")
        }
        
        // Spacer
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout button
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Log Out")
        }
    }
    
    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Clinician Login Dialog
    if (showClinicianLoginDialog) {
        AlertDialog(
            onDismissRequest = { 
                showClinicianLoginDialog = false 
                clinicianKey = ""
                showInvalidKeyError = false
            },
            title = { Text("Clinician Login") },
            text = { 
                Column {
                    Text(
                        "This screen is accessible via the Settings menu. To enter the clinician section, a valid predefined access key must be provided for authentication.",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = clinicianKey,
                        onValueChange = { 
                            clinicianKey = it
                            showInvalidKeyError = false
                        },
                        label = { Text("Clinician Key") },
                        placeholder = { Text("Enter your clinician key") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = showInvalidKeyError,
                        singleLine = true
                    )
                    
                    if (showInvalidKeyError) {
                        Text(
                            "Invalid clinician key",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (clinicianKey == "dollar-entry-apples") {
                            showClinicianLoginDialog = false
                            clinicianKey = ""
                            onNavigateToAdminView()
                        } else {
                            showInvalidKeyError = true
                        }
                    }
                ) {
                    Text("Login")
                }
            },
            dismissButton = {
                Button(
                    onClick = { 
                        showClinicianLoginDialog = false 
                        clinicianKey = ""
                        showInvalidKeyError = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
} 