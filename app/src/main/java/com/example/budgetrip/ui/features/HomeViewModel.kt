package com.example.budgetrip.ui.features

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetrip.data.network.ResultResource
import com.example.budgetrip.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository): ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            val data = repository.getTrips()
            when(data) {
                is ResultResource.Success -> {
                    _state.value = HomeState.Success(data.data)
                    Log.d("data", "fetchData: ${data.data}")
                }
                is ResultResource.Error -> {
                    _state.value = HomeState.Error(data.message)
                    Log.d("error", "fetchData: ${data.message}")
                    _event.emit(HomeEvent.showErrorMessage(data.message))
                }
                is ResultResource.Loading -> {
                    _state.value = HomeState.Loading
                }
            }
        }
    }
}