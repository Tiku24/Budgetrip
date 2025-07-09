package com.example.budgetrip.data.model

data class UpdateTripRequest(
    val name:String,
    val destination: String,
    val totalBudget: Double,
    val spentAmount: Double,
    val category: String,
    val startDate: String,
    val endDate: String,
    val notes: String
)