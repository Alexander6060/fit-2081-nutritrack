package com.alex34906991.nutritrack_a3.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alex34906991.nutritrack_a3.ui.pages.*
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel
import com.alex34906991.nutritrack_a3.data.database.FoodIntakeEntity
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun AppNavHost(viewModel: NutriTrackViewModel) {
    val navController = rememberNavController()
    // Define routes that should show the bottom navigation bar
    val bottomNavItems = listOf("home", "insights", "nutricoach", "settings")
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    
    // Check if user is already logged in
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-navigate to home if logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && (currentRoute == "welcome" || currentRoute == "login")) {
            navController.navigate("home") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            if (currentRoute in bottomNavItems) {
                BottomNavigationBar(
                    selectedRoute = currentRoute.orEmpty(),
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding: PaddingValues ->
        NavHost(
            navController = navController,
            startDestination = "welcome",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("welcome") {
                WelcomeScreen(
                    onLoginClick = { navController.navigate("login") },
                    viewModel = viewModel
                )
            }
            composable("login") {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { 
                        // Check if user has completed questionnaire before
                        coroutineScope.launch {
                            val userId = viewModel.getCurrentUser()?.userID
                            if (userId != null) {
                                val foodIntakes = viewModel.getFoodIntakeRepository().getFoodIntakesByPatient(userId).firstOrNull() ?: emptyList()
                                val hasQuestionnaireData = foodIntakes.any<FoodIntakeEntity> { it.foodName == "Questionnaire Response" }
                                
                                if (hasQuestionnaireData) {
                                    // Skip questionnaire if data exists
                                    navController.navigate("home") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                } else {
                                    // Go to questionnaire if no data exists
                                    navController.navigate("questionnaire") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                )
            }
            composable("questionnaire") {
                QuestionnaireScreen(
                    viewModel = viewModel,
                    onSave = { navController.navigate("home") }
                )
            }
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onEditClick = { navController.navigate("questionnaire") }
                )
            }
            composable("insights") {
                InsightsScreen(
                    viewModel = viewModel,
                    onImproveDietClick = {
                        navController.navigate("nutricoach")
                    }
                )
            }
            composable("nutricoach") {
                NutriCoachScreen(
                    viewModel = viewModel
                )
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onLogout = {
                        navController.navigate("welcome") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToAdminView = {
                        navController.navigate("admin_view")
                    }
                )
            }
            composable("admin_view") {
                AdminViewScreen(
                    viewModel = viewModel,
                    onDone = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = { onItemClick("home") },
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                indicatorColor = MaterialTheme.colorScheme.tertiary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        )
        NavigationBarItem(
            selected = selectedRoute == "insights",
            onClick = { onItemClick("insights") },
            icon = { Icon(imageVector = Icons.Rounded.Insights, contentDescription = "Insights") },
            label = { Text("Insights") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                indicatorColor = MaterialTheme.colorScheme.tertiary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        )
        NavigationBarItem(
            selected = selectedRoute == "nutricoach",
            onClick = { onItemClick("nutricoach") },
            icon = { Icon(Icons.Rounded.Add, contentDescription = "NutriCoach") },
            label = { Text("NutriCoach") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                indicatorColor = MaterialTheme.colorScheme.tertiary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        )
        NavigationBarItem(
            selected = selectedRoute == "settings",
            onClick = { onItemClick("settings") },
            icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                indicatorColor = MaterialTheme.colorScheme.tertiary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        )
    }
}
