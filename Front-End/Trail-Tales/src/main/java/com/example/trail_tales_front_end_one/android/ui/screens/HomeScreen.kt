package com.example.trail_tales_front_end_one.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.google.firebase.auth.FirebaseUser
@Composable
fun HomeScreen(user: FirebaseUser, authManager: AuthManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome ${user.email}")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { authManager.signOut() }) {
            Text("Sign Out")
        }
        
        // Add your main app content here
    }
} 