package com.example.rifas.presentation.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rifas.MainActivity
import com.example.rifas.data.model.Auction
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.presentation.viewmodel.UiState

@Composable
fun AuctionDetailScreen(
    auctionId: Int,
    viewModel: AuctionViewModel
) {
    val context = LocalContext.current

    val selectedAuction by viewModel.selectedAuction.collectAsState()
    val winnerResult by viewModel.winnerResult.collectAsState()

    var localMatrix by remember {
        mutableStateOf(List(10) { List(10) { 0 } })
    }

    var winnerInput by remember { mutableStateOf("") }
    var winnerError by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadAuctionById(auctionId)
    }

    LaunchedEffect(selectedAuction) {
        selectedAuction?.let { auction ->
            localMatrix = auction.matrix
            winnerInput = auction.winnerNumber?.toString() ?: ""
        }
    }

    LaunchedEffect(winnerResult) {
        when (val result = winnerResult) {
            is UiState.Success -> {
                Toast.makeText(context, "Winner saved", Toast.LENGTH_SHORT).show()
                viewModel.resetWinnerState()
            }
            is UiState.Error -> {
                Toast.makeText(context, "Error saving winner: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                viewModel.resetWinnerState()
            }
            else -> {
                // UiState.Idle o UiState.Loading: no hacer nada
            }
        }
    }


    selectedAuction?.let { auction ->
        val isEditable = auction.winnerNumber == null

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = auction.name,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(10),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                items(100) { index ->
                    val row = index / 10
                    val col = index % 10
                    val isSelected = localMatrix[row][col] == 1

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(32.dp)
                            .background(
                                color = if (isSelected) Color(0xFFE91E63) else Color.LightGray,
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable(enabled = isEditable) {
                                localMatrix = localMatrix.toMutableList().apply {
                                    this[row] = this[row].toMutableList().apply {
                                        this[col] = if (this[col] == 1) 0 else 1
                                    }
                                }
                            }
                    ) {
                        Text(text = index.toString(), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isEditable) {
                OutlinedTextField(
                    value = winnerInput,
                    onValueChange = {
                        winnerInput = it
                        winnerError = ""
                    },
                    label = { Text("Winner Number (0–99)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = winnerError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (winnerError.isNotEmpty()) {
                    Text(
                        text = winnerError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val winnerNumber = winnerInput.toIntOrNull()
                    if (winnerNumber == null || winnerNumber !in 0..99) {
                        winnerError = "Invalid number"
                    } else {
                        val row = winnerNumber / 10
                        val col = winnerNumber % 10

                        if (localMatrix[row][col] == 1) {
                            viewModel.saveWinner(auction.id, winnerNumber)
                        } else {
                            winnerError = "Number $winnerNumber not selected in matrix"
                        }
                    }
                }) {
                    Text("Save Winner")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    viewModel.updateAuction(auction.id, auction.copy(matrix = localMatrix))
                    Toast.makeText(context, "Matrix saved", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Save Matrix")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    localMatrix = List(10) { List(10) { 0 } }
                }) {
                    Text("Clear")
                }
            } else {
                Text(
                    text = "This auction already has a winner.",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
            }) {
                Text("Back to Home")
            }
        }
    }
}
