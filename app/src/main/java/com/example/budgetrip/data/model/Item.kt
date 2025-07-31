package com.example.budgetrip.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val item_name: String,
    val price: String,
    val quantity: Int,
    val symbol: String
)