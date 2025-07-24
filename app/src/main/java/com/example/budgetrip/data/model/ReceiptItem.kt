package com.example.budgetrip.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptItem(
    val item_name: String,
    val quantity: Int,
    val price: String,
    val symbol: String?
)