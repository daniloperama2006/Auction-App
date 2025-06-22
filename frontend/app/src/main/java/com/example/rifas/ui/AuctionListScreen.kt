package com.example.rifas.presentation.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.data.model.Auction
import com.example.rifas.presentation.ui.CreateAuctionScreen
import com.example.rifas.presentation.ui.AuctionDetailScreen


@Composable
fun AuctionListScreen(viewModel: AuctionViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    val auctions by viewModel.auctionList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadAuctions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Auctions", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val intent = Intent(context, CreateAuctionActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Create New Auction")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by name...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(auctions.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }) { auction ->
                AuctionItem(auction = auction, onClick = {
                    val intent = Intent(context, AuctionDetailActivity::class.java)
                    intent.putExtra("auctionId", auction.id)
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun AuctionItem(auction: Auction, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(text = auction.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "Date: ${auction.date}")
        val winnerText = auction.winnerNumber?.toString() ?: "No winner yet"
        Text(text = "Winner: $winnerText", fontWeight = FontWeight.SemiBold)
    }
}
