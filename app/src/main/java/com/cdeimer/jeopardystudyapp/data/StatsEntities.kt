package com.cdeimer.jeopardystudyapp.data

import androidx.room.ColumnInfo

// 1. Holds simple counts (e.g., "CORRECT": 45, "INCORRECT": 12)
data class LogStat(
    @ColumnInfo(name = "result") val result: String,
    @ColumnInfo(name = "count") val count: Int
)

// 2. Holds complex category data
data class CategoryStat(
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "rightCount") val right: Int,
    @ColumnInfo(name = "wrongCount") val wrong: Int
) {
    val total: Int get() = right + wrong
    val accuracy: Int get() = if (total == 0) 0 else ((right.toFloat() / total) * 100).toInt()
}