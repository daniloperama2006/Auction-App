package com.example.rifas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rifas.data.model.Auction
import com.example.rifas.data.repository.AuctionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de subastas (auctions), sigue MVVM con Repository y StateFlow.
 * Exponemos:
 *  - auctionList: lista de subastas
 *  - selectedAuction: detalle de una subasta
 *  - createState: estado de creación de subasta (Idle/Loading/Success/Error)
 *  - winnerResult: estado de asignar ganador (Idle/Loading/Success/Error)
 */
class AuctionViewModel(private val repository: AuctionRepository) : ViewModel() {

    // Lista de subastas
    private val _auctionList = MutableStateFlow<List<Auction>>(emptyList())
    val auctionList: StateFlow<List<Auction>> = _auctionList

    // Subasta seleccionada (detalle)
    private val _selectedAuction = MutableStateFlow<Auction?>(null)
    val selectedAuction: StateFlow<Auction?> = _selectedAuction

    // Estado de creación de subasta
    private val _createState = MutableStateFlow<UiState<Auction>>(UiState.Idle)
    val createState: StateFlow<UiState<Auction>> = _createState

    // Estado de asignar ganador
    private val _winnerResult = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val winnerResult: StateFlow<UiState<Unit>> = _winnerResult

    /**
     * Carga la lista de subastas desde el repositorio.
     */
    fun loadAuctions() {
        viewModelScope.launch {
            // Podrías exponer un estado loadingList si lo deseas.
            val result = repository.getAllAuctions()
            result.onSuccess { list ->
                _auctionList.value = list
            }.onFailure { ex ->
                // Opcional: manejar error de lista, p.ej. exponer un StateFlow<String?> errorList
                // Aquí por simplicidad no hacemos nada extra.
            }
        }
    }

    /**
     * Carga una subasta por su ID.
     */
    fun loadAuctionById(id: Int) {
        viewModelScope.launch {
            // Podrías exponer un estado loadingDetail si lo deseas
            val result = repository.getAuctionById(id)
            result.onSuccess { auction ->
                _selectedAuction.value = auction
            }.onFailure { ex ->
                // Opcional: exponer errorDetail
            }
        }
    }

    /**
     * Crea una nueva subasta. Exponer el estado en createState.
     */
    fun createAuction(auction: Auction) {
        viewModelScope.launch {
            _createState.value = UiState.Loading
            val result = repository.createAuction(auction)
            if (result.isSuccess) {
                val created = result.getOrThrow()
                _createState.value = UiState.Success(created)
                // Tras éxito, recargar lista
                loadAuctions()
            } else {
                _createState.value = UiState.Error(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        }
    }

    /**
     * Resetea el estado de creación a Idle, para poder reutilizar la pantalla sin repetir toasts.
     */
    fun resetCreateState() {
        _createState.value = UiState.Idle
    }

    /**
     * Actualiza una subasta existente.
     */
    fun updateAuction(id: Int, auction: Auction) {
        viewModelScope.launch {
            // Podrías exponer estado updateState si quisieras
            repository.updateAuction(id, auction)
            // Luego recarga lista
            loadAuctions()
        }
    }

    /**
     * Asigna el número ganador. Exponer estado en winnerResult.
     */
    fun saveWinner(id: Int, winnerNumber: Int) {
        viewModelScope.launch {
            _winnerResult.value = UiState.Loading
            val result = repository.saveWinner(id, winnerNumber)
            if (result.isSuccess) {
                _winnerResult.value = UiState.Success(Unit)
                // Recargar detalle tras asignar ganador
                loadAuctionById(id)
            } else {
                _winnerResult.value = UiState.Error(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        }
    }

    /**
     * Resetea el estado de asignar ganador a Idle.
     */
    fun resetWinnerState() {
        _winnerResult.value = UiState.Idle
    }
}
