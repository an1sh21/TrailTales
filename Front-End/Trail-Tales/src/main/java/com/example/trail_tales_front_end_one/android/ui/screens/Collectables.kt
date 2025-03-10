package com.example.trail_tales_front_end_one.android.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*

import com.example.trail_tales_front_end_one.android.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "collectables") {
                composable("home") { HomeScreen(navController) }
                composable("collectables") {
                    CollectableScreen(
                        navController = navController,
                        totalCoins = 100,
                        visitedSites = 5,
                        tokenCount = 3,
                        updateStats = { _, _, _ -> }
                    )
                }
            }
        }
    }
}

// ✅ Home Page with Background
@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)), // Dark Background
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Welcome to the Home Page!", fontSize = 24.sp, color = Color.White)
    }
}

// ✅ Updated CollectableScreen with Fixes
@Composable
fun CollectableScreen(
    navController: NavController,
    totalCoins: Int,
    visitedSites: Int,
    tokenCount: Int,
    updateStats: (Int, Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Enable scrolling
            .background(Color(0xFF1E1E2E))
            .padding(horizontal = 40.dp), // Adjust padding
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        Text(
            text = "Collectables",
            fontSize = 40.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        InfoCard("Total Coins", "$totalCoins", R.drawable.login, Color(0xFFFFD700), "You have collected many coins!")
        InfoCard("Visited Sites", "$visitedSites", R.drawable.login, Color(0xFF4CAF50), "Explore new places!")
        InfoCard("Total Tokens", "$tokenCount", R.drawable.login, Color(0xFF03A9F4), "Earned valuable tokens!")

        Spacer(modifier = Modifier.weight(1f)) // Push buttons to bottom

        // ✅ Place Both Buttons in the Same Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { updateStats(totalCoins + 10, visitedSites + 1, tokenCount + 2) }) {
                Text("Update Collectables")
            }

            Button(
                onClick = { navController.navigate("home") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Back to Home", color = Color.Black)
            }
        }
    }
}

// ✅ Clickable Cards with Working Pop-up
@Composable
fun InfoCard(title: String, value: String, iconRes: Int, backgroundColor: Color, description: String) {
    var showDialog by remember { mutableStateOf(false) } // Track dialog visibility

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable { showDialog = true }, // Open pop-up on click
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = value, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
            }

            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$title Icon",
                modifier = Modifier.size(90.dp)
            )
        }
    }

    // Pop-up Dialog for Description
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = title) },
            text = { Text(text = description) },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCollectableScreen() {
    val navController = rememberNavController()
    CollectableScreen(
        navController = navController,
        totalCoins = 100,
        visitedSites = 5,
        tokenCount = 3,
        updateStats = { _, _, _ -> }
    )
}
