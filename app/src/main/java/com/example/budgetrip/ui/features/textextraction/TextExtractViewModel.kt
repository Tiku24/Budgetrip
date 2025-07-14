package com.example.budgetrip.ui.features.textextraction

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetrip.data.network.ResultResource
import com.example.budgetrip.data.repository.TextExtractRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TextExtractViewModel @Inject constructor(private val repository: TextExtractRepository) : ViewModel() {
    private val _state = MutableStateFlow<TextExtractState>(TextExtractState.idel)
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<TextExtractEvent>()
    val event = _event.asSharedFlow()


    fun uploadImage(
        uri: Uri,
        context: Context,
    ) {
        viewModelScope.launch {
            _state.value = TextExtractState.Loading
            val filePart = prepareFilePart(context, uri)
            val res = repository.extractText(filePart = filePart)
            when(res){
                is ResultResource.Success -> {
                    _state.value = TextExtractState.Success(res.data)
                    Log.d("imageRes", "uploadImage: ${res.data}")
                }
                is ResultResource.Error -> {
                    _state.value = TextExtractState.Error(res.message)
                    Log.d("imageRes", "uploadImage: ${res.message}")
                }
                is ResultResource.Loading -> {
                    _state.value = TextExtractState.Loading
                }
            }
        }
    }

    private fun prepareFilePart(context: Context, uri: Uri): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: throw IOException("Failed to open stream")
        val fileBytes = inputStream.readBytes()
        val mimeType = contentResolver.getType(uri) ?: "image/*"
        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
        Log.d("UPLOAD", "mimeType=$mimeType")
        Log.d("UPLOAD", "fileSize=${fileBytes.size}")

        val cursor = contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: -1
        cursor?.moveToFirst()
        val fileName = if (nameIndex != -1) cursor?.getString(nameIndex) else "upload.jpg"
        cursor?.close()

        return MultipartBody.Part.createFormData("image", fileName ?: "upload.jpg", requestBody)
    }
}