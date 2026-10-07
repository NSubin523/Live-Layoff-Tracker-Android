package com.example.tracklayoff.features.chat.data.remote

import com.example.tracklayoff.core.network.LayoffTrackerBaseUrl
import com.example.tracklayoff.features.chat.data.dto.ChatRequestDto
import com.example.tracklayoff.features.chat.data.dto.ChatStreamEventDto
import com.example.tracklayoff.features.chat.di.ChatStreamingClient
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.IOException
import javax.inject.Inject

internal class ChatHttpException(val status: Int) : IOException("Chat HTTP $status")

class ChatStreamDataSource @Inject constructor(
    @ChatStreamingClient private val client: OkHttpClient,
    @LayoffTrackerBaseUrl private val baseUrl: String,
    private val parser: ChatStreamEventParser,
    private val gson: Gson
) {
    fun stream(prompt: String) = callbackFlow<ChatStreamEventDto> {
        val request = Request.Builder()
            .url("${baseUrl.trimEnd('/')}/chat")
            .header("Accept", "text/event-stream")
            .post(gson.toJson(ChatRequestDto(prompt)).toRequestBody("application/json".toMediaType()))
            .build()
        val source = EventSources.createFactory(client).newEventSource(request, object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                try {
                    val event = parser.parse(type, data) ?: return
                    if (trySend(event).isFailure) {
                        eventSource.cancel()
                        return
                    }
                    if (event == ChatStreamEventDto.Done) {
                        close()
                        eventSource.cancel()
                    }
                } catch (error: Exception) {
                    close(error)
                    eventSource.cancel()
                }
            }

            override fun onClosed(eventSource: EventSource) { close() }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                close(if (response != null && !response.isSuccessful) {
                    ChatHttpException(response.code)
                } else {
                    t ?: IOException("Chat connection failed")
                })
            }
        })
        awaitClose { source.cancel() }
    }.buffer(Channel.UNLIMITED) // Preserve every text delta even during a burst.
}
