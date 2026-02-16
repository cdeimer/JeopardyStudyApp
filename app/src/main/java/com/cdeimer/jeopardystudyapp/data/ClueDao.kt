package com.cdeimer.jeopardystudyapp.data

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

    // 1. Lifetime Stats
    @Query("SELECT result, COUNT(*) as count FROM answer_log GROUP BY result")
    fun getLifetimeStats(): Flow<List<LogStat>>

    // 2. Today's Stats (filtered by time)
    @Query("SELECT result, COUNT(*) as count FROM answer_log WHERE timestamp >= :startOfDay GROUP BY result")
    fun getSessionStats(startOfDay: Long): Flow<List<LogStat>>

    // 3. Category Breakdown (Best/Worst)
    // We filter for categories with at least 3 attempts so 1 lucky guess doesn't make it "Best"
    @Query("""
        SELECT 
            c.category, 
            COUNT(CASE WHEN al.result = 'CORRECT' THEN 1 END) as rightCount,
            COUNT(CASE WHEN al.result = 'INCORRECT' THEN 1 END) as wrongCount
        FROM answer_log al
        JOIN jeopardy c ON al.clue_id = c.id
        GROUP BY c.category
        HAVING (rightCount + wrongCount) >= 1
        ORDER BY rightCount DESC, wrongCount ASC
    """)
    fun getCategoryStats(): Flow<List<CategoryStat>>

    // Get all logs since a specific time (for the graph)
    @Query("SELECT * FROM answer_log WHERE timestamp >= :cutoff ORDER BY timestamp ASC")
    fun getLogsSince(cutoff: Long): Flow<List<AnswerLog>>
}