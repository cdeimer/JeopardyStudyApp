package com.cdeimer.jeopardystudyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cdeimer.jeopardystudyapp.model.GameMode
import com.cdeimer.jeopardystudyapp.views.GameScreen
import com.cdeimer.jeopardystudyapp.views.GameViewModel
import com.cdeimer.jeopardystudyapp.views.StarredListScreen
import com.cdeimer.jeopardystudyapp.views.StatsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // We now have 3 screens: "home", "list", "game"
    var currentScreen by remember { mutableStateOf("home") }
    val viewModel: GameViewModel = viewModel()

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                onStartGame = { mode ->
                if (mode is GameMode.Starred) {
                    // If they chose Starred, go to List View first
                    currentScreen = "list"
                } else {
                    // If Random, go straight to Game
                    viewModel.setGameMode(GameMode.Random)
                    currentScreen = "game"
                }
            },
                onNavigateToStats = {
                    currentScreen = "stats"
                }
            )
        }
        "list" -> {
            StarredListScreen(
                viewModel = viewModel,
                onBack = { currentScreen = "home" },
                onPlay = {
                    // NOW we start the game in Starred Mode
                    viewModel.setGameMode(GameMode.Starred)
                    currentScreen = "game"
                }
            )
        }
        "game" -> {
            GameScreen(
                viewModel = viewModel,
                onBackHome = { currentScreen = "home" }
            )
        }
        "stats" -> {
            StatsScreen(
                onBack = { currentScreen = "home" }
            )
        }
    }
}
@Composable
fun HomeScreen(
        onStartGame: (GameMode) -> Unit,
        onNavigateToStats: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060CE9)), // Jeopardy Blue Background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "INFINITE\nJEOPARDY",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 44.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Start Button
        Button(
            onClick = { onStartGame(GameMode.Random) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(
                text = "START STUDYING",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Button 2: Study Starred
        Button(
            onClick = { onStartGame(GameMode.Starred) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD69F4C)),
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("STUDY STARRED", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { onNavigateToStats() }, // You need to update HomeScreen signature!
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("MY PROFILE", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}