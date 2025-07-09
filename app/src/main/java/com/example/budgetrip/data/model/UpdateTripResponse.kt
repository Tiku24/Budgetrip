package com.example.budgetrip.data.model

data class UpdateTripResponse(
    val category: String,
    val destination: String,
    val endDate: String,
    val id: String,
    val name: String,
    val notes: String,
    val spentAmount: Double,
    val startDate: String,
    val totalBudget: Double
)