package com.example.jeopardystudyapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GameControls(
    isAnswerVisible: Boolean,
    onReveal: () -> Unit,
    onCorrect: () -> Unit,
    onWrong: () -> Unit,
    onSkip: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (!isAnswerVisible) {
            // State 1: Just show "Reveal"
            Button(
                onClick = onReveal,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060CE9))
            ) {
                Text("REVEAL ANSWER")
            }
        } else {
            // State 2: Scoring Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Wrong Button (Red)
                Button(
                    onClick = onWrong,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("MISSED IT") // X
                }

                // Correct Button (Green)
                Button(
                    onClick = onCorrect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("GOT IT") // Check
                }
            }

            // Skip Button (Secondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SKIP / NO SCORE")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ControlsPreview() {
    Column {
        Text("Hidden:")
        GameControls(false, {}, {}, {}, {})
        Spacer(modifier = Modifier.height(20.dp))
        Text("Revealed:")
        GameControls(true, {}, {}, {}, {})
    }
}