// src/main/java/com/example/rifas/presentation/ui/AuctionDetailActivity.kt
package com.example.rifas.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.rifas.data.network.RetrofitClient
import com.example.rifas.data.repository.AuctionRepository
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.presentation.viewmodel.AuctionViewModelFactory

class AuctionDetailActivity : ComponentActivity() {
    private val viewModel: AuctionViewModel by viewModels {
        AuctionViewModelFactory(AuctionRepository(RetrofitClient.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auctionId = intent.getIntExtra("auctionId", -1)
        setContent {
            if (auctionId != -1) {
                AuctionDetailScreen(
                    auctionId = auctionId,
                    viewModel = viewModel,
                    onNavigateBack = { finish() }
                )
            } else {
                finish()
            }
        }
    }
}
