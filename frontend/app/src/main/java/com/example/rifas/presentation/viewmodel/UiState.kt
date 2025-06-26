package com.example.rifas.presentation.viewmodel

/**
 * Represents the UI state for async operations.
 *
 * @param T The type of data associated with a successful result.
 */
sealed class UiState<out T> {
    /** Initial or reset state (no action yet). */
    object Idle : UiState<Nothing>()

    /** Indicates a loading or in-progress state. */
    object Loading : UiState<Nothing>()

    /** Represents a successful state holding the resulting data. */
    data class Success<T>(val data: T) : UiState<T>()

    /** Represents an error state with an associated exception. */
    data class Error(val exception: Throwable) : UiState<Nothing>()
}
