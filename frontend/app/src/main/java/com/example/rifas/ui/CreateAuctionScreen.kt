// src/main/java/com/example/rifas/presentation/ui/CreateAuctionScreen.kt
package com.example.rifas.presentation.ui
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import com.example.rifas.presentation.viewmodel.UiState
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateAuctionScreen(
    viewModel: AuctionViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var minOfferText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val createState by viewModel.createState.collectAsState()

    // Selector de imagen
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // DatePicker
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                date = LocalDate.of(year, month + 1, day)
                showDatePicker = false
            },
            date.year, date.monthValue - 1, date.dayOfMonth
        ).show()
    }

    // Reacciona a estado de creación
    LaunchedEffect(createState) {
        when (createState) {
            is UiState.Success -> {
                Toast.makeText(context, "Subasta creada", Toast.LENGTH_SHORT).show()
                viewModel.resetCreateState()
                onNavigateBack()
            }
            is UiState.Error -> {
                val ex = (createState as UiState.Error).exception
                Toast.makeText(context, "Error al crear: ${ex.message}", Toast.LENGTH_LONG).show()
                viewModel.resetCreateState()
            }
            else -> {}
        }
    }

    val isLoading = createState is UiState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Crear nueva Subasta", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; errorText = null },
            label = { Text("Nombre Subasta") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { if (!isLoading) showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Fecha de subasta: ${date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))}")
        }
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (imageUri == null) "Seleccionar imagen" else "Cambiar imagen")
        }
        imageUri?.let { uri ->
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = uri,
                contentDescription = "Preview imagen",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray.copy(alpha = 0.2f))
            )
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = minOfferText,
            onValueChange = { input ->
                minOfferText = input.filter { it.isDigit() }
                errorText = null
            },
            label = { Text("Oferta Mínima") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        if (errorText != null) {
            Text(text = errorText!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = {
                // Validaciones
                when {
                    name.isBlank() -> errorText = "El nombre no puede estar vacío"
                    date.isBefore(LocalDate.now()) -> errorText = "La fecha debe ser hoy o futura"
                    minOfferText.toLongOrNull() == null -> errorText = "Ingresa oferta mínima válida"
                    imageUri == null -> errorText = "Selecciona una imagen"
                    else -> {
                        val minOffer = minOfferText.toLong()
                        // Convertir imageUri a MultipartBody.Part?
                        val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                            // Crear archivo temporal en cache
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                            inputStream?.use { input ->
                                tempFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            val reqFile = tempFile.asRequestBody("image/jpeg".toMediaType())
                            MultipartBody.Part.createFormData("image", tempFile.name, reqFile)
                        }
                        viewModel.createAuction(
                            name,
                            date.format(DateTimeFormatter.ISO_DATE),
                            minOffer,
                            imagePart
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Guardar")
            }
        }
    }
}
