package com.example.budgetrip.ui.features.textextraction

sealed class TextExtractEvent {
    data class showErrorMessage(val message: String) : TextExtractEvent()
}