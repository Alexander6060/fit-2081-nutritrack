package com.alex34906991.nutritrack_a3.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alex34906991.nutritrack_a3.ui.pages.*
import com.alex34906991.nutritrack_a3.ui.NutriTrackViewModel

@Composable
fun AppNavHost(viewModel: NutriTrackViewModel) {
    val navController = rememberNavController()
    // Define routes that should show the bottom navigation bar
    val bottomNavItems = listOf("home", "insights")
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
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
                    onLoginClick = { navController.navigate("login") }
                )
            }
            composable("login") {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { navController.navigate("questionnaire") }
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
                        // Future: navigate to NutriCoach
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
    NavigationBar {
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = { onItemClick("home") },
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedRoute == "insights",
            onClick = { onItemClick("insights") },
            icon = { Icon(Icons.Rounded.Add, contentDescription = "Insight") },
            label = { Text("Insights") }
        )
    }
}
