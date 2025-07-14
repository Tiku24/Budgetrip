package com.example.budgetrip.ui.features.home

sealed class HomeEvent {
    data class showErrorMsg(val message: String) : HomeEvent()
    data class onUpdatedTrip(val message: String) : HomeEvent()
    data class onDeleteTrip(val message: String) : HomeEvent()
}