package com.example.trail_tales_front_end_one.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trail_tales_front_end_one.android.R
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

// Data class for Friend
data class Friend(
    val id: String,
    val name: String,
    val level: Int,
    val points: Int,
    val isOnline: Boolean,
    val avatarResId: Int = R.drawable.player_down_2
)

// Data class for Leaderboard Entry
data class LeaderboardEntry(
    val id: String,
    val name: String,
    val rank: Int,
    val points: Int,
    val avatarResId: Int = R.drawable.player_down_2
)

// Data class for Achievement
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val progress: Float = 0f, // 0.0 to 1.0
    val iconResId: Int = R.drawable.star
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialsScreen(onBackClick: () -> Unit = {}) {
    // Sample data
    val friends = remember {
        listOf(
            Friend(
                id = "1",
                name = "Alex",
                level = 12,
                points = 2500,
                isOnline = true
            ),
            Friend(
                id = "2",
                name = "Taylor",
                level = 8,
                points = 1800,
                isOnline = false
            ),
            Friend(
                id = "3",
                name = "Jordan",
                level = 15,
                points = 3200,
                isOnline = true
            ),
            Friend(
                id = "4",
                name = "Casey",
                level = 5,
                points = 950,
                isOnline = false
            )
        )
    }
    
    val leaderboard = remember {
        listOf(
            LeaderboardEntry(
                id = "1",
                name = "Jordan",
                rank = 1,
                points = 3200
            ),
            LeaderboardEntry(
                id = "2",
                name = "Alex",
                rank = 2,
                points = 2500
            ),
            LeaderboardEntry(
                id = "3",
                name = "Taylor",
                rank = 3,
                points = 1800
            ),
            LeaderboardEntry(
                id = "4",
                name = "Casey",
                rank = 4,
                points = 950
            ),
            LeaderboardEntry(
                id = "5",
                name = "You",
                rank = 5,
                points = 850
            )
        )
    }
    
    val achievements = remember {
        listOf(
            Achievement(
                id = "1",
                title = "First Steps",
                description = "Complete your first trail",
                isUnlocked = true
            ),
            Achievement(
                id = "2",
                title = "Collector",
                description = "Find 10 collectables",
                isUnlocked = true,
                progress = 1.0f
            ),
            Achievement(
                id = "3",
                title = "Explorer",
                description = "Discover 5 different locations",
                isUnlocked = true,
                progress = 1.0f
            ),
            Achievement(
                id = "4",
                title = "Trailblazer",
                description = "Walk 10km in total",
                isUnlocked = false,
                progress = 0.7f
            ),
            Achievement(
                id = "5",
                title = "Social Butterfly",
                description = "Add 5 friends",
                isUnlocked = false,
                progress = 0.4f
            )
        )
    }
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Friends", "Leaderboard", "Achievements")
    
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
                    text = "Social Hub",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 48.dp), // Balance for the back button
                    color = Color(0xFFFFD700) // Gold color for game-like feel
                )
            }
            
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFFFFD700),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 3.dp,
                        color = Color(0xFFFFD700)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content based on selected tab
            when (selectedTab) {
                0 -> FriendsList(friends)
                1 -> LeaderboardList(leaderboard)
                2 -> AchievementsList(achievements)
            }
        }
    }
}

@Composable
fun FriendsList(friends: List<Friend>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Add Friend Button
        Button(
            onClick = { /* TODO: Implement add friend */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Friend",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text("Add Friend")
        }
        
        // Friends List
        if (friends.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No friends yet. Add some friends to see them here!",
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(friends) { friend ->
                    FriendItem(friend)
                }
            }
        }
    }
}

@Composable
fun FriendItem(friend: Friend) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: View friend profile */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with online indicator
            Box {
                Surface(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Image(
                        painter = painterResource(id = friend.avatarResId),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Online indicator
                if (friend.isOnline) {
                    Surface(
                        modifier = Modifier
                            .size(15.dp)
                            .align(Alignment.BottomEnd)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            ),
                        shape = CircleShape,
                        color = Color.Green
                    ) {}
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Friend info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = friend.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Level ${friend.level}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Points
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${friend.points}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LeaderboardList(entries: List<LeaderboardEntry>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Leaderboard header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rank",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(50.dp),
                    color = Color.White
                )
                Text(
                    text = "Player",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
                Text(
                    text = "Points",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(80.dp),
                    color = Color.White
                )
            }
            
            Divider(color = Color.White.copy(alpha = 0.3f))
        }
        
        items(entries) { entry ->
            LeaderboardEntryItem(entry)
        }
    }
}

@Composable
fun LeaderboardEntryItem(entry: LeaderboardEntry) {
    val isCurrentUser = entry.name == "You"
    val backgroundColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        Color.Transparent
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank with medal for top 3
        Box(
            modifier = Modifier.width(50.dp),
            contentAlignment = Alignment.Center
        ) {
            when (entry.rank) {
                1 -> Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Gold Medal",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                2 -> Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Silver Medal",
                    tint = Color(0xFFC0C0C0),
                    modifier = Modifier.size(24.dp)
                )
                3 -> Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Bronze Medal",
                    tint = Color(0xFFCD7F32),
                    modifier = Modifier.size(24.dp)
                )
                else -> Text(
                    text = "#${entry.rank}",
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color.White
                )
            }
        }
        
        // Player info
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            ) {
                Image(
                    painter = painterResource(id = entry.avatarResId),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = entry.name,
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color.White
            )
        }
        
        // Points
        Text(
            text = "${entry.points}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.width(80.dp),
            color = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color(0xFFFFD700)
        )
    }
}

@Composable
fun AchievementsList(achievements: List<Achievement>) {
    // Calculate completion percentage
    val completedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size
    val completionPercentage = (completedCount * 100) / totalCount
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Completion card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$completionPercentage% Complete",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFFFD700)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { completedCount.toFloat() / totalCount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFFFD700),
                    trackColor = MaterialTheme.colorScheme.surface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "$completedCount of $totalCount achievements unlocked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Achievements list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(achievements) { achievement ->
                AchievementItem(achievement)
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement icon
            Surface(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                color = if (achievement.isUnlocked) {
                    Color(0xFFFFD700)
                } else {
                    Color.Gray.copy(alpha = 0.5f)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = achievement.iconResId),
                        contentDescription = "Achievement Icon",
                        modifier = Modifier
                            .size(30.dp)
                            .alpha(if (achievement.isUnlocked) 1f else 0.5f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Achievement info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (achievement.isUnlocked) Color.White else Color.Gray
                )
                
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray.copy(alpha = 0.7f)
                )
                
                if (!achievement.isUnlocked && achievement.progress > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = { achievement.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                    
                    Text(
                        text = "${(achievement.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            // Unlocked indicator
            if (achievement.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
} 