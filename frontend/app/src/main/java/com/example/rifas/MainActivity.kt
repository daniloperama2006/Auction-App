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

/**
 * Main entry point of the app, displaying the list of auctions.
 */
class MainActivity : ComponentActivity() {

    // Inject ViewModel with repository instance via factory
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
                    // Open CreateAuctionActivity
                    startActivity(Intent(this, CreateAuctionActivity::class.java))
                },
                onNavigateToDetail = { auctionId ->
                    // Open AuctionDetailActivity with selected ID
                    val intent = Intent(this, AuctionDetailActivity::class.java)
                    intent.putExtra("auctionId", auctionId)
                    startActivity(intent)
                }
            )
        }
    }
}
