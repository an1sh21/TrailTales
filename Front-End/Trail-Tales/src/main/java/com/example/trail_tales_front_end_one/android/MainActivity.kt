package com.example.trail_tales_front_end_one.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.example.trail_tales_front_end_one.android.auth.UserSession
import com.example.trail_tales_front_end_one.android.ui.screens.HomeScreen
import com.example.trail_tales_front_end_one.android.ui.screens.LoginScreen
import com.example.trail_tales_front_end_one.android.ui.screens.RegisterScreen
import com.example.trail_tales_front_end_one.android.ui.theme.AppTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = AuthManager(this)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showRegisterScreen by remember { mutableStateOf(false) }
                    val currentUser by UserSession.currentUser.collectAsState()
                    if (currentUser == null) {
                        if (showRegisterScreen) {
                            RegisterScreen(authManager)
                        } else {
                            LoginScreen(authManager)
                        }

                        // Show toggle button only when not logged in
                        Box(modifier = Modifier.fillMaxSize()) {
                            Button(
                                onClick = { showRegisterScreen = !showRegisterScreen },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp, pressedElevation = 12.dp)
                            ) {
                                Text(if (showRegisterScreen) "Go to Login" else "Go to Register", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    } else {
                        HomeScreen(currentUser!!, authManager)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AuthManager.RC_SIGN_IN) {
            authManager.handleSignInResult(data)
        }
    }
}

