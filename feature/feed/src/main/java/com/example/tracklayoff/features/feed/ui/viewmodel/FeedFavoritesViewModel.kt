package com.example.tracklayoff.features.feed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracklayoff.core.common.user.UserSession
import com.example.tracklayoff.core.favorites.domain.*
import com.example.tracklayoff.features.feed.ui.state.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedFavoritesViewModel @Inject constructor(
    observe: ObserveFavoritesUseCase,
    private val setFavorite: SetFavoriteUseCase,
    private val session: UserSession
) : ViewModel() {
    private val writing = MutableStateFlow<Set<Pair<String, String>>>(emptySet())
    private val events = Channel<FeedFavoriteEvent>(Channel.BUFFERED)
    val uiEvents = events.receiveAsFlow()
    val uiState = combine(observe(), writing) { snapshot, writes ->
        FeedFavoritesUiState(snapshot.ownerUserId, (snapshot.companies.keys + writes.filter { it.first == snapshot.ownerUserId }.map { it.second }).associateWith { companyId ->
            val state = snapshot.companies[companyId] ?: FavoriteState()
            FavoriteCardUiState(state.isSaved, (snapshot.ownerUserId to companyId) in writes, state.sync)
        }.toImmutableMap())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FeedFavoritesUiState())

    fun toggle(companyId: String) {
        val userId = session.currentUserId()
        if (userId == null) {
            events.trySend(FeedFavoriteEvent.SignInRequired)
            return
        }
        val state = uiState.value
        if (state.ownerUserId != userId) return
        val key = userId to companyId
        if (key in writing.value) return
        val saved = state.cards[companyId]?.isSaved != true
        // Set the guard before launching, so repeated events cannot race the local commit.
        writing.update { it + key }
        viewModelScope.launch {
            try {
                when (setFavorite(companyId, saved, userId)) {
                    FavoriteWriteResult.SavedLocally -> Unit
                    FavoriteWriteResult.AuthenticationRequired -> if (session.currentUserId() == userId) events.send(FeedFavoriteEvent.SignInRequired)
                    FavoriteWriteResult.StorageFailure -> if (session.currentUserId() == userId) events.send(FeedFavoriteEvent.LocalWriteFailed)
                }
            } finally { writing.update { it - key } }
        }
    }
}
