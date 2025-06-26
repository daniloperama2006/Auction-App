package com.example.rifas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rifas.data.repository.AuctionRepository

/**
 * Factory for creating instances of [AuctionViewModel]
 * with the required [AuctionRepository] dependency.
 */
class AuctionViewModelFactory(
    private val repository: AuctionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuctionViewModel::class.java)) {
            return AuctionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
