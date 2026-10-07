package com.example.tracklayoff.features.chat.data.dto

import com.google.gson.annotations.SerializedName

data class ChatRequestDto(val message: String)
data class ChatHistoryResponseDto(
    val messages: List<ChatMessageDto>?,
    @SerializedName("has_more") val hasMore: Boolean?,
    @SerializedName("next_before") val nextBefore: String?
)
data class ChatMessageDto(
    @SerializedName("message_id") val messageId: String?,
    val role: String?,
    val text: String?,
    @SerializedName("created_at") val createdAt: String?
)
data class IntentPayloadDto(val intent: String?)
data class TextPayloadDto(val chunk: String?)
data class ErrorPayloadDto(val message: String?)
data class CompanyCardDto(
    @SerializedName("layoff_id") val layoffId: String?,
    @SerializedName("company_name") val companyName: String?
)

sealed interface ChatStreamEventDto {
    data class Intent(val payload: IntentPayloadDto) : ChatStreamEventDto
    data class Cards(val payload: List<CompanyCardDto>) : ChatStreamEventDto
    data class Text(val payload: TextPayloadDto) : ChatStreamEventDto
    data class Error(val payload: ErrorPayloadDto) : ChatStreamEventDto
    data object Done : ChatStreamEventDto
}
