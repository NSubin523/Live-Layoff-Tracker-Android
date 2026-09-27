package com.example.tracklayoff.features.reporting.data.model

import com.google.gson.annotations.SerializedName


data class TelemetryEventDto(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("event_name")
    val eventName: String,

    @SerializedName("event_payload")
    val eventPayload: Map<String, String>? = null
)

data class TelemetryBatchRequest(
    @SerializedName("events")
    val events: List<TelemetryEventDto>
)
