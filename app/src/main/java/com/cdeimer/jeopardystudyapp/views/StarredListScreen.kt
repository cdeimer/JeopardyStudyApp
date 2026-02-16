package com.cdeimer.jeopardystudyapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cdeimer.jeopardystudyapp.data.Clue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarredListScreen(
    viewModel: GameViewModel = viewModel(),
    onBack: () -> Unit,
    onPlay: () -> Unit
) {
    val starredClues by viewModel.starredList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Starred Clues (${starredClues.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF060CE9),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (starredClues.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onPlay,
                    containerColor = Color(0xFFD69F4C), // Gold
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Filled.PlayArrow, "Play Starred")
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->

        if (starredClues.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No starred clues yet.\nGo play Random Mode to find some!",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // The List
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(starredClues) { clue ->
                    StarredClueItem(
                        clue = clue,
                        onDelete = { viewModel.removeStar(clue.id!!) }
                    )
                }
            }
        }
    }
}

@Composable
fun StarredClueItem(clue: Clue, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clue Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = clue.category?.uppercase() ?: "UNKNOWN",
                    color = Color(0xFFD69F4C), // Gold
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = clue.question ?: "",
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Answer: ${clue.answer}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Un-star",
                    tint = Color.Gray
                )
            }
        }
    }
}