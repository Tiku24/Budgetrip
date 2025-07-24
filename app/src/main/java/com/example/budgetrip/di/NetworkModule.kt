package com.example.budgetrip.di

import com.example.budgetrip.data.network.BudgetripApi
import com.example.budgetrip.data.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.180.219.215:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBudgetripApi(retrofit: Retrofit): BudgetripApi {
        return retrofit.create(BudgetripApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(budgetripApi: BudgetripApi): HomeRepository {
        return HomeRepository(budgetripApi)
    }
}