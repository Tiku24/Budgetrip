package com.example.budgetrip.di

import com.example.budgetrip.data.network.BudgetripApi
import com.example.budgetrip.data.repository.HomeRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExtractTextDi {

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        return Firebase.ai(
            backend = GenerativeBackend.vertexAI()
        ).generativeModel("gemini-2.5-flash")
    }
}