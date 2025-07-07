package com.example.budgetrip.data.network

sealed class ResultResource<T> {
    data class Success<T>(val data: T) : ResultResource<T>()
    data class Error<T>(val message: String,val throwable: Throwable? = null, val data: T? = null) : ResultResource<T>()
    object Loading : ResultResource<Nothing>()
}