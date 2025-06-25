// src/main/java/com/example/rifas/presentation/ui/AuctionDetailScreen.kt
package com.example.rifas.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rifas.data.model.AuctionDetail
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.presentation.viewmodel.UiState
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AuctionDetailScreen(
    auctionId: Int,
    viewModel: AuctionViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // ViewModel states
    val detailState   by viewModel.selectedAuctionDetail.collectAsState()
    val bidState      by viewModel.bidState.collectAsState()
    val finalizeState by viewModel.finalizeState.collectAsState()
    val deleteState   by viewModel.deleteState.collectAsState()

    // Local UI state
    var selectedNumber    by remember { mutableStateOf<Int?>(null) }
    var bidAmountText     by remember { mutableStateOf("") }
    var bidError          by remember { mutableStateOf<String?>(null) }
    var showFinalizeDialog by remember { mutableStateOf(false) }
    var winnerText        by remember { mutableStateOf("") }
    var winnerError       by remember { mutableStateOf<String?>(null) }

    // Load detail once
    LaunchedEffect(auctionId) {
        viewModel.loadAuctionDetail(auctionId)
    }

    // Handle bid results
    LaunchedEffect(bidState) {
        when (bidState) {
            is UiState.Success -> {
                Toast.makeText(context, "Puja enviada con éxito", Toast.LENGTH_SHORT).show()
                viewModel.resetBidState()
                viewModel.loadAuctionDetail(auctionId)
                selectedNumber = null
                bidAmountText = ""
            }
            is UiState.Error -> {
                Toast.makeText(
                    context,
                    "Error al pujar: ${(bidState as UiState.Error).exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetBidState()
            }
            else -> {}
        }
    }

    // Handle finalize results
    LaunchedEffect(finalizeState) {
        when (finalizeState) {
            is UiState.Success -> {
                Toast.makeText(context, "Subasta finalizada", Toast.LENGTH_SHORT).show()
                viewModel.resetFinalizeState()
                onNavigateBack()
            }
            is UiState.Error -> {
                Toast.makeText(
                    context,
                    "Error al finalizar: ${(finalizeState as UiState.Error).exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetFinalizeState()
            }
            else -> {}
        }
    }

    // Handle delete results
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is UiState.Success -> {
                Toast.makeText(context, "Subasta eliminada", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteState()
                onNavigateBack()
            }
            is UiState.Error -> {
                Toast.makeText(
                    context,
                    "Error al eliminar: ${(deleteState as UiState.Error).exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    // UI
    when (detailState) {
        is UiState.Loading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        is UiState.Error -> {
            val ex = (detailState as UiState.Error).exception
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error: ${ex.message}", color = Color.Red)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.loadAuctionDetail(auctionId) }) { Text("Reintentar") }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onNavigateBack) { Text("Volver") }
            }
        }
        is UiState.Success -> {
            val auction = (detailState as UiState.Success<AuctionDetail>).data

            // Protect nullable userRole
            val isAdmin = auction.userRole
                ?.uppercase(Locale.getDefault())
                ?.equals("ADMIN", ignoreCase = true)
                ?: false

            Column(Modifier.fillMaxSize().padding(16.dp)) {

                Spacer(Modifier.height(16.dp))

                // Grid 10×10
                LazyVerticalGrid(
                    columns = GridCells.Fixed(10),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                ) {
                    items(100) { idx ->
                        val row    = idx / 10
                        val col    = idx % 10
                        val status = auction.matrix.getOrNull(row)?.getOrNull(col) ?: 1
                        val available = status == 0 && !auction.isFinished
                        val selected  = selectedNumber == idx
                        val bgColor = when {
                            status != 0 -> Color(0xFFCE93D8)
                            selected    -> Color(0xFFBA68C8)
                            else        -> Color.White
                        }
                        Box(
                            Modifier
                                .aspectRatio(1f)
                                .background(bgColor, shape = MaterialTheme.shapes.small)
                                .border(
                                    1.dp,
                                    if (status == 0) Color.Gray else Color.Transparent,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable(enabled = available) {
                                    selectedNumber = if (selected) null else idx
                                    bidError = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                idx.toString().padStart(2, '0'),
                                fontSize = 12.sp,
                                color = if (status == 0) Color.Black else Color.White
                            )
                        }
                    }
                }
                Text("• números no disponibles", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(16.dp))

                // Offers Info
                Text("Oferta actual máxima: ${NumberFormat.getNumberInstance().format(auction.currentMaxOffer)}")
                Text("Oferta mínima: ${NumberFormat.getNumberInstance().format(auction.minOffer)}")
                Spacer(Modifier.height(16.dp))

                // Auction Image
                auction.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color.LightGray.copy(alpha = 0.2f))
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // Bid Input
                OutlinedTextField(
                    value = bidAmountText,
                    onValueChange = {
                        bidAmountText = it.filter(Char::isDigit)
                        bidError = null
                    },
                    label = { Text("Puja / Oferta") },
                    isError = bidError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = bidState !is UiState.Loading
                )
                bidError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                Spacer(Modifier.height(16.dp))

                // Actions Row: Guardar, Finalizar, Eliminar
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Guardar puja
                    Button(
                        onClick = {
                            val num = selectedNumber
                            val amt = bidAmountText.toLongOrNull()
                            when {
                                num == null -> bidError = "Selecciona un número"
                                amt == null -> bidError = "Ingresa un monto válido"
                                amt < auction.minOffer -> bidError = "Debe ser ≥ ${auction.minOffer}"
                                amt <= auction.currentMaxOffer -> bidError = "Debe ser > ${auction.currentMaxOffer}"
                                else -> viewModel.postBid(auctionId, num, amt)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = bidState !is UiState.Loading
                    ) {
                        if (bidState is UiState.Loading)
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        else Text("Guardar")
                    }
                    Button(
                        onClick = { showFinalizeDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                        enabled = finalizeState !is UiState.Loading
                    ) {
                        if (finalizeState is UiState.Loading)
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        else Text("Finalizar")
                    }
                    Button(
                        onClick = { viewModel.deleteAuction(auctionId) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.8f)),
                        enabled = deleteState !is UiState.Loading
                    ) {
                        if (deleteState is UiState.Loading)
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        else Text("Eliminar")
                    }

                }
            }
        }
        UiState.Idle -> {
            // No-op
        }
    }

    // Dialogo para ingresar número ganador
    if (showFinalizeDialog) {
        AlertDialog(
            onDismissRequest = { showFinalizeDialog = false },
            title = { Text("Número ganador") },
            text = {
                Column {
                    OutlinedTextField(
                        value = winnerText,
                        onValueChange = {
                            winnerText = it.filter(Char::isDigit)
                            winnerError = null
                        },
                        label = { Text("00–99") },
                        isError = winnerError != null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    winnerError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val wn = winnerText.toIntOrNull()
                    when {
                        wn == null    -> winnerError = "Número inválido"
                        wn !in 0..99  -> winnerError = "Debe ser entre 0 y 99"
                        else -> {
                            viewModel.finalizeAuction(auctionId, wn)
                            showFinalizeDialog = false
                        }
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinalizeDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
