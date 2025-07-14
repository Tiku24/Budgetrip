package com.example.budgetrip.ui.features.textextraction

import com.example.budgetrip.data.model.ExtractResponse

sealed class TextExtractState {
    object idel : TextExtractState()
    object Loading : TextExtractState()
    data class Success(val data: ExtractResponse) : TextExtractState()
    data class Error(val message: String) : TextExtractState()
}