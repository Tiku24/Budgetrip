package com.example.budgetrip.ui.features.textextraction

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetrip.data.model.ReceiptItem
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TextExtractViewModel @Inject constructor(private val model: GenerativeModel) : ViewModel() {
    private val _state = MutableStateFlow<TextExtractState>(TextExtractState.Idle)
    val state = _state.asStateFlow()

    private val _isGranted = MutableStateFlow(false)
    val isGranted = _isGranted.asStateFlow()

    private val _event = MutableSharedFlow<TextExtractEvent>()
    val event = _event.asSharedFlow()

    fun setPermissionGranted(granted: Boolean){
        _isGranted.value = granted
    }

    fun getTextFormImages(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = TextExtractState.Loading
            val prompt = content {
                image(bitmap)
                text("You are an expert at extracting structured data from receipt images. Analyze this image carefully and extract all purchasable items.\n" +
                        "\n" +
                        "For each item found, you must identify:\n" +
                        "1. Item name/description\n" +
                        "2. Quantity (if not visible, assume 1)\n" +
                        "3. Price (unit price or total price for that item)\n" +
                        "\n" +
                        "Return results as a JSON array in this exact format:\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"item_name\": \"string\",\n" +
                        "    \"quantity\": \"string or number\", \n" +
                        "    \"price\": \"string (numeric value only)\",\n" +
                        "    \"symbol\": \"string or null\"\n" +
                        "  }\n" +
                        "]\n" +
                        "\n" +
                        "Extraction rules:\n" +
                        "- Only include actual purchased items, not fees, taxes, or totals\n" +
                        "- If quantity appears as \"2x\" or \"3 @\" format, extract just the number\n" +
                        "- For prices, include currency symbol if visible (like \$5.99 or €3.50)\n" +
                        "- Clean up item names but preserve essential details\n" +
                        "- If an item spans multiple lines, combine the text logically\n" +
                        "- Skip items missing any of the three required fields\n" +
                        "- Handle different receipt formats (grocery, restaurant, retail, etc.)\n" +
                        "\n" +
                        "Output only the JSON array structure, no other text, no code blocks, no markdown formatting, no ```json wrapper. Start your response directly with the opening square bracket.")
            }
            try {
                val response = model.generateContent(prompt)
                val items = Json.decodeFromString<List<ReceiptItem>>(response.text.toString())
                _state.value = TextExtractState.Success(items)
                Log.d("TAG", "getTextFormImages: ${response.text}")
            } catch (e: Exception){
                _state.value = TextExtractState.Error(e.message.toString())
            }
        }
    }
}