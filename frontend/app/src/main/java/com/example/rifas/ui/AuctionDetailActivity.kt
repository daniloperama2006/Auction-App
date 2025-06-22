package com.example.rifas.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.rifas.data.network.RetrofitClient
import com.example.rifas.data.repository.AuctionRepository
import com.example.rifas.presentation.viewmodel.AuctionViewModel

class AuctionDetailActivity : ComponentActivity() {
    private val viewModel by lazy {
        AuctionViewModel(AuctionRepository(RetrofitClient.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auctionId = intent.getIntExtra("auctionId", -1)
        setContent {
            AuctionDetailScreen(auctionId, viewModel)
        }
    }
}
