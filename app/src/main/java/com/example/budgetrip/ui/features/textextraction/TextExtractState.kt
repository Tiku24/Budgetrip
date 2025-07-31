package com.example.budgetrip.ui.features.textextraction

import com.example.budgetrip.data.model.TextExtractResponse


sealed class TextExtractState {
    object Idle : TextExtractState()
    object Loading : TextExtractState()
    data class Success(val data: TextExtractResponse) : TextExtractState()
    data class Error(val message: String) : TextExtractState()
}