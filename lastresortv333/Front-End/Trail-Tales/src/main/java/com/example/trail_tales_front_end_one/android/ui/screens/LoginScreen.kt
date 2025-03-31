package com.example.trail_tales_front_end_one.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trail_tales_front_end_one.android.R
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.example.trail_tales_front_end_one.android.auth.UserSession
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authManager: AuthManager) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    // Define a light grey color for text field backgrounds
    val lightGreyColor = Color(0xFFF5F5F5)

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(lightGreyColor)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = lightGreyColor,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(lightGreyColor)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = lightGreyColor,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = { showForgotPasswordDialog = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val result = authManager.signInWithEmail(email, password)
                        isLoading = false
                        result.onFailure { 
                            errorMessage = it.message 
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sign In")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    authManager.signInWithGoogle(
                        onSuccess = { UserSession.updateUser(it) },
                        onError = { errorMessage = it.message }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sign in with Google")
            }
        }
        
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Reset Password") },
                text = {
                    Column {
                        Text("Enter your email address to receive a password reset link.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(lightGreyColor)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            TextField(
                                value = forgotPasswordEmail,
                                onValueChange = { forgotPasswordEmail = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = lightGreyColor,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Implement password reset functionality
                            authManager.sendPasswordResetEmail(forgotPasswordEmail) { success ->
                                if (success) {
                                    errorMessage = "Password reset email sent."
                                } else {
                                    errorMessage = "Failed to send password reset email."
                                }
                                showForgotPasswordDialog = false
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Send Reset Link")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showForgotPasswordDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}