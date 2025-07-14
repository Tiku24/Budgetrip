package com.example.budgetrip.ui.features.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgetrip.R
import com.example.budgetrip.data.model.BudgetripResponse
import com.example.budgetrip.ui.features.home.HomeState
import com.example.budgetrip.ui.features.home.HomeViewModel
import com.example.budgetrip.ui.theme.BudgetripTheme
import com.example.budgetrip.ui.widgets.CustomDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(vm: HomeViewModel, modifier: Modifier) {
    val state = vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(true) {
        vm.fetchData()
        vm.event.collectLatest {
            when (it) {
                is HomeEvent.onDeleteTrip -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is HomeEvent.onUpdatedTrip -> {

                }
                is HomeEvent.showErrorMsg -> {

                }
            }
        }
    }



    when (state.value) {
        is HomeState.Loading -> {
            CircularProgressIndicator(modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(), color = MaterialTheme.colorScheme.primary)
        }

        is HomeState.Success -> {
            val data = (state.value as HomeState.Success).data
            HomeContent(data = data,modifier, vm = vm,context)
        }

        is HomeState.Error -> {
            val errorMessage = (state.value as HomeState.Error).message
            Text(errorMessage)
        }
    }
}

@Composable
fun HomeContent(data: BudgetripResponse, modifier: Modifier, vm: HomeViewModel, context: Context) {
    Column(modifier = modifier.padding(horizontal = 10.dp)) {
        TopSection(vm = vm)
        TripCartSection(data,vm,context)
    }
}

@Composable
fun TripCartSection(data: BudgetripResponse, vm: HomeViewModel, context: Context) {

    LazyColumn() {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            TotalSpendSection(data)
        }

        items(data, key = {it.id}) {
            Card(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .clickable {

                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                val progress = it.spentAmount.toFloat() / it.totalBudget.toFloat()
                var remaining = it.totalBudget - it.spentAmount

                Column(modifier = Modifier
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(16.dp)) {
                    // Title + Tag + Icons Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = it.category,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .background(
                                    color = dynamicColors(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Icon(imageVector = Icons.Outlined.ModeEdit, contentDescription = "Edit", modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable {
                                vm.isUpdateDialogShown.value = true
                                vm.selectedTripId.value = it.id
                                vm.onNameChange(it.name)
                                vm.onDestinationChange(it.destination)
                                vm.onTotalBudgetChange(it.totalBudget.toString())
                                vm.onSpentAmountChange(it.spentAmount.toString())
                                vm.onStartDateChanged(it.startDate)
                                vm.onEndDateChanged(it.endDate)
                                vm.onCategoryChange(it.category)
                                vm.onNotesChange(it.notes)
                            })
                        Icon(imageVector = Icons.Outlined.DeleteOutline, contentDescription = "Delete", modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable {
                                vm.deleteTrip(it.id)
                            })
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.PinDrop, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = it.destination, color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dates
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${it.startDate}  -  ${it.endDate}", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Budget Progress
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Budget Progress", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "${it.spentAmount} / ", style = MaterialTheme.typography.titleSmall)
                        Text(text = "${it.totalBudget}", style = MaterialTheme.typography.titleSmall)
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFF00C853),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Remaining: $remaining",
                            color = Color(0xFF00C853),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% used",
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notes
                    Box(modifier = Modifier
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.tertiary
                        )) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.InsertDriveFile,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = it.notes,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
    if (vm.isUpdateDialogShown.value){
        CustomDialog(
            onDismiss = {
                vm.isUpdateDialogShown.value = false
            }, onConfirm = {
                vm.updateTrip(id = vm.selectedTripId.value)
                vm.onUpdateDismissDialog()
            }, vm = vm)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSection(vm: HomeViewModel) {

    val scope = rememberCoroutineScope()

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp)
        ,horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Icon(painter = painterResource(R.drawable.trip_ic_icon),contentDescription = null, modifier = Modifier.size(30.dp), tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Trip Budget Manager", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.surfaceVariant)
        }
        Box(
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    vm.isDialogShown.value = true
                    vm.clearFields()
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Add,contentDescription = null, tint = MaterialTheme.colorScheme.background)
        }
    }
    if (vm.isDialogShown.value){
        CustomDialog(onDismiss = {
            vm.onDismissDialog()
        },
            onConfirm = {
                vm.onDismissDialog()
                scope.launch {
                    vm.addTrip()
                }
            },
            vm = vm
        )
    }

    if (vm.isStartDatePickerShown.value){
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                vm.isStartDatePickerShown.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.isStartDatePickerShown.value = false
                        vm.startDateFormat(datePickerState.selectedDateMillis)
                    },
                    enabled = confirmEnabled.value,
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.isStartDatePickerShown.value = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        }
    }

    if (vm.isEndDatePickerShown.value){
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                vm.isEndDatePickerShown.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.isEndDatePickerShown.value = false
                        vm.endDateFormat(datePickerState.selectedDateMillis)
                    },
                    enabled = confirmEnabled.value,
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.isEndDatePickerShown.value = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        }
    }
}

@Composable
fun TotalSpendSection(data: BudgetripResponse) {
    val totalBudget = data.sumOf { it.totalBudget }
    val spentAmount = data.sumOf { it.spentAmount }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            modifier = Modifier
                .height(80.dp)
                .weight(1f),
            elevation = CardDefaults.cardElevation(2.dp, hoveredElevation = 1.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onBackground)
        ){
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MonetizationOn,contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Total Budget", style = MaterialTheme.typography.bodyLarge)
                    Text("$totalBudget", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        Card(
            modifier = Modifier
                .height(80.dp)
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp, hoveredElevation = 1.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onBackground)
        ){
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ShoppingCart,contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Total Spend",style = MaterialTheme.typography.bodyLarge)
                    Text("$spentAmount",style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

fun dynamicColors(): Color {
     val colors = listOf(
        Color(0xFF2196F3), // blue
        Color(0xFFFF7B00), // orange
        Color(0xFF4CAF50), // green
        Color(0xFF673AB7)  // purple
    )
    return colors.random()
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
@PreviewLightDark
fun Test() {
    BudgetripTheme {

    }
}