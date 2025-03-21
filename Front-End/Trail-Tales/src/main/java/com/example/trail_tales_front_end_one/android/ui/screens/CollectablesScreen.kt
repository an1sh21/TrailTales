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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trail_tales_front_end_one.android.R

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

@Composable
fun CollectablesScreen(onBackClick: () -> Unit = {}) {
    var collectables by remember {
        mutableStateOf(
            listOf(
                Collectable(
                    id = "1",
                    name = "Golden Leaf",
                    description = "A rare golden leaf found only in autumn.",
                    imageResId = R.drawable.login, // Replace with actual image
                    rarity = CollectableRarity.UNCOMMON,
                    isUnlocked = true,
                    discoveryDate = "Oct 15, 2023"
                ),
                Collectable(
                    id = "2",
                    name = "Ancient Coin",
                    description = "A mysterious coin from an ancient civilization.",
                    imageResId = R.drawable.login,  // Replace with actual image
                    rarity = CollectableRarity.RARE,
                    isUnlocked = true,
                    discoveryDate = "Nov 3, 2023"
                ),
                Collectable(
                    id = "3",
                    name = "Crystal Flower",
                    description = "A beautiful flower that seems to be made of crystal.",
                    imageResId = R.drawable.login,  // Replace with actual image
                    rarity = CollectableRarity.LEGENDARY,
                    isUnlocked = false
                ),
                Collectable(
                    id = "4",
                    name = "Forest Mushroom",
                    description = "A common mushroom found in the forest.",
                    imageResId = R.drawable.login,  // Replace with actual image
                    rarity = CollectableRarity.COMMON,
                    isUnlocked = true,
                    discoveryDate = "Sep 28, 2023"
                ),
                Collectable(
                    id = "5",
                    name = "Colorful Feather",
                    description = "A colorful feather from an exotic bird.",
                    imageResId = R.drawable.login,  // Replace with actual image
                    rarity = CollectableRarity.UNCOMMON,
                    isUnlocked = true,
                    discoveryDate = "Oct 5, 2023"
                ),
                Collectable(
                    id = "6",
                    name = "Ancient Artifact",
                    description = "A mysterious artifact with unknown powers.",
                    imageResId = R.drawable.login,  // Replace with actual image
                    rarity = CollectableRarity.LEGENDARY,
                    isUnlocked = false
                ),
                Collectable(
                    id = "7",
                    name = "River Stone",
                    description = "A smooth stone polished by the river.",
                    imageResId = R.drawable.login,  // Replace with actual image
                    rarity = CollectableRarity.COMMON,
                    isUnlocked = true,
                    discoveryDate = "Aug 12, 2023"
                ),
                Collectable(
                    id = "8",
                    name = "Mountain Crystal",
                    description = "A beautiful crystal found in the mountains.",
                    imageResId = R.drawable.login,  // Replace with actual image
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
        
        // Filters
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
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        
        // Rarity filters
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
        
        // Collectables grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val filteredCollectables = collectables.filter { collectable ->
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
            
            items(filteredCollectables) { collectable ->
                CollectableItem(
                    collectable = collectable,
                    onClick = { /* Handle collectable click */ }
                )
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 48.dp)
            ) {
                Image(
                    painter = painterResource(id = collectable.imageResId),
                    contentDescription = collectable.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (collectable.isUnlocked) 1f else 0.5f)
                )
                
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
                        .background(
                            when (collectable.rarity) {
                                CollectableRarity.COMMON -> Color.Gray
                                CollectableRarity.UNCOMMON -> Color(0xFF4CAF50)
                                CollectableRarity.RARE -> Color(0xFF2196F3)
                                CollectableRarity.LEGENDARY -> Color(0xFFFFD700)
                            }
                        )
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
                        color = when (collectable.rarity) {
                            CollectableRarity.COMMON -> Color.Gray
                            CollectableRarity.UNCOMMON -> Color(0xFF4CAF50)
                            CollectableRarity.RARE -> Color(0xFF2196F3)
                            CollectableRarity.LEGENDARY -> Color(0xFFFFD700)
                        }
                    )
                }
            }
        }
    }
} 