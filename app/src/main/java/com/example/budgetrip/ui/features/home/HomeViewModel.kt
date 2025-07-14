package com.example.budgetrip.ui.features.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetrip.data.model.AddTripRequest
import com.example.budgetrip.data.model.UpdateTripRequest
import com.example.budgetrip.data.network.ResultResource
import com.example.budgetrip.data.repository.HomeRepository
import com.example.budgetrip.ui.features.home.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository): ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state = _state.asStateFlow()

    var isDialogShown = mutableStateOf(false)
        private set

    var isStartDatePickerShown = mutableStateOf(false)
        private set
    var isEndDatePickerShown = mutableStateOf(false)
        private set

    var isUpdateDialogShown = mutableStateOf(false)
        private set

    var selectedTripId = mutableStateOf("")

    fun onDismissDialog() {
        isDialogShown.value = !isDialogShown.value
    }
    fun onUpdateDismissDialog() {
        isUpdateDialogShown.value = !isUpdateDialogShown.value
    }


    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _destination = MutableStateFlow("")
    val destination = _destination.asStateFlow()

    private val _totalBudget = MutableStateFlow("")
    val totalBudget = _totalBudget.asStateFlow()

    private val _spentAmount = MutableStateFlow("")
    val spentAmount = _spentAmount.asStateFlow()

    private val _category = MutableStateFlow("")
    val category = _category.asStateFlow()

    private val _startDate = MutableStateFlow("")
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow("")
    val endDate = _endDate.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes = _notes.asStateFlow()

    private val _addState = MutableStateFlow<HomeState>(HomeState.Loading)
    val addState = _addState.asStateFlow()

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    private val _addSpending = MutableStateFlow<Int>(0)
    val addSpending = _addSpending.asStateFlow()


    fun onAddSpending(spend: Int){
        _addSpending.value = spend
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onDestinationChange(destination: String) {
        _destination.value = destination
    }

    fun onTotalBudgetChange(totalBudget: String) {
        _totalBudget.value = totalBudget
    }

    fun onSpentAmountChange(spentAmount: String) {
        _spentAmount.value = spentAmount
    }

    fun onCategoryChange(category: String) {
        _category.value = category
    }

    fun endDateFormat(timeInMillis: Long?) {
        timeInMillis?.let {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Date(it)
            _endDate.value = format.format(date)
        }
    }

    fun onNotesChange(notes: String) {
        _notes.value = notes
    }


    fun onStartDateChanged(date: String){
        _startDate.value = date
    }
    fun onEndDateChanged(date: String){
        _endDate.value = date
    }

    fun startDateFormat(timeInMillis: Long?) {
        timeInMillis?.let {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Date(it)
            _startDate.value = format.format(date)
        }
    }

    fun clearFields() {
        _name.value = ""
        _destination.value = ""
        _totalBudget.value = ""
        _spentAmount.value = ""
        _category.value = ""
        _startDate.value = ""
        _endDate.value = ""
        _notes.value = ""
    }


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
                    _event.emit(HomeEvent.showErrorMsg(data.message))
                }
                is ResultResource.Loading -> {
                    _state.value = HomeState.Loading
                }
            }
        }
    }

     fun addTrip(){
        viewModelScope.launch {
            repository.addTrip(
                AddTripRequest(
                    name = _name.value,
                    destination = _destination.value,
                    totalBudget = _totalBudget.value.toDouble(),
                    spentAmount = _addSpending.value.toDouble(),
                    category = _category.value,
                    startDate = _startDate.value,
                    endDate = _endDate.value,
                    notes = _notes.value
                )
            )
            fetchData()
        }
    }

    fun updateTrip(id: String){
        viewModelScope.launch {
            val data = repository.updateTrip(
                id = id,
                UpdateTripRequest(
                    name = _name.value,
                    destination = _destination.value,
                    totalBudget = _totalBudget.value.toDouble(),
                    spentAmount = (_spentAmount.value.toDouble() + _addSpending.value),
                    category = _category.value,
                    startDate = _startDate.value,
                    endDate = _endDate.value,
                    notes = _notes.value
                )
            )
            when(data){
                is ResultResource.Loading -> {
                    _state.value = HomeState.Loading
                }
                is ResultResource.Success -> {
                    _event.emit(HomeEvent.onUpdatedTrip("Updated"))
                    _addSpending.value = 0
                    fetchData()
                }
                is ResultResource.Error -> {
                    _state.value = HomeState.Error(data.message)
                }
            }
        }
    }

    fun deleteTrip(id: String){
        viewModelScope.launch {
            val data = repository.deleteTrip(id)
            when(data){
                is ResultResource.Loading -> {
                    _state.value = HomeState.Loading
                }
                is ResultResource.Success -> {
                    _event.emit(HomeEvent.onDeleteTrip("Deleted"))
                    fetchData()
                }
                is ResultResource.Error -> {
                    _state.value = HomeState.Error(data.message)
                }
            }
        }
    }
}