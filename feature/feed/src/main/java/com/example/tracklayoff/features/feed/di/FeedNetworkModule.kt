package com.example.tracklayoff.features.feed.di

import com.example.tracklayoff.core.network.SpringRetrofit
import com.example.tracklayoff.features.feed.data.remote.FeedApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedNetworkModule {

    @Provides
    @Singleton
    fun provideFeedApiService(@SpringRetrofit retrofit: Retrofit): FeedApiService {
        return retrofit.create(FeedApiService::class.java)
    }
}
