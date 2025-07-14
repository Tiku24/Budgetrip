package com.example.budgetrip.di

import com.example.budgetrip.data.network.BudgetripApi
import com.example.budgetrip.data.network.TextExtractApi
import com.example.budgetrip.data.repository.HomeRepository
import com.example.budgetrip.data.repository.TextExtractRepository
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
object ExtractTextDi {

    @Provides
    @Singleton
    @Named("TextExtract")
    fun provideRetrofitForTextExtract(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.ocr.space")  //10.140.163.103
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideTextExtractApi(@Named("TextExtract") retrofit: Retrofit): TextExtractApi {
        return retrofit.create(TextExtractApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRepo(textExtractApi: TextExtractApi): TextExtractRepository {
        return TextExtractRepository(textExtractApi)
    }
}