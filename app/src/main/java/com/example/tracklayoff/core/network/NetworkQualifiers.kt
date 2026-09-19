package com.example.tracklayoff.core.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LayoffTrackerRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FavoritesRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LayoffTrackerBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FavoritesBaseUrl
