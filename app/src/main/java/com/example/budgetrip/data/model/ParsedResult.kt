package com.example.budgetrip.data.model

data class ParsedResult(
    val ErrorDetails: String,
    val ErrorMessage: String,
    val FileParseExitCode: Int,
    val ParsedText: String,
    val TextOrientation: String,
    val TextOverlay: TextOverlay
)