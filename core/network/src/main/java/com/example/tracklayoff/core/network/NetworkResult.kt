package com.example.tracklayoff.core.network

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T): NetworkResult<T>
    data class Error(val exception: Throwable, val message: String?): NetworkResult<Nothing>
    data object Loading: NetworkResult<Nothing>
}