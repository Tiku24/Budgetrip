package com.example.budgetrip.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgetrip.ui.features.home.HomeViewModel

@Composable
fun CustomDialog(
    onDismiss: () -> Unit,
    onConfirm:() -> Unit,
    vm: HomeViewModel
) {
    var name = vm.name.collectAsStateWithLifecycle()
    val destination = vm.destination.collectAsStateWithLifecycle()
    val totalBudget = vm.totalBudget.collectAsStateWithLifecycle()
    val spentAmount = vm.spentAmount.collectAsStateWithLifecycle()
    val addSpending = vm.addSpending.collectAsStateWithLifecycle()
    val category = vm.category.collectAsStateWithLifecycle()
    val startDate = vm.startDate.collectAsStateWithLifecycle()
    val endDate = vm.endDate.collectAsStateWithLifecycle()
    val notes = vm.notes.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = {
        onDismiss()
    },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
        ) {
            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(15.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text(if (vm.isUpdateDialogShown.value) "Update Trip" else "Add Trip", style = MaterialTheme.typography.titleLarge)
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name.value,
                    onValueChange = {
                        vm.onNameChange(it)
                    },
                    label = "Enter Trip Name"
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = destination.value,
                    onValueChange = {
                        vm.onDestinationChange(it)
                    },
                    label = "Enter Destination"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CustomTextField(
                        modifier = Modifier.height(70.dp).weight(1f),
                        value = totalBudget.value,
                        onValueChange = {
                            vm.onTotalBudgetChange(it)
                        },
                        label = "Total Budget",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    CustomTextField(
                        modifier = Modifier.height(70.dp).weight(1f),
                        value = addSpending.value.toString(),
                        onValueChange = {
                            vm.onAddSpending(it.toInt())
                        },
                        label = "Spent Amount",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = category.value,
                    onValueChange = {
                        vm.onCategoryChange(it)
                    },
                    label = "Enter Category"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CustomTextField(
                        modifier = Modifier.height(70.dp).weight(1f),
                        value = startDate.value,
                        onValueChange = {
                            vm.onStartDateChanged(it)
                        },
                        label = "Start Date",
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Start Date",
                                modifier = Modifier.clickable{
                                    vm.isStartDatePickerShown.value = true
                                }
                            )
                        }
                    )
                    CustomTextField(
                        modifier = Modifier.height(70.dp).weight(0.9f),
                        value = endDate.value,
                        readOnly = true,
                        onValueChange = {
                            vm.onEndDateChanged(it)
                        },
                        label = "End Date",
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Start Date",
                                modifier = Modifier.clickable{
                                    vm.isEndDatePickerShown.value = true
                                }
                            )
                        }
                    )
                }
                CustomTextField(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    value = notes.value,
                    onValueChange = {
                        vm.onNotesChange(it)
                    },
                    label = "Add any note about your trip...",
                    maxLines = 5,
                    singleLine = false
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        onDismiss()
                    }, colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.background)) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        onConfirm()
                    },colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.background)) {
                        Text(if (vm.isUpdateDialogShown.value) "Update" else "Create Trip")
                    }
                }
            }
        }
    }
}