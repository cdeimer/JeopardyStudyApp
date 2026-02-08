package com.example.jeopardystudyapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClueDao {
    // The magic query for your infinite scroll
    @Query(value = "SELECT * FROM jeopardy ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomClue(): Clue

    // Save/Update Settings (Stars/Notes)
    // "REPLACE" means if a row exists for this clue_id, overwrite it.
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertSettings(settings: ClueSettings)

    // Log an Answer
    // "IGNORE" is fine here since IDs are auto-generated, but REPLACE is safer.
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertLog(log: AnswerLog)

    // Get the most recent log entry for a clue
    @Query("SELECT * FROM answer_log WHERE clue_id = :clueId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastLog(clueId: Int): AnswerLog?

    // Check if a specific clue is starred
    @Query("SELECT is_starred FROM clue_settings WHERE clue_id = :clueId")
    suspend fun isClueStarred(clueId: Int): Boolean?

    // Get a random clue that is ALSO starred
    @Query("""
        SELECT * FROM jeopardy 
        INNER JOIN clue_settings ON jeopardy.id = clue_settings.clue_id 
        WHERE clue_settings.is_starred = 1 
        ORDER BY RANDOM() LIMIT 1
    """)
    suspend fun getRandomStarredClue(): Clue?

    // Get ALL starred clues for the list view
    @Query("""
        SELECT * FROM jeopardy 
        INNER JOIN clue_settings ON jeopardy.id = clue_settings.clue_id 
        WHERE clue_settings.is_starred = 1
        ORDER BY show_number DESC
    """)
    fun getAllStarredClues(): Flow<List<Clue>>
}