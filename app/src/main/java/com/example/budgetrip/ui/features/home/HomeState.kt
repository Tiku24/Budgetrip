package com.example.budgetrip.ui.features.home

import com.example.budgetrip.data.model.BudgetripResponse

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val data: BudgetripResponse) : HomeState()
    data class Error(val message: String) : HomeState()
}