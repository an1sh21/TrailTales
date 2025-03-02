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
            .background(Color(0xFFFFEB3B)) // Yellow background
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character Image
               Image(
                   painter = painterResource(id = R.drawable.login), // Replace with actual drawable
                    contentDescription = "Character Image",
                    modifier = Modifier
                       .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    // Difficulty Level
                    Text(
                        text = "Difficulty: Beginner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sites
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sites: ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "5 sites",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4A017) // Gold color
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tokens
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Tokens: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
//                        Image(
////                            painter = painterResource(id = R.drawable.blue_token), // Replace with actual drawable
//                            contentDescription = "Blue Token",
//                            modifier = Modifier.size(24.dp)
//                        )
                        Spacer(modifier = Modifier.width(4.dp))
//                        Image(
//                            painter = painterResource(id = R.drawable.red_token), // Replace with actual drawable
//                            contentDescription = "Red Token",
//                            modifier = Modifier.size(24.dp)
//                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rewards
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Rewards: ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
//                        Image(
//                            painter = painterResource(id = R.drawable.reward_icon), // Replace with actual drawable
//                            contentDescription = "Reward",
//                            modifier = Modifier.size(24.dp)
//                        )
                    }
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
