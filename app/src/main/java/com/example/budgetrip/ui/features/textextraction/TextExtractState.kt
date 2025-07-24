package com.example.budgetrip.ui.features.textextraction

import com.example.budgetrip.data.model.ReceiptItem
import com.google.firebase.ai.type.Candidate
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.InlineDataPart


sealed class TextExtractState {
    object Idle : TextExtractState()
    object Loading : TextExtractState()
    data class Success(val data: List<ReceiptItem>) : TextExtractState()
    data class Error(val message: String) : TextExtractState()
}