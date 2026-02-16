package com.cdeimer.jeopardystudyapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "answer_log",
    foreignKeys = [
        ForeignKey(
            entity = Clue::class,
            parentColumns = ["id"],
            childColumns = ["clue_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AnswerLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "clue_id")
    val clueId: Int,

    @ColumnInfo(name = "result")
    val result: String, // "CORRECT", "INCORRECT", "SKIPPED"

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)