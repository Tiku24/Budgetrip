package com.example.budgetrip.data.repository

import com.example.budgetrip.data.model.BudgetripResponse
import com.example.budgetrip.data.network.BudgetripApi
import com.example.budgetrip.data.network.ResultResource

class HomeRepository(private val budgetripApi: BudgetripApi) {
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
    }