package com.example.budgetrip.data.model

data class BudgetripResponseItem(
    val category: String,
    val destination: String,
    val endDate: String,
    val id: String,
    val name: String,
    val notes: String,
    val spentAmount: Int,
    val startDate: String,
    val totalBudget: Int
)