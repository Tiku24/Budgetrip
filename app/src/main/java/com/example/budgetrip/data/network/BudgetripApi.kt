package com.example.budgetrip.data.network

import com.example.budgetrip.data.model.BudgetripResponse
import retrofit2.Response
import retrofit2.http.GET

interface BudgetripApi {

    @GET("/trips")
    suspend fun getTrips(): Response<BudgetripResponse>
}