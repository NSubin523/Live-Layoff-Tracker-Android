package com.example.tracklayoff.features.feed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracklayoff.core.common.util.AppLifecycleTracker
import com.example.tracklayoff.core.network.NetworkObserver
import com.example.tracklayoff.core.network.NetworkResult
import com.example.tracklayoff.core.network.NetworkStatus
import com.example.tracklayoff.features.feed.data.repository.FeedRepository
import com.example.tracklayoff.features.feed.ui.state.FeedUiEvent
import com.example.tracklayoff.features.feed.ui.state.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val networkObserver: NetworkObserver,
    private val appLifecycleTracker: AppLifecycleTracker
): ViewModel() {
    private var lastFetchTimestamp: Long = 0L
    private val refreshThresholdMillis = 5 * 60 * 1000L

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing : StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    private val _networkError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FeedUiState> = combine(
        feedRepository.companiesStream,
        _isLoading,
        _networkError
    ) { companies, isLoading, error ->
        when {
            companies.isEmpty() && isLoading -> FeedUiState.Loading
            companies.isEmpty() && error != null -> FeedUiState.Error(error)
            else -> FeedUiState.Success(companies.toImmutableList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FeedUiState.Loading
    )

    private val _uiEvent = MutableSharedFlow<FeedUiEvent>()
    val uiEvent: SharedFlow<FeedUiEvent> = _uiEvent.asSharedFlow()

    init {
        observeNetworkTransition()
        observeAppLifecycleState()
        fetchFeedData()
    }

    private fun observeNetworkTransition() {
        viewModelScope.launch {
            networkObserver.networkStatus
                .drop(1)
                .collect { status ->
                    when(status) {
                        NetworkStatus.AVAILABLE -> {
                            _uiEvent.emit(
                                FeedUiEvent.ShowToast(
                                    "Network Available. Pull to refresh."
                                )
                            )
                        }
                        NetworkStatus.DISCONNECTED -> {
                            _uiEvent.emit(
                                FeedUiEvent.ShowToast(
                                    "Network Unavailable. Pull to refresh or try later."
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun observeAppLifecycleState() {
        viewModelScope.launch {
            appLifecycleTracker.isAppInForeground
                .drop(1)
                .collect { isForeground ->
                    if(isForeground) {
                        refreshFeedIfStale()
                    }
                }
        }
    }

    fun fetchFeedData(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _isLoading.value = true
            }
            _networkError.value = null

            try {
                val result = feedRepository.getFeed()
                if (result is NetworkResult.Error) {
                    _networkError.value = result.message.toString()
                } else if (result is NetworkResult.Success) {
                    lastFetchTimestamp = System.currentTimeMillis()
                }
            } finally {
                _isRefreshing.value = false
                _isLoading.value = false
            }
        }
    }

    fun refreshFeedIfStale() {
        val currentTime = System.currentTimeMillis()
        val feedAgeMillis = currentTime - lastFetchTimestamp
        val bgDurationMillis = currentTime - appLifecycleTracker.lastBackgroundTimeStamp

        if (feedAgeMillis >= refreshThresholdMillis && bgDurationMillis >= refreshThresholdMillis) {
            fetchFeedData()
        }
    }
}