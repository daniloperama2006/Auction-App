// src/main/java/com/example/rifas/presentation/ui/AuctionListScreen.kt
package com.example.rifas.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rifas.data.model.AuctionSummary
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.presentation.viewmodel.UiState
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuctionListScreen(
    viewModel: AuctionViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val auctions      by viewModel.auctionList.collectAsState()
    val finalizeState by viewModel.finalizeState.collectAsState()
    val deleteState   by viewModel.deleteState.collectAsState()

    // Auto-poll every 5 seconds, also triggered on search change
    LaunchedEffect(searchQuery) {
        while (true) {
            viewModel.loadAuctions(searchQuery.ifBlank { null })
            delay(5000)
        }
    }

    // Also refresh immediately after finalize or delete
    LaunchedEffect(finalizeState, deleteState) {
        if (finalizeState is UiState.Success || deleteState is UiState.Success) {
            viewModel.resetFinalizeState()
            viewModel.resetDeleteState()
            viewModel.loadAuctions(searchQuery.ifBlank { null })
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with title and manual Refresh
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subastas", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = {
                viewModel.loadAuctions(searchQuery.ifBlank { null })
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
            }
        }
        Spacer(Modifier.height(16.dp))

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar por nombre o fecha") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                TextButton(onClick = {
                    viewModel.loadAuctions(searchQuery.ifBlank { null })
                }) {
                    Text("Buscar")
                }
            }
        )
        Spacer(Modifier.height(16.dp))

        // Auctions table occupies remaining space
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column {
                // Column headers
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        "Nombre",
                        Modifier.weight(2f).padding(start = 8.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Máx.",
                        Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Inscritos",
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Fecha",
                        Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Acciones",
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
                Divider()

                // Auction rows
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(auctions) { auc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(auc.id) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(auc.name, Modifier.weight(2f))
                            Text(
                                NumberFormat.getNumberInstance().format(auc.currentMaxOffer),
                                Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            Text(
                                auc.enrolledPeople.toString(),
                                Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                formatDateDisplay(auc.date),
                                Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                            Spacer(Modifier.weight(1f))
                            if (!auc.isFinished) {
                                Button(onClick = { onNavigateToDetail(auc.id) }, modifier = Modifier.weight(1f)) {
                                    Text("Ver")
                                }
                            } else {
                                Text("✔️", Modifier.weight(1f), textAlign = TextAlign.Center)
                            }
                        }
                        Divider()
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // "Nueva" button at bottom
        Button(
            onClick = onNavigateToCreate,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Nueva")
        }
    }
}

// Date formatting helper
@RequiresApi(Build.VERSION_CODES.O)
fun formatDateDisplay(isoDate: String): String = runCatching {
    LocalDate.parse(isoDate).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
}.getOrNull() ?: isoDate
