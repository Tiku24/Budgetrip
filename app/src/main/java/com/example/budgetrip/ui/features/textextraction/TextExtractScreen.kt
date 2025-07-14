package com.example.budgetrip.ui.features.textextraction

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TextExtractScreen(modifier: Modifier,vm: TextExtractViewModel= hiltViewModel()) {

    LaunchedEffect(true) {
        vm.event.collectLatest {
            when (it) {
                is TextExtractEvent.showErrorMessage -> {

                }
            }
        }
    }
    val state = vm.state.collectAsStateWithLifecycle()
    when (state.value) {
        is TextExtractState.idel -> {
            UploadScreen(viewModel = vm,modifier = modifier)
        }

        is TextExtractState.Loading -> {
            CircularProgressIndicator()
        }

        is TextExtractState.Success -> {
            val data = (state.value as TextExtractState.Success).data
            Column(modifier) {
                Text("${extractDateAndItems(data.ParsedResults[0].ParsedText)}",color = Color.Red, fontSize = 20.sp)
            }
        }

        is TextExtractState.Error -> {
            val errorMessage = (state.value as TextExtractState.Error).message

        }
    }
}

@Composable
fun UploadScreen(viewModel: TextExtractViewModel,modifier: Modifier) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let { viewModel.uploadImage(context = context, uri = it) }
    }

    Column(modifier.padding(16.dp)) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Pick and Upload Image")
        }

    }
}

data class SimpleItem(
    val name: String,
    val quantity: Int,
    val price: String
)

data class ExtractedReceipt(
    val date: String,
    val items: List<SimpleItem>
)

fun extractDateAndItems(ocrText: String): ExtractedReceipt {
    val lines = ocrText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

    val dateRegex = Regex("""\d{2}/\d{2}/\d{4}""")
    val priceRegex = Regex("""\$\d+\.\d{2}""")
    val quantityRegex = Regex("""^\d+$""")

    val date = lines.find { it.matches(dateRegex) } ?: "Not available"

    val items = mutableListOf<SimpleItem>()

    for (i in lines.indices) {
        val line = lines[i]
        if (line.matches(priceRegex)) {
            val quantity = lines.getOrNull(i - 2)?.takeIf { it.matches(quantityRegex) }?.toIntOrNull() ?: 1
            val itemName = lines.getOrNull(i - 1)
                ?.takeIf { it.isNotBlank() && !it.matches(quantityRegex) && !it.contains("TOTAL", true) && it.any { c -> c.isLetter() } }

            if (itemName != null) {
                items.add(SimpleItem(name = itemName, quantity = quantity, price = line))
            }
        }
    }

    return ExtractedReceipt(
        date = date,
        items = items
    )
}
