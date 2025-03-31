package com.example.trail_tales_front_end_one.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.trail_tales_front_end_one.android.R

// Data class for Quest
data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: QuestDifficulty,
    val reward: Int,
    val isCompleted: Boolean = false,
    val distance: Float = 0f,
    val isFavorite: Boolean = false
)

enum class QuestDifficulty {
    EASY, MEDIUM, HARD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestsScreen(
    onBackClick: () -> Unit = {},
    onQuestStart: (Quest) -> Unit = {}  // Add parameter for quest start callback
) {
    var quests by remember {
        mutableStateOf(
            listOf(
                Quest(
                    id = "1",
                    title = "Mission 01 : AIR-FORCE BASE",
                    description = "Find the hidden tokens inside the AIR-FORCE base Rathmalana.",
                    difficulty = QuestDifficulty.EASY,
                    reward = 50,
                    distance = 1.2f
                ),
                Quest(
                    id = "2",
                    title = "Mountain Peak Challenge",
                    description = "Reach the top of Mount Everest and enjoy the breathtaking view.",
                    difficulty = QuestDifficulty.HARD,
                    reward = 150,
                    distance = 5.7f,
                    isFavorite = true
                ),
                Quest(
                    id = "3",
                    title = "Ancient Ruins Exploration",
                    description = "Explore the ancient ruins and discover the hidden treasure.",
                    difficulty = QuestDifficulty.MEDIUM,
                    reward = 100,
                    distance = 3.4f,
                    isCompleted = true
                ),
                Quest(
                    id = "4",
                    title = "Lakeside Picnic",
                    description = "Find the perfect spot by the lake for a picnic.",
                    difficulty = QuestDifficulty.EASY,
                    reward = 30,
                    distance = 0.8f
                ),
                Quest(
                    id = "5",
                    title = "Bird Watching Challenge",
                    description = "Spot and photograph at least 5 different bird species in the park.",
                    difficulty = QuestDifficulty.MEDIUM,
                    reward = 80,
                    distance = 2.5f
                )
            )
        )
    }
    
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Completed", "Favorites")
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.quest),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
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
                    text = "Quests",
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
                    .padding(bottom = 16.dp),
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
            
            // Quests list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filteredQuests = when (selectedFilter) {
                    "Active" -> quests.filter { !it.isCompleted }
                    "Completed" -> quests.filter { it.isCompleted }
                    "Favorites" -> quests.filter { it.isFavorite }
                    else -> quests
                }
                
                items(filteredQuests) { quest ->
                    QuestItem(
                        quest = quest,
                        onQuestClick = { /* Handle quest click */ },
                        onFavoriteToggle = { 
                            quests = quests.map { 
                                if (it.id == quest.id) it.copy(isFavorite = !it.isFavorite) else it 
                            }
                        },
                        onStartClick = { onQuestStart(quest) }  // Add handler for start quest button
                    )
                }
            }
        }
    }
}

@Composable
fun QuestItem(
    quest: Quest,
    onQuestClick: (Quest) -> Unit,
    onFavoriteToggle: () -> Unit,
    onStartClick: () -> Unit  // Add parameter for start quest button click
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onQuestClick(quest) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title with completion status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (quest.isCompleted) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Completed",
                            tint = Color.Green,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    Text(
                        text = quest.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Favorite icon
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (quest.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (quest.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (quest.isFavorite) Color(0xFFFFD700) else Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = quest.description,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quest details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Difficulty
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (quest.difficulty) {
                                QuestDifficulty.EASY -> Color(0xFF4CAF50)
                                QuestDifficulty.MEDIUM -> Color(0xFFFFA000)
                                QuestDifficulty.HARD -> Color(0xFFF44336)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = quest.difficulty.name,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                
                // Reward
                Text(
                    text = "${quest.reward} XP",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Distance
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Distance",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${quest.distance} km",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Add a Start Quest button for the Air Force Base quest
            if (quest.id == "1" && !quest.isCompleted) {  // Air Force Base quest ID is "1"
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)  // Gold color
                    )
                ) {
                    Text(
                        text = "Start Quest",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (quest.isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Completed",
                    color = Color.Green,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
} 