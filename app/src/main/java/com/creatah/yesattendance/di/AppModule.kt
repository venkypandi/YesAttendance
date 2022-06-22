package com.creatah.yesattendance.di

import com.creatah.yesattendance.BuildConfig
import com.creatah.yesattendance.data.api.YesAttendanceApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideYesAttendanceApiService(retrofit: Retrofit): YesAttendanceApiService {
        return retrofit.create(YesAttendanceApiService::class.java)
    }

}