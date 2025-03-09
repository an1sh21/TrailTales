package com.example.trail_tales_front_end_one.android.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trail_tales_front_end_one.android.R
import kotlinx.coroutines.delay


@Composable
fun LandingScreen(navController: NavController){
    // Progress tracking for automatic navigation
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(100)
            progress += 0.05f
        }
    }

    Box (modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(R.drawable.login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp)
            ){
//                Text(
//                    text ="Trail Tales",
//                    style = TextStyle(
//                        fontSize = 76.sp,
//                        fontFamily = FontFamily.Cursive,
//                        fontWeight = FontWeight.Bold,
//                        fontStyle = FontStyle.Italic,
//                        color = Color.Black,
//                        letterSpacing = 3.sp,
//                        shadow = Shadow(
//                            color = Color.Black,
//                            blurRadius = 35f
//                        )
//                    ),
//                    modifier = Modifier.padding(top = 98.dp)      // getting the text down
//                )
            }
        }

        // Loading progress bar at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            LoadingBar()
        }
    }
}

@Composable
fun LoadingBar() {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(100)
            progress += 0.05f
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Loading...",
            fontSize = 40.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        LinearProgressIndicator(
            progress = progress,
            color = Color.Red,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(8.dp)
        )
    }
}


@Preview
@Composable
fun LandingScreenPreview(){
    LandingScreen(navController = rememberNavController())
}

//@Preview
//@Composable
//fun LoadingBarPreview() {
//    LoadingBar()
//}