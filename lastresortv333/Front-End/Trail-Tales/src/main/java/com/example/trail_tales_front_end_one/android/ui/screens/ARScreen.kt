package com.example.trail_tales_front_end_one.android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.ar.core.*
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView
import kotlinx.coroutines.launch
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import kotlin.math.sin
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.trail_tales_front_end_one.android.R
import io.github.sceneview.node.Node
import androidx.compose.runtime.DisposableEffect
import io.github.sceneview.SceneView

// Helper function to safely stop animation
private fun stopAnimation(
    modelId: String,
    activeModels: MutableMap<String, Boolean>,
    animationHandlers: MutableMap<String, Handler>
) {
    try {
        Log.d("ARScreen", "Stopping animation for $modelId")
        activeModels[modelId] = false
        animationHandlers[modelId]?.let { handler ->
            handler.removeCallbacksAndMessages(null)
            animationHandlers.remove(modelId)
        }
    } catch (e: Exception) {
        Log.e("ARScreen", "Error stopping animation for $modelId: ${e.message}")
    }
}

// Helper function to animate model
private fun animateModel(
    modelNode: ModelNode,
    modelId: String,
    activeModels: MutableMap<String, Boolean>,
    animationHandlers: MutableMap<String, Handler>
) {
    try {
        Log.d("ARScreen", "Starting model animation for $modelId")
        val handler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()

        // Mark model as active
        activeModels[modelId] = true

        // Store initial position safely
        val initialPosition = Position(
            modelNode.position.x,
            modelNode.position.y,
            modelNode.position.z
        )

        val runnable = object : Runnable {
            override fun run() {
                try {
                    // Check if model is still active
                    if (!activeModels.getOrDefault(modelId, false)) {
                        Log.d("ARScreen", "Animation stopped for $modelId - model no longer active")
                        handler.removeCallbacksAndMessages(null)
                        animationHandlers.remove(modelId)
                        return
                    }

                    // Safety check for parent node
                    if (modelNode.parent == null) {
                        Log.d("ARScreen", "Animation stopped for $modelId - model has no parent")
                        activeModels[modelId] = false
                        handler.removeCallbacksAndMessages(null)
                        animationHandlers.remove(modelId)
                        return
                    }

                    // Get the elapsed time in seconds
                    val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000f

                    // Safely update rotation and position
                    try {
                        modelNode.rotation = Rotation(0f, elapsedSeconds * 45f, 0f)
                        
                        val floatOffset = sin(elapsedSeconds * 2f) * 0.05f
                        modelNode.position = Position(
                            initialPosition.x,
                            initialPosition.y + floatOffset,
                            initialPosition.z
                        )

                        // Schedule next frame only if model is still active
                        if (activeModels.getOrDefault(modelId, false)) {
                            handler.postDelayed(this, 16) // ~60fps
                        }
                    } catch (e: Exception) {
                        Log.e("ARScreen", "Error updating model animation: ${e.message}")
                        activeModels[modelId] = false
                        handler.removeCallbacksAndMessages(null)
                        animationHandlers.remove(modelId)
                    }
                } catch (e: Exception) {
                    Log.e("ARScreen", "Animation error: ${e.message}")
                    activeModels[modelId] = false
                    handler.removeCallbacksAndMessages(null)
                    animationHandlers.remove(modelId)
                }
            }
        }

        handler.post(runnable)
        animationHandlers[modelId] = handler

    } catch (e: Exception) {
        Log.e("ARScreen", "Failed to start animation for $modelId: ${e.message}")
        activeModels[modelId] = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARScreen(
    onBackPressed: () -> Unit,
    onItemCollected: (String, Int) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // AR Core components
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)
    val planeRenderer = remember { mutableStateOf(true) }

    // State for tracking images and models
    val trackingState = remember { mutableStateOf<String?>(null) }
    val trackingFailureReason = remember { mutableStateOf<TrackingFailureReason?>(null) }
    val isImageTracking = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }

    // Keep track of which images we've already processed
    val processedImages = remember { mutableStateListOf<String>() }

    // Keep track of existing model nodes
    val modelNodes = remember { mutableMapOf<String, ModelNode>() }
    val imageNodes = remember { mutableMapOf<String, AugmentedImageNode>() }

    // Model paths - replace with your actual model filenames
    val modelPaths = remember {
        mapOf(
            "AeroSpatiale SA 365 Dauphin" to "models/AeroSpatiale SA 365 Dauphin.glb",
            "Boulton Paul Balliol" to "models/Boulton Paul Balliol.glb",
            "Douglas DC - 3" to "models/Douglas DC - 3.glb",
            "PAZMANY PL-2" to "models/PAZMANY PL-2.glb",
            "PT-6" to "models/PT-6.glb",
            "Westland Sikorsky S-51 Dragonfly" to "models/Westland Sikorsky S-51 Dragonfly.glb"
        )
    }

    // Target image names - replace with your actual tracking image names
    val trackableImages = remember {
        mapOf(
            "AeroSpatiale SA 365 Dauphin" to "AeroSpatiale SA 365 Dauphin",
            "Boulton Paul Balliol" to "Boulton Paul Balliol",
            "Douglas DC - 3" to "Douglas DC - 3",
            "PAZMANY PL-2" to "PAZMANY PL-2",
            "PT-6" to "PT-6",
            "Westland Sikorsky S-51 Dragonfly" to "Westland Sikorsky S-51 Dragonfly"
        )
    }
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }
    val soundId = remember { soundPool.load(context, R.raw.collectionsound, 1) }

    // Initialize points at 0
    val points = remember { mutableStateOf(0) }

    // Keep track of collected items with their points
    val collectedItems = remember { mutableSetOf<String>() }

    // Points for each item when collected
    val itemPoints = remember {
        mapOf(
            "AeroSpatiale SA 365 Dauphin" to 100,
            "Boulton Paul Balliol" to 150,
            "Douglas DC - 3" to 200,
            "PAZMANY PL-2" to 120,
            "PT-6" to 180,
            "Westland Sikorsky S-51 Dragonfly" to 160
        )
    }

    // Add a map to store animation handlers
    val animationHandlers = remember { mutableMapOf<String, Handler>() }

    // Add state tracking for model validity
    val activeModels = remember { mutableMapOf<String, Boolean>() }

    // Add cleanup effect
    DisposableEffect(key1 = Unit) {
        onDispose {
            try {
                animationHandlers.keys.toList().forEach { modelId ->
                    try {
                        stopAnimation(modelId, activeModels, animationHandlers)
                    } catch (e: Exception) {
                        Log.e("ARScreen", "Error cleaning up animation for $modelId: ${e.message}")
                    }
                }
                animationHandlers.clear()
                activeModels.clear()
                modelNodes.clear()
                imageNodes.clear()
            } catch (e: Exception) {
                Log.e("ARScreen", "Error during cleanup: ${e.message}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // AR Scene
    ARScene(
        modifier = Modifier.fillMaxSize(),
        childNodes = childNodes,
        engine = engine,
        view = view,
        modelLoader = modelLoader,
        collisionSystem = collisionSystem,
        planeRenderer = planeRenderer.value,
        cameraNode = cameraNode,
        materialLoader = materialLoader,
        onTrackingFailureChanged = {
            trackingFailureReason.value = it
                    if (it != null) {
                        trackingState.value = when (it) {
                            TrackingFailureReason.NONE -> "Tracking normal"
                            TrackingFailureReason.BAD_STATE -> "Bad state, reset AR session"
                            TrackingFailureReason.INSUFFICIENT_LIGHT -> "Insufficient light. Try moving to a brighter area"
                            TrackingFailureReason.EXCESSIVE_MOTION -> "Excessive motion. Hold the device still"
                            TrackingFailureReason.INSUFFICIENT_FEATURES -> "Insufficient features. Point at a surface with more texture"
                            else -> "Unknown tracking failure"
                        }
                    } else {
                        trackingState.value = "Tracking normal"
                    }
                },
                onSessionUpdated = { session, updatedFrame ->
                    // Check for image tracking
                    try {
                        // Log all trackables for debugging
                        val allImages = updatedFrame.getUpdatedTrackables(AugmentedImage::class.java)
                        Log.d("ARScreen", "Total trackable images found: ${allImages.size}")

                        // Log status of each image
                        allImages.forEach { img ->
                            Log.d("ARScreen", """
                                Image Name: ${img.name}
                                Tracking State: ${img.trackingState}
                                Tracking Method: ${img.trackingMethod}
                                Center Pose: ${img.centerPose}
                                Extent: ${img.extentX} x ${img.extentZ}
                            """.trimIndent())
                        }

                        val updatedImages = allImages.filter { it.trackingState == TrackingState.TRACKING }
                        Log.d("ARScreen", "Images in TRACKING state: ${updatedImages.size}")

                        // First, remove ALL nodes from the scene
                        val previousNodeCount = childNodes.size
                        childNodes.clear()
                        Log.d("ARScreen", "Cleared $previousNodeCount nodes from scene")

                        // Only process the first tracked image
                        if (updatedImages.isNotEmpty()) {
                            val image = updatedImages[0]  // Get the first tracked image
                            val imageName = image.name
                            Log.d("ARScreen", """
                                Processing tracked image:
                                Name: $imageName
                                Size: ${image.extentX} x ${image.extentZ} meters
                                Center: ${image.centerPose}
                                Tracking Method: ${image.trackingMethod}
                            """.trimIndent())

                            // Process if we track this image and it hasn't been collected
                            if (trackableImages.containsKey(imageName) && !collectedItems.contains(imageName)) {
                                Log.d("ARScreen", "Image '$imageName' is in our trackable list and not collected")

                                // If we already have a node for this image, show it
                                if (imageNodes.containsKey(imageName)) {
                                    val existingNode = imageNodes[imageName]
                                    Log.d("ARScreen", """
                                        Reusing existing node:
                                        Node name: ${existingNode?.name}
                                        Current pose: ${existingNode?.pose}
                                        New image pose: ${image.centerPose}
                                    """.trimIndent())

                                    existingNode?.let { node ->
                                        node.pose = image.centerPose
                                        childNodes.add(node)
                                        Log.d("ARScreen", "Updated and re-added node to scene")
                                    }
                                } else {
                                    Log.d("ARScreen", "No existing node found, creating new one")
                                    // Create new node and model for first detection
                                    Log.d("ARScreen", "Creating first instance for image: $imageName")
                                    trackableImages[imageName]?.let { modelKey ->
                                        modelPaths[modelKey]?.let { modelPath ->
                                            // Load the model
                                            isLoading.value = true
                                            scope.launch {
                                                try {
                                                    val modelInstance = modelLoader.loadModelInstance(modelPath)
                                                    if (modelInstance == null) {
                                                        Log.e("ARScreen", "Failed to load model instance for $imageName")
                                                        Toast.makeText(context, "Failed to load model: Model is null", Toast.LENGTH_SHORT).show()
                                                        isLoading.value = false
                                                        return@launch
                                                    }

                                                    val modelScale = when (imageName) {
                                                        "Douglas DC - 3" -> 2.5f
                                                        else -> 0.1f
                                                    }

                                                    val modelNode = ModelNode(
                                                        modelInstance = modelInstance,
                                                        scaleToUnits = modelScale
                                                    ).apply {
                                                        // Set initial position slightly above the image
                                                        position = Position(0f, 0.1f, 0f)

                                                        // Add touch event handling using the correct API
                                                        onTouch = { hitTestResult, motionEvent ->
                                                            try {
                                                                // Play sound effect
                                                                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)

                                                                // Only update points if this item hasn't been collected before
                                                                if (!collectedItems.contains(imageName)) {
                                                                    // Add to collected items set
                                                                    collectedItems.add(imageName)
                                                                    val itemPointValue = itemPoints[imageName] ?: 0
                                                                    points.value += itemPointValue
                                                                    
                                                                    // Notify parent about collection to update CollectablesScreen
                                                                    onItemCollected(imageName, itemPointValue)
                                                                    
                                                                    Toast.makeText(
                                                                        context, 
                                                                        "Collected: $imageName! +$itemPointValue points\nTotal Points: ${points.value}\nUnlocked in Collectables!", 
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()

                                                                    Log.d("ARScreen", "Item collected and unlocked: $imageName, Points: $itemPointValue, Total: ${points.value}")
                                                                } else {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "You've already collected this model!",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }

                                                                stopAnimation(imageName, activeModels, animationHandlers)
                                                                isVisible = false

                                                                Handler(Looper.getMainLooper()).postDelayed({
                                                                    try {
                                                                        parent?.removeChildNode(this)
                                                                        modelNodes.remove(imageName)
                                                                        imageNodes.remove(imageName)
                                                                    } catch (e: Exception) {
                                                                        Log.e("ARScreen", "Error removing node: ${e.message}")
                                                                    }
                                                                }, 1000)

                                                                true
                                                            } catch (e: Exception) {
                                                                Log.e("ARScreen", "Error in onTouch: ${e.message}")
                                                                false
                                                            }
                                                        }
                                                    }

                                                    modelNodes[imageName] = modelNode
                                                    Log.d("ARScreen", "Created model node for $imageName with scale: $modelScale")

                                                    animateModel(modelNode, imageName, activeModels, animationHandlers)

                                                    val node = AugmentedImageNode(
                                                        engine = engine,
                                                        augmentedImage = image
                                                    ).apply {
                                                        name = imageName
                                                        addChildNode(modelNode)
                                                    }

                                                    imageNodes[imageName] = node
                                                    childNodes.add(node)
                                                    Log.d("ARScreen", "Added new node to scene for $imageName")

                                                    Toast.makeText(context, "Model placed for $imageName", Toast.LENGTH_SHORT).show()
                                                } catch (e: Exception) {
                                                    Log.e("ARScreen", "Error creating model for $imageName: ${e.message}")
                                                    Toast.makeText(context, "Failed to load model: ${e.message}", Toast.LENGTH_SHORT).show()
                                                } finally {
                                                    isLoading.value = false
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.d("ARScreen", "Image not in trackable list or already collected: $imageName")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ARScreen", "Error in onSessionUpdated: ${e.message}")
                        trackingState.value = "Error: ${e.message}"
                    }
                },
                sessionConfiguration = { session, config ->
                    Log.d("ARScreen", "Configuring AR session")

                    // Configure depth if supported
                    val depthSupported = session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)
                    Log.d("ARScreen", "Depth mode supported: $depthSupported")
                    config.depthMode = when (depthSupported) {
                true -> Config.DepthMode.AUTOMATIC
                else -> Config.DepthMode.DISABLED
                }

                    // Configure lighting
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

                    // Configure image tracking database
                    val augmentedImageDatabase = AugmentedImageDatabase(session)
                    Log.d("ARScreen", "Created new AugmentedImageDatabase")

                    // Log available assets
                    try {
                        context.assets.list("images")?.let { files ->
                            Log.d("ARScreen", "Available images in assets/images/: ${files.joinToString()}")
                        }
                    } catch (e: Exception) {
                        Log.e("ARScreen", "Error listing assets: ${e.message}")
                    }

                    // Add all images to track
                    var successfullyAddedImages = 0
                    trackableImages.keys.forEach { imageName ->
                        try {
                            // Strip the file extension for lookup since our files use .jpg
                            val imageFileName = imageName
                            Log.d("ARScreen", "Attempting to load image: $imageFileName")

                            val inputStream = context.assets.open("images/$imageFileName.jpg")
                            Log.d("ARScreen", "Successfully opened image file: $imageFileName.jpg")

                            val augmentedImageBitmap = loadAugmentedImageBitmap(inputStream)
                            if (augmentedImageBitmap != null) {
                                Log.d("ARScreen", """
                                    Loaded bitmap for $imageFileName:
                                    Width: ${augmentedImageBitmap.width}
                                    Height: ${augmentedImageBitmap.height}
                                    Config: ${augmentedImageBitmap.config}
                                """.trimIndent())

                                val status = augmentedImageDatabase.addImage(imageName, augmentedImageBitmap)
                                if (status != -1) {
                                    successfullyAddedImages++
                                    Log.d("ARScreen", "Successfully added image to database: $imageName (Index: $status)")
                                } else {
                                    Log.e("ARScreen", "Failed to add image to database: $imageName")
                                }
                            } else {
                                Log.e("ARScreen", "Failed to load bitmap for: $imageFileName")
                            }
                        } catch (e: Exception) {
                            Log.e("ARScreen", "Failed to load reference image: $imageName", e)
                        }
                    }

                    Log.d("ARScreen", "Successfully added $successfullyAddedImages images to database")
                    config.augmentedImageDatabase = augmentedImageDatabase
                    isImageTracking.value = true
                    Log.d("ARScreen", "AR session configuration complete")
                },
            )

            // UI Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        tonalElevation = 4.dp
                    ) {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Points display
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = "Points: ${points.value}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    // Status info
                    if (!trackingState.value.isNullOrEmpty()) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            tonalElevation = 4.dp
                        ) {
                            Text(
                                text = trackingState.value ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Loading indicator
            if (isLoading.value) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Loading model...", fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Instructions
            if (!isLoading.value) {
                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "Point camera at aircraft images to see 3D models",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }


// Utility function to load bitmap for AR image tracking
private fun loadAugmentedImageBitmap(inputStream: java.io.InputStream): android.graphics.Bitmap? {
    return try {
        val options = android.graphics.BitmapFactory.Options().apply {
            inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
        }
        android.graphics.BitmapFactory.decodeStream(inputStream, null, options)
    } catch (e: Exception) {
        null
    } finally {
        try {
            inputStream.close()
        } catch (e: Exception) {
            // Ignore close error
        }
    }
}