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
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random
import kotlin.math.cos
import com.google.android.gms.maps.model.BitmapDescriptor
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import android.location.Location
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.maps.android.compose.Marker
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.window.Dialog
import com.google.maps.android.compose.Polyline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.blur

// Data class for Points of Interest
data class PointOfInterest(
    val id: String,
    val position: LatLng,
    val title: String,
    val description: String,
    var distance: Float = 0f
)

// Create a data class for the active quest
data class ActiveQuest(
    val id: String,
    val title: String,
    val position: LatLng,
    val siteMapResourceId: Int
)

// Constants for locations
private val AIR_FORCE_BASE_LOCATION = LatLng(6.824377931569581, 79.892272582069)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: FirebaseUser, 
    authManager: AuthManager,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToQuests: () -> Unit = {},
    onNavigateToAR: () -> Unit = {},
    initialActiveQuest: ActiveQuest? = null,
    collectedItems: Set<String> = emptySet(),
    totalPoints: Int = 0
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Use the passed collectedItems and totalPoints
    val collectedItemsState = remember { mutableSetOf<String>().apply { addAll(collectedItems) } }
    val totalPointsState = remember { mutableStateOf(totalPoints) }
    
    // Function to handle item collection
    val onItemCollected = { itemName: String, points: Int ->
        if (!collectedItemsState.contains(itemName)) {
            collectedItemsState.add(itemName)
            totalPointsState.value += points
            Log.d("HomeScreen", "Item collected: $itemName, Points: $points, Total: ${totalPointsState.value}, Collected Items: $collectedItemsState")
        } else {
            Log.d("HomeScreen", "Item already collected: $itemName")
        }
    }
    
    // State for user location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    
    // Create a temporary file for storing the camera image
    val tempImageFile = remember { createImageFile(context) }
    val imageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempImageFile
        )
    }
    
    // Camera launcher - moved before cameraPermissionLauncher
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Image captured successfully
            Log.d("CameraDebug", "Image captured: $imageUri")
            // TODO: Process the captured image (e.g., show it, upload it, etc.)
        } else {
            Log.d("CameraDebug", "Image capture failed")
        }
    }
    
    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Navigate to AR Screen instead of launching the camera directly
            onNavigateToAR()
        } else {
            // Handle permission denied
            Log.d("CameraDebug", "Camera permission denied")
            Toast.makeText(
                context,
                "Camera permission is required for AR features",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
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
    
    // CameraPositionState to control the map camera
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            // Center between user location and Air Force base for a better view of the route
            LatLng(
                (userLocation?.latitude ?: 0.0) + AIR_FORCE_BASE_LOCATION.latitude / 2,
                (userLocation?.longitude ?: 0.0) + AIR_FORCE_BASE_LOCATION.longitude / 2
            ),
            11f // Zoom out a bit to see both points
        )
    }
    
    // Add a state to track if we should recenter the map
    var shouldRecenterMap by remember { mutableStateOf(true) }
    
    // Update camera position when user location changes, but only if shouldRecenterMap is true
    LaunchedEffect(userLocation) {
        if (shouldRecenterMap && userLocation != null) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(userLocation!!),
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
    
    // Calculate bearing for the player marker direction
    var bearing by remember { mutableStateOf(0f) }
    var prevLocation by remember { mutableStateOf<LatLng?>(null) }
    
    // Update bearing when location changes
    LaunchedEffect(userLocation) {
        if (prevLocation != userLocation) {
            // Calculate bearing between previous and current location
            val results = FloatArray(2)
            Location.distanceBetween(
                prevLocation?.latitude ?: 0.0, prevLocation?.longitude ?: 0.0,
                userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0,
                results
            )
            // Only update bearing if we've moved a significant distance
            if (results[0] > 5) {
                bearing = results[1] // The second value is the bearing
                prevLocation = userLocation
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
            val lngOffset = (Random.nextDouble() - 0.5) * 0.009 / cos(Math.toRadians(userLocation?.latitude ?: 0.0))  // ~500m in longitude
            
            val poi = PointOfInterest(
                id = "poi_$index",
                position = LatLng(
                    (userLocation?.latitude ?: 0.0) + latOffset, 
                    (userLocation?.longitude ?: 0.0) + lngOffset
                ),
                title = "Trail Point ${index + 1}",
                description = "Discover this location to earn points!"
            )
            
            // Calculate distance to POI
            val distanceResults = FloatArray(1)
            Location.distanceBetween(
                userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0,
                poi.position.latitude, poi.position.longitude,
                distanceResults
            )
            poi.distance = distanceResults[0]
            
            list.add(poi)
        }
        list
    }
    
    // Game stats (would be fetched from backend in a real app)
    val playerLevel = remember { 5 }
    val discoveredLocations = remember { collectedItemsState.size }
    
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
    
    // State for content selection (Map, Quests, Collectables)
    var selectedContent by remember { mutableStateOf("Map") }
    
    // Add active quest state - initialize with provided quest if any, otherwise null (no quest active by default)
    var activeQuest: ActiveQuest? by remember { mutableStateOf(initialActiveQuest) }
    
    // Initialize route points if we have an active quest
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    
    // State for sitemap dialog
    var showSiteMap by remember { mutableStateOf(false) }
    
    // Generate route when active quest changes
    LaunchedEffect(activeQuest, userLocation) {
        activeQuest?.let { quest ->
            // For demo, generate a simple route
            routePoints = generateSimpleRoute(userLocation ?: LatLng(0.0, 0.0), quest.position)
        } ?: run {
            routePoints = emptyList()
        }
    }
    
    // Check if user is near quest destination
    LaunchedEffect(userLocation, activeQuest) {
        activeQuest?.let { quest ->
            val distanceResults = FloatArray(1)
            Location.distanceBetween(
                userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0,
                quest.position.latitude, quest.position.longitude,
                distanceResults
            )
            
            // If user is within 50 meters of destination, show sitemap
            if (distanceResults[0] < 50 && !showSiteMap) {
                showSiteMap = true
            }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Content area (takes most of the screen)
        Box(modifier = Modifier.weight(1f)) {
            when (selectedContent) {
                "Map" -> {
                    // Main content - Map
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
                            // Player marker
                            Marker(
                                state = MarkerState(position = userLocation ?: LatLng(0.0, 0.0)),
                                title = "You are here",
                                snippet = "Lat: ${userLocation?.latitude?.format(4)}, Lng: ${userLocation?.longitude?.format(4)}",
                                icon = vectorToBitmap(com.example.trail_tales_front_end_one.android.R.drawable.player_down_2),
                                rotation = bearing,
                                zIndex = 1f
                            )
                            
                            // Add POI markers
                            poiList.forEach { poi ->
                                Marker(
                                    state = MarkerState(position = poi.position),
                                    title = poi.title,
                                    snippet = "${poi.description}\nDistance: ${formatDistance(poi.distance)}",
                                    icon = vectorToBitmap(com.example.trail_tales_front_end_one.android.R.drawable.poi_marker)
                                )
                            }
                            
                            // Draw route to active quest if available
                            if (routePoints.isNotEmpty()) {
                                Polyline(
                                    points = routePoints,
                                    color = Color(0xFF2196F3), // Blue color
                                    width = 8f
                                )
                                
                                // Add destination marker for quest
                                activeQuest?.let { questForMarker ->
                                    Marker(
                                        state = MarkerState(position = questForMarker.position),
                                        title = questForMarker.title,
                                        snippet = "Quest destination",
                                        icon = if (questForMarker.position == AIR_FORCE_BASE_LOCATION) {
                                            // Use a special marker for the Air Force Base
                                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                        } else {
                                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                        },
                                        zIndex = 2f
                                    )
                                }
                            }
                        }
                        
                        // Proximity alert
                        if (showProximityAlert) {
                            Box(
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
                                            text = "${totalPointsState.value}",
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
                        
                        // Redesigned "My Location" button (bottom-right, above toolbar)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 100.dp, end = 16.dp)
                        ) {
                            // Gold circle border
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = CircleShape,
                                color = Color(0xFFFFD700) // Gold color
                            ) {
                                // Location button
                                IconButton(
                                    onClick = { 
                                        // Recenter map on current location
                                        shouldRecenterMap = true
                                        scope.launch {
                                            cameraPositionState.animate(
                                                update = CameraUpdateFactory.newCameraPosition(
                                                    CameraPosition.fromLatLngZoom(userLocation ?: LatLng(0.0, 0.0), 15f)
                                                ),
                                                durationMs = 1000
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(3.dp) // Border thickness
                                        .fillMaxSize()
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Icon(
                                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.player_marker),
                                            contentDescription = "Center on my location",
                                            modifier = Modifier.padding(8.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
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
                                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_social),
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
                                        Icon(
                                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_social),
                                            contentDescription = null
                                        )
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
                                        Icon(
                                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_settings),
                                            contentDescription = null
                                        )
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
                                    IconButton(onClick = { selectedContent = "Quests" }) {
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
                                    IconButton(onClick = { selectedContent = "Collectables" }) {
                                        Icon(
                                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_inventory),
                                            contentDescription = "Collectables",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { selectedContent = "Socials" }) {
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
                                        onClick = { 
                                            // Request camera permission and navigate to AR screen
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            painter = painterResource(id = com.example.trail_tales_front_end_one.android.R.drawable.ic_camera),
                                            contentDescription = "AR Camera",
                                            modifier = Modifier.size(28.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Add quest controls when a quest is active
                        if (activeQuest != null) {
                            // Show quest information panel
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .align(Alignment.TopCenter)
                                    .offset(y = 70.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    tonalElevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .border(
                                            width = 2.dp,
                                            color = Color(0xFFFFD700),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Active Quest",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.Gray
                                        )
                                        activeQuest?.let { safeQuest ->
                                            Text(
                                                text = safeQuest.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            
                                            // Display description for Air Force base
                                            if (safeQuest.position == AIR_FORCE_BASE_LOCATION) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "RVFV+FJC, Ratmalana, Dehiwala-Mount Lavinia",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray
                                                )
                                            }
                                            
                                            // Calculate and show distance
                                            val distanceResults = FloatArray(1)
                                            Location.distanceBetween(
                                                userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0,
                                                safeQuest.position.latitude, safeQuest.position.longitude,
                                                distanceResults
                                            )
                                            
                                            Text(
                                                text = "Distance: ${formatDistance(distanceResults[0])}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Show end quest button and sitemap button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 100.dp)
                                    .fillMaxWidth(0.9f)
                            ) {
                                val questLocalCopy = activeQuest // Local copy for smart cast to avoid impossible smart cast error
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = { 
                                            // End the quest
                                            activeQuest = null
                                            routePoints = emptyList()
                                            Toast.makeText(context, "Quest ended", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Red
                                        )
                                    ) {
                                        Text("End Quest")
                                    }
                                    
                                    // Calculate distance to destination
                                    questLocalCopy?.let { quest ->
                                        val distanceResults = FloatArray(1)
                                        Location.distanceBetween(
                                            userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0,
                                            quest.position.latitude, quest.position.longitude,
                                            distanceResults
                                        )
                                        
                                        // Show view sitemap button if nearby
                                        if (distanceResults[0] < 100) {
                                            Button(
                                                onClick = { showSiteMap = true },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFFFFD700)
                                                )
                                            ) {
                                                Text("View Site Map", color = Color.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "Quests" -> {
                    // Display a list of quests inline instead of navigating to a separate screen
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background with some styling
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            // Show a simple message with a button to navigate to the full Quests screen
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Available Quests",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Button(
                                    onClick = { onNavigateToQuests() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFD700) // Gold color
                                    )
                                ) {
                                    Text("See All Quests", color = Color.Black)
                                }
                            }
                        }
                    }
                }
                "Collectables" -> CollectablesScreen(
                    onBackClick = { selectedContent = "Map" },
                    collectedItems = collectedItemsState,
                    totalPoints = totalPointsState.value
                )
                "Socials" -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Render the actual Socials screen in the background with blur
                        SocialsScreen(onBackClick = { selectedContent = "Map" })
                        
                        // Overlay with blur and "Coming Soon" message
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(10.dp),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            // Empty surface for blur and darkening effect
                        }
                        
                        // Coming soon content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Back button (clearly visible at the top)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                IconButton(
                                    onClick = { selectedContent = "Map" },
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .size(48.dp)
                                        .background(Color(0xFFFFD700), CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back to Map",
                                        tint = Color.Black
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            // Coming soon label
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFFD700),
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "COMING SOON",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Message
                            Text(
                                text = "The social features of Trail Tales are under development and will be available in the next update!",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Return button
                            Button(
                                onClick = { selectedContent = "Map" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text("Return to Map", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Show sitemap dialog when triggered
    if (showSiteMap && activeQuest != null) {
        val activeQuestCopy = activeQuest // Create a local copy for smart cast
        Dialog(onDismissRequest = { showSiteMap = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    activeQuestCopy?.let { quest ->
                        Text(
                            text = "Site Map: ${quest.title}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Image(
                            painter = painterResource(id = quest.siteMapResourceId),
                            contentDescription = "Site Map",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showSiteMap = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Close")
                            }
                            
                            Button(
                                onClick = { 
                                    // Complete the quest
                                    showSiteMap = false
                                    activeQuest = null
                                    routePoints = emptyList()
                                    Toast.makeText(context, "Quest completed! You earned 50 points.", Toast.LENGTH_LONG).show() 
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700)
                                )
                            ) {
                                Text("Complete Quest", color = Color.Black)
                            }
                        }
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

// Helper function to create a temporary image file
private fun createImageFile(context: Context): File {
    // Create an image file name with timestamp
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    
    // Get the directory for the app's private pictures directory
    val storageDir = context.getExternalFilesDir("Pictures")
    
    return File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    )
}

// Helper function to generate a simple route (for demo)
private fun generateSimpleRoute(start: LatLng, end: LatLng): List<LatLng> {
    val points = mutableListOf<LatLng>()
    points.add(start)
    
    // For Air Force Base route, create a more precise route
    if (end == AIR_FORCE_BASE_LOCATION) {
        // Create waypoints for a route to Ratmalana Air Force Base
        // These are approximate waypoints that might be on a realistic route
        points.add(LatLng(start.latitude * 0.9 + end.latitude * 0.1, start.longitude * 0.9 + end.longitude * 0.1))
        points.add(LatLng(start.latitude * 0.7 + end.latitude * 0.3, start.longitude * 0.7 + end.longitude * 0.3))
        points.add(LatLng(start.latitude * 0.5 + end.latitude * 0.5, start.longitude * 0.5 + end.longitude * 0.5))
        points.add(LatLng(start.latitude * 0.3 + end.latitude * 0.7, start.longitude * 0.3 + end.longitude * 0.7))
        points.add(LatLng(start.latitude * 0.1 + end.latitude * 0.9, start.longitude * 0.1 + end.longitude * 0.9))
    } else {
        // Add some points in between for a more realistic route
        val steps = 5
        for (i in 1 until steps) {
            val fraction = i.toFloat() / steps
            val lat = start.latitude + (end.latitude - start.latitude) * fraction
            val lng = start.longitude + (end.longitude - start.longitude) * fraction
            
            // Add some randomness to make it look like a real route
            val jitter = 0.0005 * (Math.random() - 0.5)
            points.add(LatLng(lat + jitter, lng + jitter))
        }
    }
    
    points.add(end)
    return points
}