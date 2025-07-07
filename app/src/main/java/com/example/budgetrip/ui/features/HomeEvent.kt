package com.example.budgetrip.ui.features

sealed class HomeEvent {
    data class showErrorMessage(val message: String) : HomeEvent()
}