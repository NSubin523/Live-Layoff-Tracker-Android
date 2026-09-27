package com.example.tracklayoff.features.reporting.data.api

import com.example.tracklayoff.features.reporting.data.model.TelemetryBatchRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportingApiService {
    @POST("telemetry/batch")
    suspend fun sendTelemetryBatch(
        @Body request: TelemetryBatchRequest
    )
}