// src/main/java/com/example/rifas/presentation/ui/CreateAuctionActivity.kt
package com.example.rifas.presentation.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.rifas.data.network.RetrofitClient
import com.example.rifas.data.repository.AuctionRepository
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.presentation.viewmodel.AuctionViewModelFactory

class CreateAuctionActivity : ComponentActivity() {
    private val viewModel: AuctionViewModel by viewModels {
        AuctionViewModelFactory(AuctionRepository(RetrofitClient.apiService))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateAuctionScreen(
                viewModel = viewModel,
                onNavigateBack = { finish() }
            )
        }
    }
}
