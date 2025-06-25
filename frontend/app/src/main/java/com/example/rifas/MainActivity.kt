// src/main/java/com/example/rifas/MainActivity.kt
package com.example.rifas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.rifas.data.network.RetrofitClient
import com.example.rifas.data.repository.AuctionRepository
import com.example.rifas.presentation.ui.AuctionListScreen
import com.example.rifas.presentation.ui.CreateAuctionActivity
import com.example.rifas.presentation.ui.AuctionDetailActivity
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.presentation.viewmodel.AuctionViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: AuctionViewModel by viewModels {
        AuctionViewModelFactory(AuctionRepository(RetrofitClient.apiService))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuctionListScreen(
                viewModel = viewModel,
                onNavigateToCreate = {
                    startActivity(Intent(this, CreateAuctionActivity::class.java))
                },
                onNavigateToDetail = { auctionId ->
                    val intent = Intent(this, AuctionDetailActivity::class.java)
                    intent.putExtra("auctionId", auctionId)
                    startActivity(intent)
                }
            )
        }
    }
}
