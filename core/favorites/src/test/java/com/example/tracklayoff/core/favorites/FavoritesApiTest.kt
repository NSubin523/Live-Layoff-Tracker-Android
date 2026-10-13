package com.example.tracklayoff.core.favorites

import com.example.tracklayoff.core.favorites.data.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavoritesApiTest {
    @Test fun explicitStateAndMembershipMatchSpringWireContract() = runTest {
        MockWebServer().use { server ->
            val api = Retrofit.Builder().baseUrl(server.url("/api/v1/"))
                .addConverterFactory(GsonConverterFactory.create()).build().create(FavoritesApi::class.java)
            server.enqueue(MockResponse().setBody("{\"companyId\":\"c\",\"saved\":false}"))
            assertEquals(FavoriteResponseDto("c", false), api.setSaved("Bearer account-a", "c", SetFavoriteRequestDto(false)))
            server.takeRequest().apply {
                assertEquals("PUT", method); assertEquals("/api/v1/favorites/c", path)
                assertEquals("Bearer account-a", getHeader("Authorization"))
                assertEquals("{\"saved\":false}", body.readUtf8())
            }
            server.enqueue(MockResponse().setBody("[\"c\"]"))
            assertEquals(listOf("c"), api.getIds("Bearer account-a"))
            assertEquals("/api/v1/favorites/ids", server.takeRequest().path)
        }
    }
    @Test fun validCompanyMappingPreservesNullableImpact() {
        val dto = FavoriteCompanyDto("c", "Acme", null, "Confirmed", "Tech", "NY", "2026-10-12T12:00:00Z", null, "stable")
        assertEquals("Acme", dto.toEntity()!!.companyName)
        assertNull(dto.toEntity()!!.impactCount)
        assertNull(dto.copy(reportedAt = "invalid").toEntity())
        assertNull(dto.copy(id = "").toEntity())
        assertNull(dto.copy(companyName = null).toEntity())
    }
}
