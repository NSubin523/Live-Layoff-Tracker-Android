package com.example.tracklayoff.core.common.util

import com.example.tracklayoff.core.common.di.IoDispatcher
import com.example.tracklayoff.core.common.user.UserRepository
import com.example.tracklayoff.core.network.NetworkObserver
import com.example.tracklayoff.core.network.NetworkStatus
import com.example.tracklayoff.features.reporting.data.api.ReportingApiService
import com.example.tracklayoff.features.reporting.data.model.TelemetryBatchRequest
import com.example.tracklayoff.features.reporting.data.model.TelemetryEventDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelemetryQueue @Inject constructor(
    private val reportingApiService: ReportingApiService,
    private val userRepository: UserRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    private val networkObserver: NetworkObserver
) {
    data class PendingEvent(
        val eventName: String,
        val payload: Map<String, String>? = null
    )

    private val coroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val queue = ConcurrentLinkedQueue<PendingEvent>()
    private val flushMutex = Mutex()
    private val maxBatchSize = 5
    private val flushInterval = 60_000L

    @Volatile
    private var isConnectedToNetwork: Boolean = true

    init {
        observeNetwork()
        startPeriodicFlush()
    }

    private fun observeNetwork() {
        coroutineScope.launch {
            networkObserver.networkStatus.collectLatest { status ->
                val wasConnected = isConnectedToNetwork
                isConnectedToNetwork = (status == NetworkStatus.AVAILABLE)

                if (!wasConnected && isConnectedToNetwork && queue.isNotEmpty()) {
                    flush()
                }
            }
        }
    }

    fun enqueue(eventName: String, payload: Map<String, String>? = null) {
        queue.add(PendingEvent(eventName, payload))

        if (queue.size >= maxBatchSize) {
            coroutineScope.launch {
                flush()
            }
        }
    }

    private fun startPeriodicFlush() {
        coroutineScope.launch {
            while(true) {
                delay(flushInterval)
                flush()
            }
        }
    }

    suspend fun flush() {
        flushMutex.withLock {
            if(!isConnectedToNetwork || queue.isEmpty()) return

            val batch = mutableListOf<PendingEvent>()
            while (batch.size < maxBatchSize && queue.isNotEmpty()) {
                queue.poll()?.let { batch.add(it) }
            }

            if(batch.isEmpty()) return

            val requestDto = batch.map { event ->
                TelemetryEventDto(
                    userId = userRepository.getUserId(),
                    eventName = event.eventName,
                    eventPayload =  event.payload
                )
            }

            try {
                reportingApiService.sendTelemetryBatch(
                    TelemetryBatchRequest(events = requestDto)
                )
            } catch (ex: Exception) {
                batch.reversed().forEach { queue.add(it) }
            }
        }
    }
}