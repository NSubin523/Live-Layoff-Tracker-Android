package com.example.tracklayoff.features.chat.di

import com.example.tracklayoff.core.network.LayoffTrackerRetrofit
import com.example.tracklayoff.features.chat.data.remote.ChatApiService
import com.example.tracklayoff.features.chat.data.repository.ChatRepositoryImpl
import com.example.tracklayoff.features.chat.domain.repository.ChatRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChatStreamingClient

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatRepositoryModule {
    @Binds abstract fun bindChatRepository(implementation: ChatRepositoryImpl): ChatRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ChatNetworkModule {
    @Provides fun provideChatGson(): Gson = Gson()

    @Provides fun provideChatApi(@LayoffTrackerRetrofit retrofit: Retrofit): ChatApiService =
        retrofit.create(ChatApiService::class.java)

    @Provides
    @Singleton
    @ChatStreamingClient
    fun provideChatStreamingClient(client: OkHttpClient): OkHttpClient = client.newBuilder().apply {
        // BODY logging buffers an entire SSE response and delays every chunk.
        interceptors().removeAll { it is HttpLoggingInterceptor }
        networkInterceptors().removeAll { it is HttpLoggingInterceptor }
        retryOnConnectionFailure(false) // POST may already have saved a message.
        readTimeout(90, TimeUnit.SECONDS)
        callTimeout(0, TimeUnit.SECONDS)
    }.build()
}
