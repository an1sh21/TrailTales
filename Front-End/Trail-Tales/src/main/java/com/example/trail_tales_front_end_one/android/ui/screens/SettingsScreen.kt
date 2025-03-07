package com.example.trail_tales_front_end_one.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: FirebaseUser,
    authManager: AuthManager,
    onNavigateBack: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showLinkGoogleDialog by remember { mutableStateOf(false) }
    var musicVolume by remember { mutableStateOf(0.7f) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    
    // User profile state
    var displayName by remember { mutableStateOf(user.displayName ?: "") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(64.dp),
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // User Name
                    Text(
                        text = displayName.ifEmpty { "Trail Explorer" },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // User Email
                    Text(
                        text = user.email ?: "No email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Edit Profile Button
                    Button(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                        Text("Edit Profile")
                    }
                }
            }
            
            // Account Settings Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Account Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Link with Google (only if not already linked)
                    if (user.providerData.none { it.providerId == "google.com" }) {
                        ListItem(
                            headlineContent = { Text("Link with Google") },
                            supportingContent = { Text("Connect your account with Google") },
                            leadingContent = {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = "Google",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = { showLinkGoogleDialog = true }) {
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = "Link"
                                    )
                                }
                            }
                        )
                        Divider()
                    }
                    
                    // Change Password (only for email users)
                    if (user.providerData.any { it.providerId == "password" }) {
                        ListItem(
                            headlineContent = { Text("Change Password") },
                            supportingContent = { Text("Update your account password") },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = { /* TODO: Implement password change */ }) {
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = "Change"
                                    )
                                }
                            }
                        )
                        Divider()
                    }
                    
                    // Delete Account
                    ListItem(
                        headlineContent = { Text("Delete Account") },
                        supportingContent = { Text("Permanently delete your account and data") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { showDeleteConfirmDialog = true }) {
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    )
                }
            }
            
            // App Settings Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Music Volume
                    ListItem(
                        headlineContent = { Text("Music Volume") },
                        supportingContent = {
                            Slider(
                                value = musicVolume,
                                onValueChange = { musicVolume = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                        },
                        leadingContent = {
                            Icon(
                                if (musicVolume > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Volume",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    
                    Divider()
                    
                    // Notifications
                    var notificationsEnabled by remember { mutableStateOf(true) }
                    ListItem(
                        headlineContent = { Text("Notifications") },
                        supportingContent = { Text("Enable or disable app notifications") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        }
                    )
                    
                    Divider()
                    
                    // Dark Mode
                    var darkModeEnabled by remember { mutableStateOf(false) }
                    ListItem(
                        headlineContent = { Text("Dark Mode") },
                        supportingContent = { Text("Enable or disable dark theme") },
                        leadingContent = {
                            Icon(
                                if (darkModeEnabled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Theme",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = darkModeEnabled,
                                onCheckedChange = { darkModeEnabled = it }
                            )
                        }
                    )
                }
            }
            
            // About Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ListItem(
                        headlineContent = { Text("Version") },
                        supportingContent = { Text("1.0.0") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Version",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    
                    Divider()
                    
                    ListItem(
                        headlineContent = { Text("Terms of Service") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Terms",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { /* TODO: Open Terms of Service */ }) {
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = "Open"
                                )
                            }
                        }
                    )
                    
                    Divider()
                    
                    ListItem(
                        headlineContent = { Text("Privacy Policy") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Privacy",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { /* TODO: Open Privacy Policy */ }) {
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = "Open"
                                )
                            }
                        }
                    )
                }
            }
            
            // Sign Out Button
            Button(
                onClick = { authManager.signOut() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Sign Out",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text("Sign Out")
            }
        }
    }
    
    // Delete Account Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Account") },
            text = { 
                Text(
                    "Are you sure you want to delete your account? This action cannot be undone and all your data will be permanently lost."
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Link Google Account Dialog
    if (showLinkGoogleDialog) {
        AlertDialog(
            onDismissRequest = { showLinkGoogleDialog = false },
            title = { Text("Link with Google") },
            text = { 
                Text(
                    "Do you want to link your account with Google? This will allow you to sign in using your Google account."
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLinkGoogleDialog = false
                        // TODO: Implement Google account linking
                    }
                ) {
                    Text("Link")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLinkGoogleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Edit Profile Dialog
    if (showEditProfileDialog) {
        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showEditProfileDialog = false }) {
                            Text("Cancel")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                // TODO: Update user profile
                                showEditProfileDialog = false
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
} 