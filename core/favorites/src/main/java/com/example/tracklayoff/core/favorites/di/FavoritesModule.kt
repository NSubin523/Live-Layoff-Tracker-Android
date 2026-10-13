package com.example.tracklayoff.core.favorites.di

import com.example.tracklayoff.core.favorites.data.*
import com.example.tracklayoff.core.favorites.domain.*
import com.example.tracklayoff.core.favorites.sync.WorkManagerFavoritesScheduler
import com.example.tracklayoff.core.network.AppInterceptor
import com.example.tracklayoff.core.network.SpringBaseUrl
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesBindings {
    @Binds abstract fun repository(implementation: RoomFavoritesRepository): FavoritesRepository
    @Binds abstract fun scheduler(implementation: WorkManagerFavoritesScheduler): FavoritesSyncScheduler
    @Binds abstract fun tokens(implementation: FirebaseFavoritesTokenProvider): FavoritesTokenProvider
}
@Module
@InstallIn(SingletonComponent::class)
object FavoritesNetworkModule {
    @Provides @Singleton
    fun api(client: OkHttpClient, @SpringBaseUrl baseUrl: String): FavoritesApi {
        val accountBoundClient = client.newBuilder().apply {
            // Every favorite request supplies the token for the outbox owner explicitly.
            // The general interceptor must not replace it with another account's token.
            interceptors().removeAll { it is AppInterceptor }
            retryOnConnectionFailure(false)
        }.build()
        return Retrofit.Builder().baseUrl(baseUrl).client(accountBoundClient)
            .addConverterFactory(GsonConverterFactory.create()).build().create(FavoritesApi::class.java)
    }
}
