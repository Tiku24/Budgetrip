package com.example.budgetrip.data.repository

import com.example.budgetrip.data.model.AddTripRequest
import com.example.budgetrip.data.model.AddTripResponse
import com.example.budgetrip.data.model.BudgetripResponse
import com.example.budgetrip.data.model.UpdateTripRequest
import com.example.budgetrip.data.model.UpdateTripResponse
import com.example.budgetrip.data.network.BudgetripApi
import com.example.budgetrip.data.network.ResultResource
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
            ResultResource.Error("Check Internet Connection")
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