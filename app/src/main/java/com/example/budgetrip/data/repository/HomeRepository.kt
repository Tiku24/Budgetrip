package com.example.budgetrip.data.repository

import android.net.http.HttpException
import com.example.budgetrip.data.model.AddTripRequest
import com.example.budgetrip.data.model.AddTripResponse
import com.example.budgetrip.data.model.BudgetripResponse
import com.example.budgetrip.data.model.UpdateTripRequest
import com.example.budgetrip.data.model.UpdateTripResponse
import com.example.budgetrip.data.network.BudgetripApi
import com.example.budgetrip.data.network.ResultResource
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class HomeRepository @Inject constructor(private val budgetripApi: BudgetripApi) {
    suspend fun getTrips(): ResultResource<BudgetripResponse> {
        return try {
            val response = budgetripApi.getTrips()
            if (response.isSuccessful){
                ResultResource.Success(response.body()!!)
            }else{
                ResultResource.Error("Data Not Found")
            }
        }catch (e: Exception){
            val error = when (e) {
                is UnknownHostException -> "No Internet Connection"
                is SocketTimeoutException -> "Server Timeout"
                is ConnectException -> "Server is unreachable"
                else -> "Something went wrong: ${e.localizedMessage ?: "Unknown error"}"
            }
            ResultResource.Error(error)
            }
        }

    suspend fun addTrip(addTripRequest: AddTripRequest): ResultResource<AddTripResponse> {
        return try {
            val response = budgetripApi.addTrips(addTripRequest)
            if (response.isSuccessful){
                ResultResource.Success(response.body()!!)
            }else{
                ResultResource.Error("Data Not Found")
            }
        }catch (e: Exception){
            ResultResource.Error("Check Internet Connection")
        }
    }

    suspend fun updateTrip(id: String, updateTripRequest: UpdateTripRequest) : ResultResource<UpdateTripResponse> {
        return try {
            val response = budgetripApi.updateTrip(id = id, updateTripRequest = updateTripRequest)
            if (response.isSuccessful){
                ResultResource.Success(response.body()!!)
            }else{
                ResultResource.Error("Data Not Found")
            }
        }catch (e: Exception){
            ResultResource.Error("Check Internet Connection")
        }
    }

    suspend fun deleteTrip(id: String): ResultResource<Unit>{
        return try {
            val response = budgetripApi.deleteTrip(id = id)
            if (response.isSuccessful){
                ResultResource.Success(response.body()!!)
            }else{
                ResultResource.Error("Data Not Found")
            }
        }catch (e: Exception){
            ResultResource.Error("Check Internet Connection")
        }
    }
}