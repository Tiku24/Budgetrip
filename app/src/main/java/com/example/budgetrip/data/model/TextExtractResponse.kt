package com.example.budgetrip.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TextExtractResponse(
    val items: List<Item>,
    val tax: Tax
)