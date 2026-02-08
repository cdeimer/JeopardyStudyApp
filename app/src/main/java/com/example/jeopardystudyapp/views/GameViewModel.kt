package com.example.jeopardystudyapp.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeopardystudyapp.data.AnswerLog
import com.example.jeopardystudyapp.data.AppDatabase
import com.example.jeopardystudyapp.data.Clue
import com.example.jeopardystudyapp.data.ClueSettings
import com.example.jeopardystudyapp.model.GameMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.clueDao()

    // --- STATE ---

    // 1. The Clue currently on screen
    private val _currentClue = MutableStateFlow<Clue?>(null)
    val currentClue: StateFlow<Clue?> = _currentClue.asStateFlow()

    // 2. The Next Clue (Buffered in background)
    private var nextClue: Clue? = null

    // 3. Is the answer currently visible?
    private val _isAnswerVisible = MutableStateFlow(false)
    val isAnswerVisible: StateFlow<Boolean> = _isAnswerVisible.asStateFlow()

    // 4. User Score
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    // 5. Loading State
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Starred State
    private val _isStarred = MutableStateFlow(false)
    val isStarred: StateFlow<Boolean> = _isStarred.asStateFlow()

    val starredList: StateFlow<List<Clue>> = dao.getAllStarredClues()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeStar(clueId: Int) {
        viewModelScope.launch {
            dao.insertSettings(
                ClueSettings(clueId = clueId, isStarred = false)
            )
        }
    }

    // Text from answer log
    private val _lastSeenText = MutableStateFlow<String?>(null)
    val lastSeenText: StateFlow<String?> = _lastSeenText.asStateFlow()

    // Game Mode Control
    private var currentGameMode: GameMode = GameMode.Random

    fun setGameMode(mode: GameMode) {
        currentGameMode = mode
        _currentClue.value = null
        nextClue = null
        _isStarred.value = false
        _lastSeenText.value = null
        startGame() // Reset and fetch new cards immediately
    }

    // 1. Call this whenever a new card loads (inside advanceToNextClue or startGame)
    private suspend fun checkLastSeen(clueId: Int) {
        val log = dao.getLastLog(clueId)
        if (log != null) {
            val timeAgo = formatRelativeTime(log.timestamp)
            _lastSeenText.value = "Last seen: $timeAgo (${log.result})"
        } else {
            _lastSeenText.value = null // Never seen before
        }
    }

    // 2. Call this when the user clicks buttons
    fun logAnswer(result: String) {
        val clue = _currentClue.value ?: return

        viewModelScope.launch {
            // Write to DB
            dao.insertLog(
                AnswerLog(
                    clueId = clue.id!!,
                    result = result,
                    timestamp = System.currentTimeMillis()
                )
            )
            // Trigger next card
            advanceToNextClue()
        }
    }

    // --- INIT ---
    init {
        startGame()
    }

    fun startGame() {
        viewModelScope.launch {
            _isLoading.value = true
            _score.value = 0

            // Fetch two clues immediately
            val first = fetchClueForMode()
            val second = fetchClueForMode()

            _currentClue.value = first
            nextClue = second

            first.id?.let { id ->
                checkStarStatus(id)
                checkLastSeen(id)
            }

            _isAnswerVisible.value = false
            _isLoading.value = false
        }
    }

    // --- USER ACTIONS ---

    fun revealAnswer() {
        _isAnswerVisible.value = true
    }

    fun onCorrect() {
        // Add value to score (default to 0 if null)
        val value = _currentClue.value?.value ?: 0
        _score.value += value
        logAnswer("CORRECT")
    }

    fun onWrong() {
        // Subtract value from score
        val value = _currentClue.value?.value ?: 0
        _score.value -= value
        logAnswer("INCORRECT")
    }

    fun onSkip() {
        // No score change
        logAnswer("SKIPPED")
    }

    // --- INTERNAL LOGIC ---

    // Helper function to check status when loading a clue
    private suspend fun checkStarStatus(clueId: Int) {
        val status = dao.isClueStarred(clueId) ?: false // Returns true/false or null
        _isStarred.value = status
    }

    // Helper function to format time from answer log
    private fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        // Simple conversion constants
        val oneMinute = 60 * 1000L
        val oneHour = 60 * oneMinute
        val oneDay = 24 * oneHour

        return when {
            diff < oneMinute -> "Just now"
            diff < oneHour -> "${diff / oneMinute}m ago"
            diff < oneDay -> "${diff / oneHour}h ago"
            else -> "${diff / oneDay}d ago"
        }
    }

    // Helper: Decides which clue to fetch based on the mode
    private suspend fun fetchClueForMode(): Clue {
        // 1. Try to get a clue for the current mode
        val clue = when (currentGameMode) {
            is GameMode.Random -> dao.getRandomClue()
            is GameMode.Starred -> dao.getRandomStarredClue()
        }

        // 2. SAFETY FALLBACK:
        // If we are in "Starred Mode" but the user has 0 stars, 'clue' will be null.
        // We fallback to a Random clue so the app doesn't freeze/crash.
        return clue ?: dao.getRandomClue()
    }

    private fun advanceToNextClue() {
        viewModelScope.launch {
            _isLoading.value = true

            // --- STEP 1: Determine the new Current Clue ---
            if (nextClue != null) {
                // Happy Path: Use the pre-fetched buffer
                _currentClue.value = nextClue
            } else {
                // Buffer Empty Path: Fetch immediately (blocking UI slightly)
                _currentClue.value = fetchClueForMode()
            }

            // CRITICAL: Update the Star Icon for the *newly visible* clue
            // We use safe calls (?.) so it doesn't crash if something went wrong
            _currentClue.value?.id?.let { id ->
                checkStarStatus(id)
                checkLastSeen(id)
            }

            // --- STEP 2: Reset UI State ---
            _isAnswerVisible.value = false
            _isLoading.value = false

            // --- STEP 3: Re-fill the Buffer ---
            // Fetch the *next* card in the background so it's ready for next time
            try {
                nextClue = fetchClueForMode()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleStar() {
        val clue = _currentClue.value ?: return
        val newStatus = !_isStarred.value

        viewModelScope.launch {
            // Update the UI instantly
            _isStarred.value = newStatus

            // Save to DB in background
            dao.insertSettings(
                ClueSettings(
                    clueId = clue.id!!,
                    isStarred = newStatus
                )
            )
        }
    }
}