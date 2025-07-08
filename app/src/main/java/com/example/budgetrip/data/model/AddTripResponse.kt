package com.example.budgetrip.data.model

data class AddTripResponse(
    val category: String,
    val destination: String,
    val endDate: String,
    val id: Int,
    val name: String,
    val notes: String,
    val spentAmount: Double,
    val startDate: String,
    val totalBudget: Double
)