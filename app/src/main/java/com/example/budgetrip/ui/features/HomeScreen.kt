package com.example.budgetrip.ui.features

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.budgetrip.data.model.BudgetripResponseItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(vm: HomeViewModel,modifier: Modifier) {

    LaunchedEffect(true) {
        vm.event.collectLatest {
            when (it) {
                is HomeEvent.showErrorMessage -> {

                }
            }
        }
    }

    val state = vm.state.collectAsStateWithLifecycle()
    when (state.value) {
        is HomeState.Loading -> {
            CircularProgressIndicator()
        }

        is HomeState.Success -> {
            val data = (state.value as HomeState.Success).data
            LazyColumn(modifier) {
                items(data) {
                    TripCardSection(item = it)
                }
            }
        }

        is HomeState.Error -> {
            val errorMessage = (state.value as HomeState.Error).message

        }
    }
}

@Composable
fun TripCardSection(item: BudgetripResponseItem) {
    Card {
        Text(text = item.name)
    }
}