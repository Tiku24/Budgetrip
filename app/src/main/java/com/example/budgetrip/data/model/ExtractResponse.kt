package com.example.budgetrip.data.model

data class ExtractResponse(
    val IsErroredOnProcessing: Boolean,
    val OCRExitCode: Int,
    val ParsedResults: List<ParsedResult>,
    val ProcessingTimeInMilliseconds: String,
    val SearchablePDFURL: String
)