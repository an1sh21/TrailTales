package com.example.trail_tales_front_end_one.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.trail_tales_front_end_one.android.R
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.example.trail_tales_front_end_one.android.auth.UserSession
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(authManager: AuthManager) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Regular expression for email validation
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

    // Regular expression for password validation (at least 6 characters and one special character)
    val passwordRegex = "^(?=.*[!@#\$%^&*(),.?\":{}|<>]).{6,}$".toRegex()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!email.matches(emailRegex)) {
                        errorMessage = "Invalid email format"
                    } else if (!password.matches(passwordRegex)) {
                        errorMessage = "Password must be at least 6 characters and include a special character"
                    } else {
                        scope.launch {
                            isLoading = true
                            val result = authManager.registerWithEmail(email, password)
                            isLoading = false
                            result.onSuccess {
                                authManager.sendEmailVerification(it) { success ->
                                    if (success) {
                                        errorMessage = "Verification email sent. Please verify your email."
                                    } else {
                                        errorMessage = "Failed to send verification email."
                                    }
                                }
                            }.onFailure { 
                                errorMessage = it.message 
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register with Email")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    authManager.signInWithGoogle(
                        onSuccess = { UserSession.updateUser(it) },
                        onError = { errorMessage = it.message }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register with Google")
            }
        }
    }
} 