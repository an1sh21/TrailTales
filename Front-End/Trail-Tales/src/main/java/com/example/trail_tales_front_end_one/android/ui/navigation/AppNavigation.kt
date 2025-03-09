package com.example.trail_tales_front_end_one.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.example.trail_tales_front_end_one.android.auth.UserSession
import com.example.trail_tales_front_end_one.android.ui.screens.HomeScreen
import com.example.trail_tales_front_end_one.android.ui.screens.LandingScreen
import com.example.trail_tales_front_end_one.android.ui.screens.LoginScreen
import com.example.trail_tales_front_end_one.android.ui.screens.RegisterScreen
import com.example.trail_tales_front_end_one.android.ui.screens.SettingsScreen
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState

// Define navigation routes
sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authManager: AuthManager
) {
    // Track authentication state - properly collect the StateFlow as State
    val currentUser by UserSession.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
    ) {
        // Loading/Splash screen
        composable(Screen.Loading.route) {
            LandingScreen(navController)
            
            // Simulate loading and check authentication
            LaunchedEffect(true) {
                delay(3000) // Show loading screen for 3 seconds
                
                // Navigate based on authentication status
                if (authManager.isUserLoggedIn()) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                }
            }
        }
        
        // Login screen
        composable(Screen.Login.route) {
            LoginScreen(authManager = authManager)
            
            // If user logs in, navigate to home
            LaunchedEffect(currentUser) {
                if (currentUser != null) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }
        
        // Register screen
        composable(Screen.Register.route) {
            RegisterScreen(authManager = authManager)
            
            // If user registers and is logged in, navigate to home
            LaunchedEffect(currentUser) {
                if (currentUser != null) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            }
        }
        
        // Home screen
        composable(Screen.Home.route) {
            // Only show HomeScreen if we have a user
            currentUser?.let { user ->
                HomeScreen(
                    user = user,
                    authManager = authManager,
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
                
                // If user logs out, navigate back to login
                LaunchedEffect(currentUser) {
                    if (currentUser == null) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }
            }
        }
        
        // Settings screen
        composable(Screen.Settings.route) {
            currentUser?.let { user ->
                SettingsScreen(
                    user = user,
                    authManager = authManager,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
} 