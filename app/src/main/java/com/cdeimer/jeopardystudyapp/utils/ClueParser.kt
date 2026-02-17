package com.cdeimer.jeopardystudyapp.utils

import java.util.regex.Pattern

data class ParsedClue(
    val cleanText: String,
    val mediaUrl: String?
)

object ClueParser {
    // Regex to find <a href="...">text</a>
    // Capture Group 1: The URL
    // Capture Group 2: The inner text
    private val anchorPattern = Pattern.compile("<a\\s+href=\"([^\"]+)\"[^>]*>(.*?)</a>", Pattern.CASE_INSENSITIVE)

    fun parse(rawText: String): ParsedClue {
        val matcher = anchorPattern.matcher(rawText)

        if (matcher.find()) {
            val url = matcher.group(1) // Extract URL
            val innerText = matcher.group(2) // Extract "this state" or "Tate..."

            // Replace the whole <a> tag with just the inner text
            // e.g. "Say <a...>this state</a>" -> "Say this state"
            val cleanText = matcher.replaceAll("$2")

            return ParsedClue(cleanText, url)
        }

        // No media found, just return raw text
        return ParsedClue(rawText, null)
    }
}