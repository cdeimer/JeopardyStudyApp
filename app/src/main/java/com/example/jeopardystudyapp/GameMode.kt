package com.example.jeopardystudyapp

sealed class GameMode {
    // Default Infinite Mode
    object Random : GameMode()

    // Starred Mode
    object Starred : GameMode()

    // Future Modes will go here
}