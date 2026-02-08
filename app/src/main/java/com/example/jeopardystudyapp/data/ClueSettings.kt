package com.example.jeopardystudyapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "clue_settings",
    foreignKeys = [
        ForeignKey(
            entity = Clue::class,
            parentColumns = ["id"],
            childColumns = ["clue_id"],
            onDelete = ForeignKey.CASCADE // If clue is deleted, delete its settings
        )
    ]
)
data class ClueSettings(
    @PrimaryKey
    @ColumnInfo(name = "clue_id")
    val clueId: Int,

    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean = false,

    @ColumnInfo(name = "notes")
    val notes: String? = null
)