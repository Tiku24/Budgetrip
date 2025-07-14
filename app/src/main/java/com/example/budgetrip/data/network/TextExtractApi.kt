package com.example.budgetrip.data.network

import com.example.budgetrip.data.model.ExtractResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TextExtractApi {

    @Multipart
    @POST("/parse/image")
    suspend fun extractText(
        @Header("apikey") apiKey: String = "K87863632688957",
        @Part file: MultipartBody.Part
    ): Response<ExtractResponse>
}