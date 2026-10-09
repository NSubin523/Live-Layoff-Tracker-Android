package com.example.tracklayoff.features.chat.data

import com.example.tracklayoff.features.chat.data.dto.*
import com.example.tracklayoff.features.chat.data.mapper.toDomain
import com.example.tracklayoff.features.chat.data.remote.ChatStreamEventParser
import com.example.tracklayoff.features.chat.domain.model.*
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class ChatMappingTest {
    private val gson = Gson()
    private val parser = ChatStreamEventParser(gson)

    @Test fun `history maps backend agent role and preserves cursor`() {
        val json = """{"messages":[{"message_id":"server-id","role":"agent","text":"Hello","created_at":"2026-10-06T10:00:00Z"}],"has_more":true,"next_before":"2026-10-06T10:00:00+00:00"}"""
        val page = gson.fromJson(json, ChatHistoryResponseDto::class.java).toDomain()
        assertEquals(ChatRole.ASSISTANT, page.messages.single().role)
        assertEquals("server-id", page.messages.single().id)
        assertEquals("2026-10-06T10:00:00+00:00", page.nextBefore)
    }

    @Test fun `text payload handles escapes and preserves whitespace`() {
        val event = parser.parse("text", """{"chunk":" world\n\"quoted\""}""")!!.toDomain()
        assertEquals(ChatStreamEvent.Text(" world\n\"quoted\""), event)
    }

    @Test fun `done null and unknown events are safe`() {
        assertEquals(ChatStreamEventDto.Done, parser.parse("done", "null"))
        assertNull(parser.parse("future_audio", "not json"))
    }

    @Test fun `card and error payloads map to domain events`() {
        val cards = parser.parse("cards", """[{"layoff_id":"id","company_name":"Meta"}]""")!!.toDomain()
        assertEquals(ChatStreamEvent.Cards(listOf(ChatCard("id", "Meta"))), cards)
        assertEquals(ChatStreamEvent.Error(ChatFailure.Service), parser.parse("error", """{"message":"server error"}""")!!.toDomain())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `non string text is rejected instead of coerced`() { parser.parse("text", """{"chunk":42}""") }

    @Test(expected = IllegalArgumentException::class)
    fun `missing text is rejected`() { parser.parse("text", "{}") }

    @Test(expected = IllegalArgumentException::class)
    fun `has more without cursor is rejected`() { ChatHistoryResponseDto(emptyList(), true, null).toDomain() }
}
