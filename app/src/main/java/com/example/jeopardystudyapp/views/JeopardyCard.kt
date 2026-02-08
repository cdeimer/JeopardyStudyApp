package com.example.jeopardystudyapp.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontStyle
import com.example.jeopardystudyapp.data.Clue

@Composable
fun JeopardyCard(
    clue: Clue?,
    isAnswerVisible: Boolean,
    isStarred: Boolean,        // <--- NEW PARAMETER
    onToggleStar: () -> Unit,  // <--- NEW CALLBACK
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF060CE9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        // Use a Box so we can overlay the Star icon on top of the Column
        Box(modifier = Modifier.fillMaxSize()) {

            if (clue == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else {
                // --- 1. The Main Content (Same as before) ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = clue.category?.uppercase() ?: "UNKNOWN",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp) // Make room for star
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${clue.value ?: 0}",
                            color = Color(0xFFD69F4C),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Question
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = clue.question?.uppercase() ?: "",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )
                    }

                    // Answer Area
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAnswerVisible) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                HorizontalDivider(color = Color(0xFFD69F4C), thickness = 2.dp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = clue.answer ?: "",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // --- 2. The Star Button (Overlay) ---
                IconButton(
                    onClick = onToggleStar,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Pin to top-right
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isStarred) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star this clue",
                        tint = if (isStarred) Color(0xFFD69F4C) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
// --- TEST PREVIEW ---
// Use this to verify the UI without running the app!
@Preview(showBackground = true)
@Composable
fun JeopardyCardPreview() {
    val dummyClue = Clue(
        id = 1,
        showNumber = "4500",
        airDate = "2020-01-01",
        round = "Jeopardy!",
        category = "HISTORY",
        value = 400,
        question = "This founding father was the first Secretary of the Treasury.",
        answer = "Alexander Hamilton"
    )

    Column {
        Text("Hidden State (Not Starred):")
        JeopardyCard(
            clue = dummyClue,
            isAnswerVisible = false,
            isStarred = false, // <--- Pass 'false' to see the outline star
            onToggleStar = {}  // <--- Pass an empty action (does nothing in preview)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Revealed State (Starred):")
        JeopardyCard(
            clue = dummyClue,
            isAnswerVisible = true,
            isStarred = true,  // <--- Pass 'true' to see the filled gold star
            onToggleStar = {}
        )
    }
}