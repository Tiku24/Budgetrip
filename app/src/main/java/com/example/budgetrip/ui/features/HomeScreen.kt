package com.example.budgetrip.ui.features

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgetrip.R
import com.example.budgetrip.data.model.BudgetripResponse
import com.example.budgetrip.data.model.BudgetripResponseItem
import com.example.budgetrip.ui.theme.BudgetripTheme
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
            HomeContent(data = data,modifier)
        }

        is HomeState.Error -> {
            val errorMessage = (state.value as HomeState.Error).message
            Text(errorMessage)
        }
    }
}

@Composable
fun HomeContent(data: BudgetripResponse,modifier: Modifier) {
    Column(modifier = modifier.padding(horizontal = 10.dp)) {
        TopSection()
        TripCartSection(data)
    }
}

@Composable
fun TripCartSection(data: BudgetripResponse) {

    LazyColumn() {
        items(data) {
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                val progress = it.spentAmount.toFloat() / it.totalBudget.toFloat()
                val remaining = it.totalBudget - it.spentAmount
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title + Tag + Icons Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "tag",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFF7B00),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.padding(start = 8.dp))
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.padding(start = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = it.destination, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    }



                    Spacer(modifier = Modifier.height(8.dp))

                    // Dates
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${it.startDate}  -  ${it.endDate}", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Budget Progress
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
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
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = it.notes, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    }
                }
            }
        }
    }
}


@Composable
fun TopSection() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 15.dp),horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Icon(painter = painterResource(R.drawable.trip_ic_icon),contentDescription = null, modifier = Modifier.size(25.dp))
            Text("Trip Budget Manager", style = MaterialTheme.typography.titleSmall)
        }
        Box(
            modifier = Modifier
                .size(50.dp)
                .clickable {

                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Add,contentDescription = null, tint = MaterialTheme.colorScheme.background)
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
@PreviewLightDark
fun Test() {
    BudgetripTheme {

    }
}