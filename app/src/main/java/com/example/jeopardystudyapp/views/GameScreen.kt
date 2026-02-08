package com.example.jeopardystudyapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(), // Auto-creates the VM
    onBackHome: () -> Unit
) {
    // 1. Listen to the Live Data
    val clue by viewModel.currentClue.collectAsState()
    val isAnswerVisible by viewModel.isAnswerVisible.collectAsState()
    val score by viewModel.score.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isStarred by viewModel.isStarred.collectAsState()
    val lastSeen by viewModel.lastSeenText.collectAsState()

    // 2. The Main Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Dark background for contrast
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- TOP BAR (Score & Exit) ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackHome) {
                Text("EXIT", color = Color.White)
            }

            Text(
                text = "SCORE: $$score",
                color = if (score >= 0) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        // --- MIDDLE (The Card) ---
        Box(
            modifier = Modifier.weight(1f), // Takes all available space
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && clue == null) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Only show if text exists
                    if (lastSeen != null) {
                        Text(
                            text = lastSeen!!,
                            color = Color.White.copy(alpha = 0.7f), // Subtle/Dim
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    JeopardyCard(
                        clue = clue,
                        isAnswerVisible = isAnswerVisible,
                        isStarred = isStarred,
                        onToggleStar = { viewModel.toggleStar() }
                    )
                }
            }
        }

        // --- BOTTOM (Controls) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            GameControls(
                isAnswerVisible = isAnswerVisible,
                onReveal = { viewModel.revealAnswer() },
                onCorrect = { viewModel.onCorrect() },
                onWrong = { viewModel.onWrong() },
                onSkip = { viewModel.onSkip() }
            )
        }
    }
}