package com.example.budgetrip.data.network

import com.example.budgetrip.data.model.AddTripRequest
import com.example.budgetrip.data.model.AddTripResponse
import com.example.budgetrip.data.model.BudgetripResponse
import com.example.budgetrip.data.model.UpdateTripRequest
import com.example.budgetrip.data.model.UpdateTripResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface BudgetripApi {

    @GET("/trips")
    suspend fun getTrips(): Response<BudgetripResponse>

    @POST("/trips")
    suspend fun addTrips(@Body addTripRequest: AddTripRequest): Response<AddTripResponse>

    @PATCH("/trips/{id}")
    suspend fun updateTrip(
        @Path("id") id: String,
        @Body updateTripRequest: UpdateTripRequest
    ): Response<UpdateTripResponse>

    @DELETE("/trips/{id}")
    suspend fun deleteTrip(@Path("id") id: String): Response<Unit>
}