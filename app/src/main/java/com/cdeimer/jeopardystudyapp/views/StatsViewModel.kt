package com.cdeimer.jeopardystudyapp.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
            if (it.result == "CORRECT") correct = it.count
            total += it.count
        }
        val accuracy = if (total == 0) 0 else ((correct.toFloat() / total) * 100).toInt()
        return Pair(total, accuracy)
    }
}