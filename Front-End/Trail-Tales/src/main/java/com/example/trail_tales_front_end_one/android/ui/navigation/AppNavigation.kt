package com.example.trail_tales_front_end_one.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.example.trail_tales_front_end_one.android.ui.screens.HomeScreen
import com.example.trail_tales_front_end_one.android.ui.screens.LoadingScreen
import com.example.trail_tales_front_end_one.android.ui.screens.QuestsScreen
import com.example.trail_tales_front_end_one.android.ui.screens.SettingsScreen
import com.example.trail_tales_front_end_one.android.ui.screens.ActiveQuest
import com.example.trail_tales_front_end_one.android.ui.screens.Quest
import com.google.android.gms.maps.model.LatLng
import com.example.trail_tales_front_end_one.android.R
import com.google.firebase.auth.FirebaseUser

sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Quests : Screen("quests")
}

@Composable
fun AppNavigation(
    user: FirebaseUser,
    authManager: AuthManager,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Loading.route
) {
    val actions = remember(navController) { NavigationActions(navController) }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Loading.route) {
            LoadingScreen(
                onLoadingComplete = actions.navigateToHome
            )
        }
        
        composable(Screen.Home.route) {
            // Get and consume any pending active quest
            val pendingQuest = actions.consumePendingQuest()
            
            HomeScreen(
                user = user,
                authManager = authManager,
                onNavigateToSettings = actions.navigateToSettings,
                onNavigateToQuests = actions.navigateToQuests,
                initialActiveQuest = pendingQuest
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
        
        composable(Screen.Quests.route) {
            QuestsScreen(
                onBackClick = actions.navigateBack,
                onQuestStart = { quest ->
                    // Convert Quest to ActiveQuest and navigate to Home
                    val airforceBaseLatLng = LatLng(6.8219, 79.8862) // Example coordinates
                    val activeQuest = ActiveQuest(
                        id = quest.id,
                        title = quest.title,
                        position = airforceBaseLatLng,
                        siteMapResourceId = R.drawable.airforce_sitemap // Add your sitemap resource
                    )
                    actions.navigateToHomeWithQuest(activeQuest)
                }
            )
        }
    }
}

class NavigationActions(private val navController: NavHostController) {
    
    // ActiveQuest that will be passed to HomeScreen
    private var pendingActiveQuest: ActiveQuest? = null
    
    val navigateToHome: () -> Unit = {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Loading.route) { inclusive = true }
        }
    }
    
    val navigateToSettings: () -> Unit = {
        navController.navigate(Screen.Settings.route)
    }
    
    val navigateToQuests: () -> Unit = {
        navController.navigate(Screen.Quests.route)
    }
    
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
    
    val navigateToHomeWithQuest: (ActiveQuest) -> Unit = { quest ->
        // Store the quest to be used by HomeScreen
        pendingActiveQuest = quest
        
        // Navigate to home
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }
    
    // Function to get and clear the pending quest
    fun consumePendingQuest(): ActiveQuest? {
        val quest = pendingActiveQuest
        pendingActiveQuest = null
        return quest
    }
} 