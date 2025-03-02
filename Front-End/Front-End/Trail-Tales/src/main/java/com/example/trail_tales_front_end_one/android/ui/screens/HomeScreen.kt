package com.example.trail_tales_front_end_one.android.ui.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.trail_tales_front_end_one.android.auth.AuthManager
import com.example.trail_tales_front_end_one.android.location.LocationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

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
    
    // Update user location when location changes
    LaunchedEffect(locationState.value) {
        locationState.value?.let {
            userLocation = LatLng(it.latitude, it.longitude)
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
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true
            )
        ) {
            // Add a marker at the user's location
            Marker(
                state = MarkerState(position = userLocation),
                title = "You are here"
            )
        }
        
        // Top Bar with User Profile
        TopAppBar(
            title = { Text("Trail Tales") },
            actions = {
                IconButton(onClick = { /* Show user profile */ }) {
                    Icon(Icons.Default.Person, "Profile")
                }
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )
        
        // Bottom Navigation Bar
        BottomAppBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { /* Open Quests */ }) {
                    Icon(Icons.Default.List, "Quests")
                }
                IconButton(onClick = { /* Open Settings */ }) {
                    Icon(Icons.Default.Settings, "Settings")
                }
                FloatingActionButton(
                    onClick = { /* Open Camera */ },
                    modifier = Modifier.offset(y = (-20).dp)
                ) {
                    Icon(Icons.Default.Info, "Camera")
                }
                IconButton(onClick = { /* Open Inventory */ }) {
                    Icon(Icons.Default.Face, "Inventory")
                }
                IconButton(onClick = { /* Open Social */ }) {
                    Icon(Icons.Default.AccountCircle, "Social")
                }
            }
        }
    }
} 