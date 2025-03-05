package com.example.gameui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
            .background(Color(0xFFFFEB3C)) // Yellow background
            .padding(8.dp),
        contentAlignment = Alignment.BottomCenter   // aligning the content including all the cards

    ) {

        Column(

            modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)    // adding spaces between the cards

        ) {

            Text(
                text = "Quest",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black

            )

            Spacer(modifier = Modifier.height(30.dp))   // adding the space between first card and the tittle

            // Creating the 3 main game level cards

            LevelCard("Beginner", 5, R.drawable.login)  // Replace image
            Spacer(modifier = Modifier.height(16.dp))
            LevelCard("Explorer", 7, R.drawable.login)  // Replace image
            Spacer(modifier = Modifier.height(16.dp))
            LevelCard("Expert", 10, R.drawable.login)  // Replace image
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun LevelCard(difficulty: String, sites: Int, imageRes: Int) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Row(

            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            //  Adding the Character Image

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

                Spacer(modifier = Modifier.height(4.dp))

                // Tokens (Commented till corrected  images adding)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Tokens: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
//                    Image(painter = painterResource(id = R.drawable.blue_token), contentDescription = "Blue Token", modifier = Modifier.size(24.dp))
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Image(painter = painterResource(id = R.drawable.red_token), contentDescription = "Red Token", modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Rewards (Commented till corrected images added )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Rewards: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
//                    Image(painter = painterResource(id = R.drawable.reward_icon), contentDescription = "Reward", modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGameLevelScreen() {
    GameLevelScreen()
}
