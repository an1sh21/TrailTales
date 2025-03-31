package com.example.trail_tales_front_end_one.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trail_tales_front_end_one.android.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import android.util.Log

// Data class for Collectable
data class Collectable(
    val id: String,
    val name: String,
    val description: String,
    val imageResId: Int,
    val rarity: CollectableRarity,
    val isUnlocked: Boolean = false,
    val discoveryDate: String? = null
)

enum class CollectableRarity {
    COMMON, UNCOMMON, RARE, LEGENDARY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectablesScreen(
    onBackClick: () -> Unit = {},
    collectedItems: Set<String> = emptySet(),
    totalPoints: Int = 0
) {
    val context = LocalContext.current
    
    // State for loading screen
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    // Get current date for newly collected items
    val currentDate = remember { java.time.LocalDate.now().toString() }
    
    // Prepare collectables data with unlocked state from collectedItems
    val collectablesList = remember(collectedItems) {
        listOf(
            Collectable(
                id = "1",
                name = "AeroSpatiale SA 365 Dauphin",
                description = "A rare helicopter model.",
                imageResId = R.drawable.pink,
                rarity = CollectableRarity.UNCOMMON,
                isUnlocked = collectedItems.contains("AeroSpatiale SA 365 Dauphin"),
                discoveryDate = if (collectedItems.contains("AeroSpatiale SA 365 Dauphin")) currentDate else null
            ),
            Collectable(
                id = "2",
                name = "Boulton Paul Balliol",
                description = "A historical aircraft model.",
                imageResId = R.drawable.green,
                rarity = CollectableRarity.COMMON,
                isUnlocked = collectedItems.contains("Boulton Paul Balliol"),
                discoveryDate = if (collectedItems.contains("Boulton Paul Balliol")) currentDate else null
            ),
            Collectable(
                id = "3",
                name = "Douglas DC - 3",
                description = "A classic aircraft model.",
                imageResId = R.drawable.purpule,
                rarity = CollectableRarity.LEGENDARY,
                isUnlocked = collectedItems.contains("Douglas DC - 3"),
                discoveryDate = if (collectedItems.contains("Douglas DC - 3")) currentDate else null
            ),
            Collectable(
                id = "4",
                name = "PAZMANY PL-2",
                description = "A unique aircraft model.",
                imageResId = R.drawable.heart,
                rarity = CollectableRarity.LEGENDARY,
                isUnlocked = collectedItems.contains("PAZMANY PL-2"),
                discoveryDate = if (collectedItems.contains("PAZMANY PL-2")) currentDate else null
            ),
            Collectable(
                id = "5",
                name = "PT-6",
                description = "A modern aircraft model.",
                imageResId = R.drawable.key,
                rarity = CollectableRarity.LEGENDARY,
                isUnlocked = collectedItems.contains("PT-6"),
                discoveryDate = if (collectedItems.contains("PT-6")) currentDate else null
            ),
            Collectable(
                id = "6",
                name = "Westland Sikorsky S-51 Dragonfly",
                description = "A vintage helicopter model.",
                imageResId = R.drawable.star,
                rarity = CollectableRarity.COMMON,
                isUnlocked = collectedItems.contains("Westland Sikorsky S-51 Dragonfly"),
                discoveryDate = if (collectedItems.contains("Westland Sikorsky S-51 Dragonfly")) currentDate else null
            )
        )
    }
    
    // Log the state for debugging
    LaunchedEffect(collectedItems) {
        Log.d("CollectablesScreen", "Collected Items: $collectedItems")
        Log.d("CollectablesScreen", "Total Points: $totalPoints")
        collectablesList.forEach { collectable ->
            Log.d("CollectablesScreen", "Collectable: ${collectable.name}, Unlocked: ${collectable.isUnlocked}")
        }
    }
    
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = remember { listOf("All", "Unlocked", "Locked") }
    
    var selectedRarity by remember { mutableStateOf("All Rarities") }
    val rarityFilters = remember { listOf("All Rarities", "Common", "Uncommon", "Rare", "Legendary") }
    
    // Much shorter loading delay
    LaunchedEffect(key1 = Unit) {
        delay(100)
        isLoading = false
    }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            // Use the collectables.jpg image as background with a dimming overlay
            Image(
                painter = painterResource(id = R.drawable.collectables),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            
            // Semi-transparent overlay to ensure content readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212).copy(alpha = 0.7f))
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top bar with back button and title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Title
                    Text(
                        text = "Inventory",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 48.dp), // Balance for the back button
                        color = Color(0xFFFFD700) // Gold color for game-like feel
                    )
                }
                
                // Simplified filter UI - just buttons in a row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Simplified rarity filter UI
                ScrollableTabRow(
                    selectedTabIndex = rarityFilters.indexOf(selectedRarity),
                    edgePadding = 0.dp,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    rarityFilters.forEachIndexed { index, rarity ->
                        Tab(
                            selected = selectedRarity == rarity,
                            onClick = { selectedRarity = rarity },
                            text = { Text(rarity) }
                        )
                    }
                }
                
                // Stats
                val unlockedCount = collectablesList.count { it.isUnlocked }
                val totalCount = collectablesList.size
                val legendaryUnlocked = collectablesList.count { it.rarity == CollectableRarity.LEGENDARY && it.isUnlocked }
                val legendaryTotal = collectablesList.count { it.rarity == CollectableRarity.LEGENDARY }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = "Collected",
                        value = "$unlockedCount/$totalCount",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    StatItem(
                        title = "Points",
                        value = "$totalPoints",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    StatItem(
                        title = "Legendary",
                        value = "$legendaryUnlocked/$legendaryTotal",
                        color = Color(0xFFFFD700)
                    )
                }
                
                // Apply filters to the collectables list - simplified and optimized
                val filteredCollectables = remember(selectedFilter, selectedRarity, collectablesList) {
                    collectablesList.filter { collectable ->
                        val matchesStatusFilter = when (selectedFilter) {
                            "Unlocked" -> collectable.isUnlocked
                            "Locked" -> !collectable.isUnlocked
                            else -> true
                        }
                        
                        val matchesRarityFilter = when (selectedRarity) {
                            "Common" -> collectable.rarity == CollectableRarity.COMMON
                            "Uncommon" -> collectable.rarity == CollectableRarity.UNCOMMON
                            "Rare" -> collectable.rarity == CollectableRarity.RARE
                            "Legendary" -> collectable.rarity == CollectableRarity.LEGENDARY
                            else -> true
                        }
                        
                        matchesStatusFilter && matchesRarityFilter
                    }
                }
                
                // Use LazyVerticalGrid with fixed spans for better performance
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = filteredCollectables,
                        key = { it.id } // Using stable item keys
                    ) { collectable ->
                        SimpleCollectableItem(collectable = collectable)
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(title: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

// Simplified collectable item that loads images directly without custom caching
@Composable
fun SimpleCollectableItem(collectable: Collectable) {
    // Using remember for values that won't change to prevent unnecessary recompositions
    val rarityColor = remember(collectable.rarity) {
        when (collectable.rarity) {
            CollectableRarity.COMMON -> Color.Gray
            CollectableRarity.UNCOMMON -> Color(0xFF4CAF50)
            CollectableRarity.RARE -> Color(0xFF2196F3)
            CollectableRarity.LEGENDARY -> Color(0xFFFFD700)
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 48.dp)
            ) {
                // Use standard Image composable with fixed size
                Image(
                    painter = painterResource(id = collectable.imageResId),
                    contentDescription = collectable.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = if (collectable.isUnlocked) 1f else 0.5f
                )
                
                // Locked overlay - only render when needed
                if (!collectable.isUnlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                // Rarity indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(rarityColor)
                )
            }
            
            // Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                Text(
                    text = collectable.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (collectable.isUnlocked && collectable.discoveryDate != null) {
                    Text(
                        text = "Found: ${collectable.discoveryDate}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = collectable.rarity.name,
                        fontSize = 12.sp,
                        color = rarityColor
                    )
                }
            }
        }
    }
} 