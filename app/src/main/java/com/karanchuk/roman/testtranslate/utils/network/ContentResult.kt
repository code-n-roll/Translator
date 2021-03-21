package com.karanchuk.roman.testtranslate.utils.network

sealed class ContentResult<out T> {
    data class Error(val error: String) : ContentResult<Nothing>()
    data class Success<T>(val content: T) : ContentResult<T>()
    data class Loading(val isLoading: Boolean, val text: String? = null) : ContentResult<Nothing>()

    fun get(): T? = when(this) {
        is Success -> content
        else -> null
    }
}