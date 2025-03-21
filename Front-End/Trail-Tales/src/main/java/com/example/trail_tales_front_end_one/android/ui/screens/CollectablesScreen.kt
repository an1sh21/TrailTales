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
import com.example.trail_tales_front_end_one.android.ui.components.OptimizedImage
import com.example.trail_tales_front_end_one.android.ui.components.ImageCacheManager
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

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
fun CollectablesScreen(onBackClick: () -> Unit = {}) {
    // Initialize the ImageCacheManager
    ImageCacheManager()
    
    // State for loading screen
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    // Show loading indicator for a short time to allow UI initialization
    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            delay(300) // Reduced the delay for better user experience
            isLoading = false
        }
    }
    
    var collectables by remember {
        mutableStateOf(
            listOf(
                Collectable(
                    id = "1",
                    name = "Sakura Sapphire",
                    description = "A rare golden leaf found only in autumn.",
                    imageResId = R.drawable.pink,
                    rarity = CollectableRarity.UNCOMMON,
                    isUnlocked = true,
                    discoveryDate = "Oct 15, 2023"
                ),
                Collectable(
                    id = "2",
                    name = "Serendib Jade",
                    description = "A mysterious coin from an ancient civilization.",
                    imageResId = R.drawable.green,
                    rarity = CollectableRarity.COMMON,
                    isUnlocked = true,
                    discoveryDate = "Nov 3, 2023"
                ),
                Collectable(
                    id = "3",
                    name = "Amethyst of Anuradhapura",
                    description = "A beautiful flower that seems to be made of crystal.",
                    imageResId = R.drawable.purpule,
                    rarity = CollectableRarity.LEGENDARY,
                    isUnlocked = false
                ),
                Collectable(
                    id = "4",
                    name = "Ruhunu Ruby",
                    description = "A common mushroom found in the forest.",
                    imageResId = R.drawable.heart,
                    rarity = CollectableRarity.LEGENDARY,
                    isUnlocked = true,
                    discoveryDate = "Sep 28, 2023"
                ),
                Collectable(
                    id = "5",
                    name = "Sigiriya Key",
                    description = "A colorful feather from an exotic bird.",
                    imageResId = R.drawable.key,
                    rarity = CollectableRarity.LEGENDARY,
                    isUnlocked = true,
                    discoveryDate = "Oct 5, 2023"
                ),
                Collectable(
                    id = "6",
                    name = "Araliya Sta",
                    description = "A mysterious artifact with unknown powers.",
                    imageResId = R.drawable.star,
                    rarity = CollectableRarity.COMMON,
                    isUnlocked = false
                ),
                Collectable(
                    id = "7",
                    name = "River Stone",
                    description = "A smooth stone polished by the river.",
                    imageResId = R.drawable.badge,
                    rarity = CollectableRarity.LEGENDARY,
                    isUnlocked = true,
                    discoveryDate = "Aug 12, 2023"
                ),
                Collectable(
                    id = "8",
                    name = "Mahavansa Hoard",
                    description = "A beautiful crystal found in the mountains.",
                    imageResId = R.drawable.chest,
                    rarity = CollectableRarity.RARE,
                    isUnlocked = false
                )
            )
        )
    }
    
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Unlocked", "Locked")
    
    var selectedRarity by remember { mutableStateOf("All Rarities") }
    val rarityFilters = listOf("All Rarities", "Common", "Uncommon", "Rare", "Legendary")
    
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
            // Using a solid color background instead of a large image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212)) // Dark background instead of image
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
                
                // Filters - using a scrollable row for better performance
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    filters.forEachIndexed { index, filter ->
                        SegmentedButton(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            shape = when (index) {
                                0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                                filters.lastIndex -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                                else -> RoundedCornerShape(0.dp)
                            }
                        ) {
                            Text(filter)
                        }
                    }
                }
                
                // Rarity filters - Optimized scrollable tab row
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val unlockedCount = collectables.count { it.isUnlocked }
                    val totalCount = collectables.size
                    
                    StatItem(
                        title = "Collected",
                        value = "$unlockedCount/$totalCount",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    StatItem(
                        title = "Completion",
                        value = "${(unlockedCount * 100 / totalCount)}%",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    StatItem(
                        title = "Legendary",
                        value = "${collectables.count { it.rarity == CollectableRarity.LEGENDARY && it.isUnlocked }}/${collectables.count { it.rarity == CollectableRarity.LEGENDARY }}",
                        color = Color(0xFFFFD700)
                    )
                }
                
                // Collectables grid - optimized with better filtering and virtualization
                val filteredCollectables = remember(selectedFilter, selectedRarity, collectables) {
                    collectables.filter { collectable ->
                        val statusFilter = when (selectedFilter) {
                            "Unlocked" -> collectable.isUnlocked
                            "Locked" -> !collectable.isUnlocked
                            else -> true
                        }
                        
                        val rarityFilter = when (selectedRarity) {
                            "Common" -> collectable.rarity == CollectableRarity.COMMON
                            "Uncommon" -> collectable.rarity == CollectableRarity.UNCOMMON
                            "Rare" -> collectable.rarity == CollectableRarity.RARE
                            "Legendary" -> collectable.rarity == CollectableRarity.LEGENDARY
                            else -> true
                        }
                        
                        statusFilter && rarityFilter
                    }
                }
                
                // Use LazyVerticalGrid with optimized item key management
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = filteredCollectables,
                        key = { it.id } // Using key for better recomposition behavior
                    ) { collectable ->
                        CollectableItem(
                            collectable = collectable,
                            onClick = { /* Handle collectable click */ }
                        )
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

@Composable
fun CollectableItem(
    collectable: Collectable,
    onClick: () -> Unit
) {
    // Use remember to prevent unnecessary recompositions
    val cardColor = MaterialTheme.colorScheme.surface
    val rarityColor = remember(collectable.rarity) {
        when (collectable.rarity) {
            CollectableRarity.COMMON -> Color.Gray
            CollectableRarity.UNCOMMON -> Color(0xFF4CAF50)
            CollectableRarity.RARE -> Color(0xFF2196F3)
            CollectableRarity.LEGENDARY -> Color(0xFFFFD700)
        }
    }
    
    // Matrix used to desaturate images for locked items, calculated only once
    val lockedMatrix = remember {
        ColorMatrix().apply { 
            setToSaturation(0.3f)
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image with optimization
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 48.dp)
            ) {
                // Using our optimized image component instead of regular Image
                OptimizedImage(
                    imageResId = collectable.imageResId,
                    contentDescription = collectable.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = if (!collectable.isUnlocked) 
                        ColorFilter.colorMatrix(lockedMatrix) 
                        else null,
                    maxWidth = 300,  // Small size for grid items
                    maxHeight = 300  // Small size for grid items
                )
                
                // Optimized locked overlay - only show if needed
                if (!collectable.isUnlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
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
                    .background(cardColor)
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