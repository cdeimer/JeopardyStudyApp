package com.cdeimer.jeopardystudyapp.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cdeimer.jeopardystudyapp.data.AnswerLog
import com.cdeimer.jeopardystudyapp.data.AppDatabase
import com.cdeimer.jeopardystudyapp.data.CategoryStat
import com.cdeimer.jeopardystudyapp.data.LogStat
import kotlinx.coroutines.flow.*
import java.util.Calendar

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).clueDao()

    // 1. Lifetime Accuracy
    val lifetimeStats = dao.getLifetimeStats()
        .map { calculateAccuracy(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Pair(0, 0)) // (Total, Accuracy%)

    // 2. Today's Accuracy
    val todayStats = dao.getSessionStats(getStartOfDay())
        .map { calculateAccuracy(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Pair(0, 0))

    // 3. Category Lists
    private val _categoryStats = dao.getCategoryStats()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // We split the single list into "Best" (High Accuracy) and "Worst" (Low Accuracy)
    val bestCategories = _categoryStats.map { list ->
        list.filter { it.accuracy >= 50 }.sortedByDescending { it.accuracy }.take(10)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val worstCategories = _categoryStats.map { list ->
        list.filter { it.accuracy < 50 }.sortedBy { it.accuracy }.take(10)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    data class DailyScore(
        val dayLabel: String, // e.g., "Mon"
        val accuracy: Float   // 0.0 to 100.0
    )

    // --- Helpers ---

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun calculateAccuracy(stats: List<LogStat>): Pair<Int, Int> {
        var correct = 0
        var total = 0

        stats.forEach {
            // Only count if it's explicitly RIGHT or WRONG
            if (it.result == "CORRECT") {
                correct = it.count
                total += it.count
            } else if (it.result == "INCORRECT") {
                total += it.count
            }
            // "SKIPPED" is now ignored
        }

        val accuracy = if (total == 0) 0 else ((correct.toFloat() / total) * 100).toInt()
        return Pair(total, accuracy)
    }

    // The Graph Data Source
    val graphData: StateFlow<List<DailyScore>> = dao.getLogsSince(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L)
        .map { logs -> processGraphData(logs) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private fun processGraphData(logs: List<AnswerLog>): List<DailyScore> {
        val today = Calendar.getInstance()
        val results = mutableListOf<DailyScore>()

        // Loop backwards for 7 days (6 days ago -> Today)
        for (i in 6 downTo 0) {
            val date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val dayStart = date.apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }.timeInMillis
            val dayEnd = dayStart + (24 * 60 * 60 * 1000)

            // Filter logs for this specific window
            val daysLogs = logs.filter { it.timestamp in dayStart until dayEnd }

            // Calculate Accuracy
            val right = daysLogs.count { it.result == "CORRECT" }
            val total = daysLogs.count { it.result == "CORRECT" || it.result == "INCORRECT" }
            val accuracy = if (total == 0) 0f else (right.toFloat() / total) * 100f

            // Create Label (e.g., "Mon")
            val label = java.text.SimpleDateFormat("EEE", java.util.Locale.US).format(date.time)

            results.add(DailyScore(label, accuracy))
        }
        return results
    }
}