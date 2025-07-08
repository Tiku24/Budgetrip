package com.example.budgetrip.data.model

import java.time.LocalDate

data class AddTripRequest(
    val name:String,
    val destination: String,
    val totalBudget: Double,
    val spentAmount: Double,
    val category: String,
    val startDate: String,
    val endDate: String,
    val notes: String
)