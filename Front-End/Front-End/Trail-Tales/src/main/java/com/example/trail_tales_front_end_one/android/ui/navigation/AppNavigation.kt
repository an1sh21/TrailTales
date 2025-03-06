package com.example.trail_tales_front_end_one.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.example.trail_tales_front_end_one.android.ui.screens.HomeScreen
import com.example.trail_tales_front_end_one.android.ui.screens.SettingsScreen
import com.google.firebase.auth.FirebaseUser

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    user: FirebaseUser,
    authManager: AuthManager,
    navController: NavHostController = rememberNavController()
) {
    val actions = remember(navController) { NavigationActions(navController) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                user = user,
                authManager = authManager,
                onNavigateToSettings = actions.navigateToSettings
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                user = user,
                authManager = authManager,
                onNavigateBack = actions.navigateBack,
                onDeleteAccount = {
                    // TODO: Implement account deletion logic
                    authManager.signOut()
                }
            )
        }
    }
}

class NavigationActions(private val navController: NavHostController) {
    val navigateToSettings: () -> Unit = {
        navController.navigate(Screen.Settings.route)
    }
    
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
} 