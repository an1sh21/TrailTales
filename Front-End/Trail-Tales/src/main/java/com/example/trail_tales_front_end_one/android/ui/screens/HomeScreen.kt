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
import androidx.compose.material.icons.filled.LocationOn
import android.location.LocationManager as AndroidLocationManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random
import kotlin.math.cos
import com.google.android.gms.maps.model.BitmapDescriptor
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import android.location.Location
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.res.painterResource
import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import androidx.compose.ui.viewinterop.AndroidView
import com.google.maps.android.compose.Marker

// Data class for Points of Interest
data class PointOfInterest(
    val id: String,
    val position: LatLng,
    val title: String,
    val description: String,
    var distance: Float = 0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: FirebaseUser, 
    authManager: AuthManager,
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Default location (Sri Lanka instead of San Francisco)
    var userLocation by remember { mutableStateOf(LatLng(7.8731, 80.7718)) } // Default to Sri Lanka center
    
    // Initialize location manager
    val locationManager = remember { LocationManager(context) }
    val locationState = locationManager.location.collectAsState()
    
    // Check if location services are enabled
    val androidLocationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager }
    var locationServicesEnabled by remember { mutableStateOf(false) }
    
    // Update location services status
    LaunchedEffect(Unit) {
        locationServicesEnabled = androidLocationManager.isProviderEnabled(AndroidLocationManager.GPS_PROVIDER) ||
                                androidLocationManager.isProviderEnabled(AndroidLocationManager.NETWORK_PROVIDER)
        Log.d("LocationDebug", "Location services enabled: $locationServicesEnabled")
    }
    
    // Request location permissions with better feedback
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        Log.d("LocationDebug", "Fine location permission: $fineLocationGranted")
        Log.d("LocationDebug", "Coarse location permission: $coarseLocationGranted")
        
        if (fineLocationGranted || coarseLocationGranted) {
            // Try to get last known location first
            try {
                val lastKnownLocation = if (fineLocationGranted) {
                    androidLocationManager.getLastKnownLocation(AndroidLocationManager.GPS_PROVIDER)
                } else {
                    androidLocationManager.getLastKnownLocation(AndroidLocationManager.NETWORK_PROVIDER)
                }
                
                lastKnownLocation?.let {
                    Log.d("LocationDebug", "Last known location: ${it.latitude}, ${it.longitude}")
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            } catch (e: SecurityException) {
                Log.e("LocationDebug", "Error getting last location: ${e.message}")
            }
            
            // Start continuous updates
            scope.launch {
                locationManager.startLocationUpdates()
            }
        } else {
            Log.d("LocationDebug", "Location permissions denied")
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
    
    // Start location updates
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
    
    // Add a state to track if we should recenter the map
    var shouldRecenterMap by remember { mutableStateOf(true) }
    
    // Update camera position when user location changes, but only if shouldRecenterMap is true
    LaunchedEffect(userLocation) {
        if (shouldRecenterMap) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(userLocation),
                durationMs = 1000
            )
        }
    }
    
    // Update the map properties
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,  // Enable the blue dot for current location
                mapType = MapType.NORMAL,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, com.example.trail_tales_front_end_one.android.R.raw.map_style)
            )
        )
    }
    
    // Add state for dropdown menu
    var showProfileMenu by remember { mutableStateOf(false) }
    
    // Add a pulsating animation for the player marker
    val pulseAnim = remember { Animatable(1f) }
    
    // Add a walking animation for the player marker
    val walkingAnim = remember { Animatable(0f) }
    
    // Add a state for footsteps
    var footsteps by remember { mutableStateOf(listOf<LatLng>()) }
    val footstepFadeAnim = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        // Create an infinite pulsating animation
        pulseAnim.animateTo(
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }
    
    LaunchedEffect(Unit) {
        // Create an infinite walking animation
        walkingAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    
    // Add footsteps when the user moves
    LaunchedEffect(userLocation) {
        // Only add footsteps if we've moved a significant distance
        if (footsteps.isEmpty()) {
            // Add first footstep
            footsteps = listOf(userLocation)
        } else {
            // Check distance to last footstep
            val results = FloatArray(1)
            Location.distanceBetween(
                footsteps.last().latitude, footsteps.last().longitude,
                userLocation.latitude, userLocation.longitude,
                results
            )
            if (results[0] > 5) {
                // Add a new footstep if we've moved more than 5 meters
                footsteps = (footsteps + userLocation).takeLast(10) // Keep only the last 10 footsteps
            }
        }
    }

    // Function to create a custom marker from a vector drawable
    fun vectorToBitmap(drawableId: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    
    // Generate some random POIs around the user's location
    val poiList = remember(userLocation) {
        val list = mutableListOf<PointOfInterest>()
        // Generate 5 random POIs within a 500m radius
        repeat(5) { index ->
            // Convert meters to latitude/longitude degrees (approximate)
            val latOffset = (Random.nextDouble() - 0.5) * 0.009  // ~500m in latitude
            val lngOffset = (Random.nextDouble() - 0.5) * 0.009 / cos(Math.toRadians(userLocation.latitude))  // ~500m in longitude
            
            val poi = PointOfInterest(
                id = "poi_$index",
                position = LatLng(
                    userLocation.latitude + latOffset, 
                    userLocation.longitude + lngOffset
                ),
                title = "Trail Point ${index + 1}",
                description = "Discover this location to earn points!"
            )
            
            // Calculate distance to POI
            val results = FloatArray(1)
            Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                poi.position.latitude, poi.position.longitude,
                results
            )
            poi.distance = results[0]
            
            list.add(poi)
        }
        list
    }
    
    // Game stats (would be fetched from backend in a real app)
    val playerLevel = remember { 5 }
    val playerPoints = remember { 1250 }
    val discoveredLocations = remember { 8 }
    
    // State for proximity notification
    var nearbyPoi by remember { mutableStateOf<PointOfInterest?>(null) }
    var showProximityAlert by remember { mutableStateOf(false) }
    
    // Check for nearby POIs whenever user location changes
    LaunchedEffect(userLocation, poiList) {
        // Find the closest POI within 50 meters
        val closestPoi = poiList.filter { it.distance < 50 }.minByOrNull { it.distance }
        
        if (closestPoi != null && (nearbyPoi == null || nearbyPoi?.id != closestPoi.id)) {
            nearbyPoi = closestPoi
            showProximityAlert = true
            // Auto-hide the alert after 5 seconds
            kotlinx.coroutines.delay(5000)
            showProximityAlert = false
        }
    }
    
    // Main content
    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map with custom style
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,  // We'll add our own button
                mapToolbarEnabled = false
            ),
            onMapLoaded = {
                Log.d("MapDebug", "Map loaded successfully")
            },
            onMapClick = {
                // When user interacts with map, stop auto-centering
                shouldRecenterMap = false
            }
        ) {
            // Player marker with walking animation
            val markerOffset = when ((walkingAnim.value * 4).toInt() % 4) {
                0 -> LatLng(userLocation.latitude - 0.00002, userLocation.longitude)
                1 -> LatLng(userLocation.latitude + 0.00002, userLocation.longitude)
                2 -> LatLng(userLocation.latitude, userLocation.longitude - 0.00002)
                else -> LatLng(userLocation.latitude, userLocation.longitude + 0.00002)
            }
            
            // Calculate the bearing (direction) based on previous and current location
            var bearing by remember { mutableStateOf(0f) }
            var prevLocation by remember { mutableStateOf(userLocation) }
            
            // Update bearing when location changes
            LaunchedEffect(userLocation) {
                if (prevLocation != userLocation) {
                    // Calculate bearing between previous and current location
                    val results = FloatArray(2)
                    Location.distanceBetween(
                        prevLocation.latitude, prevLocation.longitude,
                        userLocation.latitude, userLocation.longitude,
                        results
                    )
                    // Only update bearing if we've moved a significant distance
                    if (results[0] > 5) {
                        bearing = results[1] // The second value is the bearing
                        prevLocation = userLocation
                    }
                }
            }
            
            // Use the player avatar drawable for the marker
            val avatarFrame = when ((walkingAnim.value * 4).toInt() % 4) {
                0 -> com.example.trail_tales_front_end_one.android.R.drawable.player_avatar
                1 -> com.example.trail_tales_front_end_one.android.R.drawable.player_avatar_frame2
                2 -> com.example.trail_tales_front_end_one.android.R.drawable.player_avatar
                else -> com.example.trail_tales_front_end_one.android.R.drawable.player_avatar_frame3
            }
            
            // Custom info window content
            val playerInfoWindow: (@Composable (com.google.maps.android.compose.MarkerState) -> Unit) = { marker ->
                Surface(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Player",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Level: $playerLevel",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Coordinates: ${userLocation.latitude.format(4)}, ${userLocation.longitude.format(4)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            Marker(
                state = MarkerState(position = markerOffset),
                title = "You are here",
                snippet = "Lat: ${userLocation.latitude.format(4)}, Lng: ${userLocation.longitude.format(4)}",
                icon = vectorToBitmap(avatarFrame),
                rotation = bearing,
                zIndex = 1f
            )
            
            // Add POI markers
            poiList.forEach { poi ->
                Marker(
                    state = MarkerState(position = poi.position),
                    title = poi.title,
                    snippet = "${poi.description}\nDistance: ${formatDistance(poi.distance)}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                )
            }

            // Add footstep markers
            footsteps.forEachIndexed { index, position ->
                // Calculate the alpha based on the index (older footsteps are more transparent)
                val alpha = 1f - (index.toFloat() / footsteps.size)
                
                // Only show footsteps that are not too close to the player
                val distanceToPlayer = FloatArray(1)
                Location.distanceBetween(
                    position.latitude, position.longitude,
                    userLocation.latitude, userLocation.longitude,
                    distanceToPlayer
                )
                
                if (distanceToPlayer[0] > 2) {
                    Marker(
                        state = MarkerState(position = position),
                        icon = vectorToBitmap(com.example.trail_tales_front_end_one.android.R.drawable.footstep),
                        alpha = alpha,
                        anchor = Offset(0.5f, 0.5f),
                        zIndex = 0.5f
                    )
                }
            }
        }
        
        // Proximity alert
        AnimatedVisibility(
            visible = showProximityAlert,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .offset(y = 100.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFD700),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trail Point Nearby!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = nearbyPoi?.title ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to discover and earn points!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // Game info panel at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFFFD700),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Level indicator
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "LVL",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "$playerLevel",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Points
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "POINTS",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "$playerPoints",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Discovered locations
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DISCOVERED",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "$discoveredLocations",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        // Custom "My Location" button (bottom-right, above toolbar)
        FloatingActionButton(
            onClick = { 
                // Recenter map on current location
                shouldRecenterMap = true
                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(userLocation, 15f)
                        ),
                        durationMs = 1000
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "Center on my location"
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
                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_quest),
                            contentDescription = "Quests",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(
                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_settings),
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Spacer for the center button
                    Spacer(modifier = Modifier.width(56.dp))
                    
                    // Right side buttons
                    IconButton(onClick = { /* Open Inventory */ }) {
                        Icon(
                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_inventory),
                            contentDescription = "Inventory",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { /* Open Social */ }) {
                        Icon(
                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_social),
                            contentDescription = "Social",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Center floating camera button
            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp),
                shape = CircleShape,
                color = Color(0xFFFFD700), // Gold color
                tonalElevation = 8.dp
            ) {
                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize(),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    IconButton(
                        onClick = { /* Open Camera */ },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_camera),
                            contentDescription = "Camera",
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Format distance in meters to a readable format
private fun formatDistance(meters: Float): String {
    return when {
        meters < 1000 -> "${meters.toInt()}m"
        else -> String.format("%.1fkm", meters / 1000)
    }
}

// Add this extension function at the bottom of your file
private fun Double.format(digits: Int) = "%.${digits}f".format(this)