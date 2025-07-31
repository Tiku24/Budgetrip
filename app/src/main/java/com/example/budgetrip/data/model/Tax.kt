package com.example.budgetrip.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Tax(
    val amount: String,
    val symbol: String
)