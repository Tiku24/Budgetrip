package com.example.budgetrip.data.network

import com.example.budgetrip.data.model.AddTripRequest
import com.example.budgetrip.data.model.AddTripResponse
import com.example.budgetrip.data.model.BudgetripResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BudgetripApi {

    @GET("/trips")
    suspend fun getTrips(): Response<BudgetripResponse>

    @POST("/trips")
    suspend fun addTrips(@Body addTripRequest: AddTripRequest): Response<AddTripResponse>
}