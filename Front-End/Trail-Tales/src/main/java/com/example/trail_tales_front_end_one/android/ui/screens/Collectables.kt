package com.example.trail_tales_front_end_one.android.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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


    }
}

}