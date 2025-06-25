// src/main/java/com/example/rifas/presentation/viewmodel/UiState.kt
package com.example.rifas.presentation.viewmodel

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}
