package com.example.budgetrip.ui.features.textextraction

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetrip.data.model.TextExtractResponse
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
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
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
                        "4. Tax information (if present)\n" +
                        "\n" +
                        "Return results as a JSON object in this exact format:\n" +
                        "{\n" +
                        "  \"items\": [\n" +
                        "    {\n" +
                        "      \"item_name\": \"string\",\n" +
                        "      \"quantity\": \"string or number\", \n" +
                        "      \"price\": \"string (like \\\"15.95\\\", \\\"3.50\\\") - always in quotes\",\n" +
                        "      \"symbol\": \"string or empty string \\\"\\\"\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"tax\": {\n" +
                        "    \"amount\": \"string (like \\\"2.50\\\") - always in quotes, or empty string \\\"\\\" if not found\",\n" +
                        "    \"symbol\": \"string or empty string \\\"\\\"\"\n" +
                        "  }\n" +
                        "}\n" +
                        "\n" +
                        "Extraction rules:\n" +
                        "- Only include actual purchased items, not fees, taxes, or totals in the items array\n" +
                        "- Extract tax information separately and include in the tax object\n" +
                        "- Tax can appear with various labels like \"Tax\", \"VAT\", \"GST\", \"Sales Tax\", etc.\n" +
                        "- If quantity appears as \"2x\" or \"3 @\" format, extract just the number\n" +
                        "- For prices, include currency symbol if visible (like \$5.99 or €3.50)\n" +
                        "- Clean up item names but preserve essential details\n" +
                        "- If an item spans multiple lines, combine the text logically\n" +
                        "- Skip items missing any of the three required fields\n" +
                        "- Handle different receipt formats (grocery, restaurant, retail, etc.)\n" +
                        "\n" +
                        "Output only the JSON object structure, no other text, no code blocks, no markdown formatting, no ```json wrapper. Start your response directly with the opening curly brace. Do NOT use backticks ``` anywhere in your response.")
            }
            try {
                val response = model.generateContent(prompt)
                val items = Json.decodeFromString<TextExtractResponse>(response.text.toString())
                _state.value = TextExtractState.Success(items)
                Log.d("TAG", "getTextFormImages: ${response.text}")
            } catch (e: Exception){
                val errorMessage = when (e) {
                    is UnknownHostException -> "No Internet Connection"
                    is SocketTimeoutException -> "Server Timeout"
                    is ConnectException -> "Cannot Reach Server"
                    is HttpException -> "Server Error: ${e.code()} ${e.message()}"
                    else -> "Can't able to process now try again later"
                }
                _state.value = TextExtractState.Error(errorMessage)
                Log.d("TAG", "getTextFormImages: ${e.message}")
            }
        }
    }
}