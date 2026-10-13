package com.example.tracklayoff.core.favorites.data

import retrofit2.http.*

interface FavoritesApi {
    @PUT("favorites/{companyId}")
    suspend fun setSaved(@Header("Authorization") authorization: String, @Path("companyId") companyId: String, @Body body: SetFavoriteRequestDto): FavoriteResponseDto
    @GET("favorites/ids")
    suspend fun getIds(@Header("Authorization") authorization: String): List<String>
    @GET("favorites")
    suspend fun getCompanies(@Header("Authorization") authorization: String): List<FavoriteCompanyDto>
}
