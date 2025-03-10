package com.example.trail_tales_front_end_one.android.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.trail_tales_front_end_one.android.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var totalCoins by remember { mutableStateOf(0) }
            var visitedSites by remember { mutableStateOf(0) }
            var tokenCount by remember { mutableStateOf(0) }

            CollectableScreen(
                totalCoins = totalCoins,
                visitedSites = visitedSites,
                tokenCount = tokenCount,
                updateStats = { coins, sites, tokens ->
                    totalCoins = coins
                    visitedSites = sites
                    tokenCount = tokens
                }
            )
        }
    }
}

@Composable
fun CollectableScreen(
    totalCoins: Int,
    visitedSites: Int,
    tokenCount: Int,
    updateStats: (Int, Int, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2E))
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
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

            // Creating Coins Card
            InfoCard(
                title = "Total Coins",
                value = "$totalCoins",
                iconRes = R.drawable.login, // have to replace
                backgroundColor = Color(0xFFFFD700),
                description = " "    // give a unique description for the total coins
            )

            //  Creating Visited Sites Card
            InfoCard(
                title = "Visited Sites",
                value = "$visitedSites",
                iconRes = R.drawable.login,   // have to replace
                backgroundColor = Color(0xFF4CAF50),
                description = " "   // give a unique description for the visited sites
            )

            //  Creating Tokens Card
            InfoCard(
                title = " Total Tokens",
                value = "$tokenCount",
                iconRes = R.drawable.login,   // have to Replace
                backgroundColor = Color(0xFF03A9F4),
                description = " "   //  give a unique description for the total tokens
            )

            // Button to simulate collectables
            Button(onClick = {
                updateStats(
                    totalCoins + 10,
                    visitedSites + 1,
                    tokenCount + 2
                )
            }) {
                Text("Update Collectables")
            }
        }
    }
}

// creating cards for displaying the preview as cards
@Composable
fun InfoCard(title: String, value: String, iconRes: Int, backgroundColor: Color,description: String) {
    var showDialog by remember {mutableStateOf(false)}  // tracking dialog visibility
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable{showDialog},   // open dialog when card is clicked
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

    // Creating the pop up that shows the description of the card 

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = { Text(text = description) },  // ONLY the description
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
fun PreviewUserCollectableScreen() {
    CollectableScreen(
        totalCoins = 100,
        visitedSites = 5,
        tokenCount = 3,
        updateStats = { _, _,_->}
        )
}