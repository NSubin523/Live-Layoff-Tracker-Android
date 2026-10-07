package com.example.tracklayoff.features.chat.data.remote

import com.example.tracklayoff.features.chat.data.dto.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

/** OkHttp handles SSE framing, including multiline data, before calling this parser. */
class ChatStreamEventParser @Inject constructor(private val gson: Gson) {
    fun parse(type: String?, data: String): ChatStreamEventDto? = when (type) {
        "intent" -> ChatStreamEventDto.Intent(
            IntentPayloadDto(requiredString(data, "intent"))
        )
        "cards" -> ChatStreamEventDto.Cards(
            requireNotNull(gson.fromJson<List<CompanyCardDto>>(
                data, object : TypeToken<List<CompanyCardDto>>() {}.type
            ))
        )
        "text" -> ChatStreamEventDto.Text(TextPayloadDto(requiredString(data, "chunk")))
        "error" -> ChatStreamEventDto.Error(ErrorPayloadDto(requiredString(data, "message")))
        "done" -> ChatStreamEventDto.Done // Backend sends the JSON literal null.
        else -> null // Future event types do not break a text-only client.
    }

    private fun requiredString(data: String, name: String): String {
        val value = JsonParser.parseString(data).asJsonObject.get(name)
        require(value != null && value.isJsonPrimitive && value.asJsonPrimitive.isString) {
            "Invalid $name payload"
        }
        return value.asString
    }
}
