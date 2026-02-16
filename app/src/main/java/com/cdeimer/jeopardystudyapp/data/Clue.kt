package com.cdeimer.jeopardystudyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "jeopardy",
    indices = [
        Index(value = ["category"], name = "ix_category"),
        Index(value = ["show_number"], name = "ix_show_number"),
        Index(value = ["value"], name = "ix_value")
    ])
data class Clue(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "show_number") val showNumber: String?,
    @ColumnInfo(name = "air_date") val airDate: String?,
    @ColumnInfo(name = "round") val round: String?,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "value") val value: Int?,
    @ColumnInfo(name = "question") val question: String?, // The "Clue" text
    @ColumnInfo(name = "answer") val answer: String?      // The Solution
)