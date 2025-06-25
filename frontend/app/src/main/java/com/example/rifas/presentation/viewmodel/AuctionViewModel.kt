package com.example.rifas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rifas.data.model.AuctionSummary
import com.example.rifas.data.model.AuctionDetail
import com.example.rifas.data.repository.AuctionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AuctionViewModel(
    private val repository: AuctionRepository
) : ViewModel() {

    private val _auctionList = MutableStateFlow<List<AuctionSummary>>(emptyList())
    val auctionList: StateFlow<List<AuctionSummary>> = _auctionList

    private val _selectedAuctionDetail = MutableStateFlow<UiState<AuctionDetail>>(UiState.Idle)
    val selectedAuctionDetail: StateFlow<UiState<AuctionDetail>> = _selectedAuctionDetail

    private val _createState = MutableStateFlow<UiState<AuctionDetail>>(UiState.Idle)
    val createState: StateFlow<UiState<AuctionDetail>> = _createState

    private val _bidState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val bidState: StateFlow<UiState<Unit>> = _bidState

    private val _finalizeState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val finalizeState: StateFlow<UiState<Unit>> = _finalizeState

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState

    fun loadAuctions(search: String? = null) {
        viewModelScope.launch {
            repository.getAuctions(search).onSuccess { list ->
                _auctionList.value = list
            }
        }
    }

    fun loadAuctionDetail(id: Int) {
        viewModelScope.launch {
            _selectedAuctionDetail.value = UiState.Loading
            repository.getAuctionDetail(id).onSuccess { detail ->
                _selectedAuctionDetail.value = UiState.Success(detail)
            }.onFailure { ex ->
                _selectedAuctionDetail.value = UiState.Error(ex)
            }
        }
    }

    fun createAuction(
        name: String,
        date: String,
        minOffer: Long,
        imagePart: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _createState.value = UiState.Loading
            repository.createAuction(name, date, minOffer, imagePart).onSuccess { created ->
                _createState.value = UiState.Success(created)
                loadAuctions()
            }.onFailure { ex ->
                _createState.value = UiState.Error(ex)
            }
        }
    }

    fun resetCreateState() {
        _createState.value = UiState.Idle
    }

    fun postBid(auctionId: Int, number: Int, amount: Long) {
        viewModelScope.launch {
            _bidState.value = UiState.Loading
            repository.postBid(auctionId, number, amount).onSuccess {
                _bidState.value = UiState.Success(Unit)
            }.onFailure { ex ->
                _bidState.value = UiState.Error(ex)
            }
        }
    }
    fun resetBidState() { _bidState.value = UiState.Idle }

    /** Finalizar subasta (solo admin), indicando número ganador */
    fun finalizeAuction(auctionId: Int, winnerNumber: Int) {
        viewModelScope.launch {
            _finalizeState.value = UiState.Loading
            repository.finalizeAuction(auctionId, winnerNumber).onSuccess {
                _finalizeState.value = UiState.Success(Unit)
                loadAuctions()
            }.onFailure { ex ->
                _finalizeState.value = UiState.Error(ex)
            }
        }
    }
    fun resetFinalizeState() { _finalizeState.value = UiState.Idle }

    fun deleteAuction(auctionId: Int) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            repository.deleteAuction(auctionId).onSuccess {
                _deleteState.value = UiState.Success(Unit)
                loadAuctions()
            }.onFailure { ex ->
                _deleteState.value = UiState.Error(ex)
            }
        }
    }
    fun resetDeleteState() { _deleteState.value = UiState.Idle }
}
