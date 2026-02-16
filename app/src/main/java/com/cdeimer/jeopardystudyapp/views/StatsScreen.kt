package com.cdeimer.jeopardystudyapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cdeimer.jeopardystudyapp.data.CategoryStat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(),
    onBack: () -> Unit
) {
    val lifetime by viewModel.lifetimeStats.collectAsState()
    val today by viewModel.todayStats.collectAsState()
    val bestCats by viewModel.bestCategories.collectAsState()
    val worstCats by viewModel.worstCategories.collectAsState()
    val graphData by viewModel.graphData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Stats") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF060CE9),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. SCORECARDS
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard("Lifetime", lifetime.first, lifetime.second, Modifier.weight(1f))
                    StatCard("Today", today.first, today.second, Modifier.weight(1f))
                }
            }

            // 2. GRAPH Section
            item {
                Text("Last 7 Days Accuracy", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // The Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp) // Little taller for labels
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
                ) {
                    // The Graph
                    LineGraph(
                        data = graphData,
                        modifier = Modifier.fillMaxSize().padding(bottom = 20.dp) // Padding for text
                    )
                }
            }

            // 3. CATEGORY LISTS
            item {
                Text("Weakest Categories", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
            }
            items(worstCats) { cat -> CategoryRow(cat) }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Best Categories", color = Color(0xFF4ECB71), fontWeight = FontWeight.Bold)
            }
            items(bestCats) { cat -> CategoryRow(cat) }
        }
    }
}

@Composable
fun StatCard(title: String, total: Int, accuracy: Int, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Text("$accuracy%", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("$total cards", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun CategoryRow(stat: CategoryStat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(4.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stat.category.uppercase(),
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        Text(
            text = "${stat.accuracy}% (${stat.right}/${stat.total})",
            color = if(stat.accuracy > 50) Color(0xFF4ECB71) else Color(0xFFFF6B6B),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}