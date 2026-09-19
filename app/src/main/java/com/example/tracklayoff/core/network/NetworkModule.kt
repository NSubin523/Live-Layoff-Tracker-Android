package com.example.tracklayoff.core.network

import com.example.tracklayoff.BuildConfig
import com.example.tracklayoff.core.common.util.DeviceDetector
import com.example.tracklayoff.features.feed.data.remote.FeedApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @LayoffTrackerBaseUrl
    fun provideLayoffTrackerBaseUrl(): String {
        if (!BuildConfig.DEBUG) {
            return BuildConfig.BASE_URL
        }
        return DeviceDetector.getDynamicBaseUrl(BuildConfig.MAC_IP, port = 8000)
    }

    @Provides
    @Singleton
    @FavoritesBaseUrl
    fun provideFavoritesBaseUrl(): String {
        if (!BuildConfig.DEBUG) {
            return BuildConfig.FAVORITES_BASE_URL
        }
        return DeviceDetector.getDynamicBaseUrl(BuildConfig.MAC_IP, port = 8081)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAppInterceptor(firebaseAuth: FirebaseAuth): AppInterceptor {
        return AppInterceptor(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        appInterceptor: AppInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(appInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @LayoffTrackerRetrofit
    fun provideLayoffTrackerRetrofit(@LayoffTrackerBaseUrl baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @FavoritesRetrofit
    fun provideFavoritesRetrofit(@FavoritesBaseUrl baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFeedApiService(@LayoffTrackerRetrofit retrofit: Retrofit): FeedApiService {
        return retrofit.create(FeedApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}