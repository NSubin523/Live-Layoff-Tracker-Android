package com.example.tracklayoff.features.chat.data

import com.example.tracklayoff.features.chat.data.dto.ChatStreamEventDto
import com.example.tracklayoff.features.chat.data.dto.TextPayloadDto
import com.example.tracklayoff.features.chat.data.remote.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class ChatStreamDataSourceTest {
    private val server = MockWebServer()
    private val client = OkHttpClient.Builder().retryOnConnectionFailure(false).build()
    private val gson = Gson()
    private lateinit var source: ChatStreamDataSource

    @Before fun setup() {
        server.start()
        source = ChatStreamDataSource(client, server.url("/api/v1/").toString(), ChatStreamEventParser(gson), gson)
    }

    @After fun cleanup() {
        server.shutdown()
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }

    @Test fun `post SSE preserves burst chunks and uses shared base path`() = runBlocking {
        val body = (0 until 150).joinToString("") { "event: text\ndata: {\"chunk\":\"$it \"}\n\n" } +
            "event: done\ndata: null\n\n"
        server.enqueue(MockResponse().setHeader("Content-Type", "text/event-stream").setBody(body))
        val events = withTimeout(5000) { source.stream("Meta?").toList() }
        assertEquals(151, events.size)
        assertEquals(ChatStreamEventDto.Done, events.last())
        assertEquals((0 until 150).joinToString("") { "$it " }, events.filterIsInstance<ChatStreamEventDto.Text>().joinToString("") { it.payload.chunk!! })
        val request = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals("POST", request.method)
        assertEquals("/api/v1/chat", request.path)
        assertEquals("text/event-stream", request.getHeader("Accept"))
        assertEquals("Meta?", gson.fromJson(request.body.readUtf8(), com.example.tracklayoff.features.chat.data.dto.ChatRequestDto::class.java).message)
    }

    @Test fun `first chunk arrives before delayed done`() = runBlocking {
        // Throttled bytes force the response to stay open after the first event.
        val firstEvent = "event: text\ndata: {\"chunk\":\"Hello\"}\n\n"
        server.enqueue(MockResponse().setHeader("Content-Type", "text/event-stream")
            .setBody(firstEvent + "event: done\ndata: null\n\n")
            .throttleBody(firstEvent.toByteArray().size.toLong(), 3, TimeUnit.SECONDS))
        val first = withTimeout(1500) { source.stream("Meta?").first() }
        assertEquals(ChatStreamEventDto.Text(TextPayloadDto("Hello")), first)
    }

    @Test fun `http authentication failure is propagated without retrying post`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(401))
        try {
            withTimeout(5000) { source.stream("Meta?").toList() }
            fail("Expected authentication failure")
        } catch (error: ChatHttpException) {
            assertEquals(401, error.status)
        }
        assertEquals(1, server.requestCount)
    }

    @Test fun `malformed known event fails the stream`() = runBlocking {
        server.enqueue(MockResponse().setHeader("Content-Type", "text/event-stream")
            .setBody("event: text\ndata: {}\n\n"))
        try {
            withTimeout(5000) { source.stream("Meta?").toList() }
            fail("Expected invalid payload failure")
        } catch (_: IllegalArgumentException) { }
    }
}
