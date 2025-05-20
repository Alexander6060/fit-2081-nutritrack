package com.alex34906991.nutritrack_a3.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.alex34906991.nutritrack_a3.ui.AuthStatus
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel

@Composable
fun LoginScreen(
    viewModel: NutriTrackViewModel,
    onLoginSuccess: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedUserId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    // Track authentication state
    val authStatus by viewModel.authStatus.collectAsState()
    
    // Handle auth status changes
    LaunchedEffect(authStatus) {
        when (authStatus) {
            is AuthStatus.Success -> {
                onLoginSuccess()
            }
            is AuthStatus.Error -> {
                errorMessage = (authStatus as AuthStatus.Error).message
            }
            is AuthStatus.NeedRegistration -> {
                val data = (authStatus as AuthStatus.NeedRegistration)
                selectedUserId = data.userId
                phoneNumber = data.phoneNumber
            }
            is AuthStatus.NeedPassword -> {
                val data = (authStatus as AuthStatus.NeedPassword)
                selectedUserId = data.userId
            }
            else -> { /* No action needed */ }
        }
    }
    
    // Clear auth status when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetAuthStatus()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "NutriTrack Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (authStatus is AuthStatus.NeedRegistration) {
            // First-time account creation screen (after ID and phone verification)
            Text(
                text = "Create Your Account",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Full name input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
            
            // Password input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
            
            // Confirm password input
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
            
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter your name"
                    } else if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                    } else if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                    } else {
                        val data = authStatus as AuthStatus.NeedRegistration
                        viewModel.registerUser(data.userId, data.phoneNumber, name, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authStatus !is AuthStatus.Loading
            ) {
                Text(text = "Register")
            }
        } else if (authStatus is AuthStatus.NeedPassword) {
            // Returning user login (password entry after verification)
            Text(
                text = "Enter Password",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // User ID (readonly)
            OutlinedTextField(
                value = selectedUserId,
                onValueChange = { },
                label = { Text("User ID") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
            
            // Password input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
            
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (password.isBlank()) {
                        errorMessage = "Please enter your password"
                    } else {
                        val data = authStatus as AuthStatus.NeedPassword
                        viewModel.login(data.userId, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authStatus !is AuthStatus.Loading
            ) {
                Text(text = "Login")
            }
        } else {
            // Login or First-time user tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("New User") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Existing User") }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (selectedTab) {
                0 -> {
                    // New user flow (ID + phone verification)
                    Text(
                        text = "First-time Login",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "This app is only for pre-registered users. Please enter your ID and phone number to claim your account.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Dropdown for user IDs from database
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
                            if (selectedUserId.isBlank()) {
                                errorMessage = "Please select a User ID"
                            } else if (phoneNumber.isBlank()) {
                                errorMessage = "Please enter your phone number"
                            } else {
                                viewModel.verifyUserCredentials(selectedUserId, phoneNumber)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = authStatus !is AuthStatus.Loading
                    ) {
                        Text(text = "Continue")
                    }
                }
                1 -> {
                    // Direct login for returning users
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Dropdown for user IDs from database
                    DropdownMenuBox(
                        userList = viewModel.users.map { it.userID },
                        selectedUserId = selectedUserId,
                        onUserSelected = { selectedUserId = it }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (selectedUserId.isBlank()) {
                                errorMessage = "Please select a User ID"
                            } else if (password.isBlank()) {
                                errorMessage = "Please enter your password"
                            } else {
                                viewModel.login(selectedUserId, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = authStatus !is AuthStatus.Loading
                    ) {
                        Text(text = "Login")
                    }
                }
            }
        }
        
        // Loading indicator
        if (authStatus is AuthStatus.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
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
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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
