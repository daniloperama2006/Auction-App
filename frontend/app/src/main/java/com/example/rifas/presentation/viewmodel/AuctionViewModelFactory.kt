// src/main/java/com/example/rifas/presentation/viewmodel/AuctionViewModelFactory.kt
package com.example.rifas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rifas.data.repository.AuctionRepository

class AuctionViewModelFactory(
    private val repository: AuctionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuctionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuctionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
