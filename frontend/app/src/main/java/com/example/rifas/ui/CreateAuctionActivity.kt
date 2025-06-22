package com.example.rifas.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.rifas.data.network.RetrofitClient
import com.example.rifas.data.repository.AuctionRepository
import com.example.rifas.presentation.viewmodel.AuctionViewModel

class CreateAuctionActivity : ComponentActivity() {
    private val viewModel by lazy {
        AuctionViewModel(AuctionRepository(RetrofitClient.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateAuctionScreen(viewModel)
        }
    }
}
