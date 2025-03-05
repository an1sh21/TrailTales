package com.example.trail_tales_front_end_one.android.ui.screens

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.trail_tales_front_end_one.R
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.example.trail_tales_front_end_one.android.location.LocationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseUser
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Divider
import androidx.compose.ui.unit.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(user: FirebaseUser, authManager: AuthManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Default location (will be updated when user location is available)
    var userLocation by remember { mutableStateOf(LatLng(37.7749, -122.4194)) } // Default to San Francisco
    
    // Initialize location manager
    val locationManager = remember { LocationManager(context) }
    val locationState = locationManager.location.collectAsState()
    
    // Request location permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            scope.launch {
                locationManager.startLocationUpdates()
            }
        }
    }
    
    // Request permissions when the screen is first displayed
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    // Add this after the LaunchedEffect that requests permissions
    LaunchedEffect(Unit) {
        Log.d("LocationDebug", "Starting location updates")
        locationManager.startLocationUpdates()
    }
    
    // Modify your location update effect to log the location changes
    LaunchedEffect(locationState.value) {
        if (locationState.value != null) {
            val location = locationState.value!!
            Log.d("LocationDebug", "Location updated: ${location.latitude}, ${location.longitude}")
            userLocation = LatLng(location.latitude, location.longitude)
        } else {
            Log.d("LocationDebug", "Location is null")
        }
    }
    
    // Camera position state that follows the user location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }
    
    // Update camera position when user location changes
    LaunchedEffect(userLocation) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLng(userLocation),
            durationMs = 1000
        )
    }
    
    // Add map style
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = locationState.value != null,
                mapType = MapType.NORMAL,
                mapStyleOptions = null
            )
        )
    }
    
    // Add state for dropdown menu
    var showProfileMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        Log.d("MapDebug", "Attempting to load map with location: $userLocation")
    }
    
    // Main content
    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map with custom style
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false, // Hide default controls for cleaner UI
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false
            ),
            onMapLoaded = {
                Log.d("MapDebug", "Map loaded successfully")
            }
        ) {
            // Add a marker at the user's location
            Marker(
                state = MarkerState(position = userLocation),
                title = "You are here",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
        
        // User profile button (top-right corner)
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        ) {
            // Gold circle border
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = Color(0xFFFFD700) // Gold color
            ) {
                // Profile button
                IconButton(
                    onClick = { showProfileMenu = true },
                    modifier = Modifier
                        .padding(3.dp) // Border thickness
                        .fillMaxSize()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Profile dropdown menu
            DropdownMenu(
                expanded = showProfileMenu,
                onDismissRequest = { showProfileMenu = false },
                modifier = Modifier.width(200.dp)
            ) {
                // User info item
                DropdownMenuItem(
                    text = { Text(user.email ?: "User") },
                    onClick = { },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
                Divider()
                // Logout item
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        authManager.signOut()
                        showProfileMenu = false
                    },
                    leadingIcon = {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                    }
                )
            }
        }
        
        // Bottom Navigation Bar (game-like)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            // Bottom bar background
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(70.dp),
                shape = RoundedCornerShape(35.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side buttons
                    IconButton(onClick = { /* Open Quests */ }) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Quests",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { /* Open Settings */ }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Spacer for the center button
                    Spacer(modifier = Modifier.width(56.dp))
                    
                    // Right side buttons
                    IconButton(onClick = { /* Open Inventory */ }) {
                        Icon(
                            Icons.Default.Face,
                            contentDescription = "Inventory",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { /* Open Social */ }) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Social",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Center floating camera button
            FloatingActionButton(
                onClick = { /* Open Camera */ },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Favorite, // Use PhotoCamera instead of Info
                    contentDescription = "Camera",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}