package com.example.rifas.presentation.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rifas.MainActivity
import com.example.rifas.data.model.Auction
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.presentation.viewmodel.UiState
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateAuctionScreen(viewModel: AuctionViewModel) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    // Observa el estado de creación
    val createState by viewModel.createState.collectAsState()

    // Cuando showDatePicker = true, mostrar DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                date = LocalDate.of(year, month + 1, dayOfMonth)
                showDatePicker = false
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        ).show()
    }

    // Reacciona a cambios de createState para mostrar Toast y navegar/resetear estado
    LaunchedEffect(createState) {
        when (createState) {
            is UiState.Success -> {
                Toast.makeText(context, "Auction created", Toast.LENGTH_SHORT).show()
                // Resetear el estado para evitar disparos repetidos
                viewModel.resetCreateState()
                // Volver a pantalla principal (lista). Aquí abrimos MainActivity.
                context.startActivity(Intent(context, MainActivity::class.java))
            }
            is UiState.Error -> {
                val ex = (createState as UiState.Error).exception
                Log.e("CreateAuction", "Failed to create auction", ex)
                Toast.makeText(context, "Error creating auction: ${ex.message}", Toast.LENGTH_LONG).show()
                viewModel.resetCreateState()
            }
            else -> {
                // Idle o Loading: no hacer nada especial aquí
            }
        }
    }

    // Indicar si estamos en Loading (pendiente de respuesta)
    val isLoading = createState is UiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Create New Auction", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de nombre
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para seleccionar fecha
        Button(
            onClick = { if (!isLoading) showDatePicker = true },
            enabled = !isLoading
        ) {
            Text("Select Date: $date")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Si está cargando, mostramos indicador
        if (isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón Guardar subasta
        Button(
            onClick = {
                if (name.isBlank()) {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    // Matriz vacía 10x10
                    val emptyMatrix = List(10) { List(10) { 0 } }
                    val newAuction = Auction(
                        name = name,
                        date = date.toString(),
                        matrix = emptyMatrix
                    )
                    viewModel.createAuction(newAuction)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && !isLoading
        ) {
            Text("Save Auction")
        }
    }
}
