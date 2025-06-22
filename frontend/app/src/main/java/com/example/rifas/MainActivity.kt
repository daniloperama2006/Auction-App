package com.example.rifas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.rifas.data.network.RetrofitClient
import com.example.rifas.data.repository.AuctionRepository
import com.example.rifas.presentation.ui.*
import com.example.rifas.presentation.viewmodel.AuctionViewModel

class MainActivity : ComponentActivity() {

    private val viewModel by lazy {
        val repository = AuctionRepository(RetrofitClient.apiService)
        AuctionViewModel(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuctionListScreen(viewModel)
        }
    }
}
