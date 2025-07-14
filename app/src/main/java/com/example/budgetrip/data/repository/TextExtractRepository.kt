package com.example.budgetrip.data.repository

import com.example.budgetrip.data.model.ExtractResponse
import com.example.budgetrip.data.network.ResultResource
import com.example.budgetrip.data.network.TextExtractApi
import okhttp3.MultipartBody
import javax.inject.Inject

class TextExtractRepository @Inject constructor(private val textExtractApi: TextExtractApi) {
    suspend fun extractText(filePart: MultipartBody.Part): ResultResource<ExtractResponse> {
        return try {
            val response = textExtractApi.extractText(file = filePart)
            if (response.isSuccessful){
                ResultResource.Success(response.body()!!)
            }else{
                ResultResource.Error("Data Not Found")
            }
        }catch (e: Exception){
            ResultResource.Error("Check Internet Connection ${e.message}")
        }
    }
}