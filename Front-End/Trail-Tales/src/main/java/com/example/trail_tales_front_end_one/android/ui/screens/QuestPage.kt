package com.example.gameui

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.trail_tales_front_end_one.android.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameLevelScreen()
        }
    }
}

@Composable
fun GameLevelScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEB3C))    // Yellow background
            .padding(8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Quest",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))  // Space between the title and first card

            // Creating the 3 main game level cards with their own descriptions

            LevelCard(
                "Beginner", 5, R.drawable.login,   // replace the image when crated
                "Perfect for newcomers! Explore 5 beginner-friendly sites with simple challenges to get you started on your adventure."
            )
            Spacer(modifier = Modifier.height(16.dp))

            LevelCard(
                "Explorer", 7, R.drawable.login,    // replace the image when created
                "Ready for a challenge? This level takes you through 7 exciting locations, testing your skills and strategy."
            )
            Spacer(modifier = Modifier.height(16.dp))

            LevelCard(
                "Expert", 10, R.drawable.login,    // replace the image when created
                "Only for the brave! Tackle 10 intense locations filled with the toughest obstacles and grand rewards."
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LevelCard(difficulty: String, sites: Int, imageRes: Int, description: String) {
    var showDialog by remember { mutableStateOf(false) }    // State to track dialog visibility

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { showDialog = true },     // Show pop-up when the card is clicked
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Adding the Character Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Character Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                // Creating Difficulty Level

                Text(
                    text = "Difficulty: $difficulty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Creating Sites

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Sites: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$sites sites",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4A017) // Gold color
                    )
                }
            }
        }
    }

    // Show pop-up with description when card is clicked

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = { Text(text = description) },      // Only showing the description
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
fun PreviewGameLevelScreen() {
    GameLevelScreen()
}
