package com.example.tracklayoff.features.chat.data.repository

import com.example.tracklayoff.core.common.di.IoDispatcher
import com.example.tracklayoff.features.chat.data.mapper.toDomain
import com.example.tracklayoff.features.chat.data.remote.*
import com.example.tracklayoff.features.chat.domain.model.*
import com.example.tracklayoff.features.chat.domain.repository.ChatRepository
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.format.DateTimeParseException
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatApiService,
    private val stream: ChatStreamDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ChatRepository {
    override suspend fun getHistory(before: String?, limit: Int): ChatResult<ChatHistoryPage> = withContext(ioDispatcher) {
        try {
            ChatResult.Success(api.getHistory(before, limit).toDomain())
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            ChatResult.Failure(error.toFailure())
        }
    }

    override fun sendMessage(prompt: String) = stream.stream(prompt)
        .map { it.toDomain() }
        .catch { error ->
            if (error is CancellationException) throw error
            emit(ChatStreamEvent.Error(error.toFailure()))
        }
}

private fun Throwable.toFailure(): ChatFailure = when (this) {
    is HttpException -> if (code() == 401 || code() == 403) ChatFailure.AuthenticationRequired else ChatFailure.Service
    is ChatHttpException -> if (status == 401 || status == 403) ChatFailure.AuthenticationRequired else ChatFailure.Service
    is JsonParseException, is IllegalArgumentException, is DateTimeParseException, is IllegalStateException -> ChatFailure.Protocol
    is IOException -> ChatFailure.Network
    else -> ChatFailure.Service
}
