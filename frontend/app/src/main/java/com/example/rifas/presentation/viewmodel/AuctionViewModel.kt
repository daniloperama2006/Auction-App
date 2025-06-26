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

/**
 * ViewModel for managing auction-related UI logic and state.
 */
class AuctionViewModel(
    private val repository: AuctionRepository
) : ViewModel() {

    // Holds the current list of auction summaries
    private val _auctionList = MutableStateFlow<List<AuctionSummary>>(emptyList())
    val auctionList: StateFlow<List<AuctionSummary>> = _auctionList

    // Holds the state of a selected auction's detail
    private val _selectedAuctionDetail = MutableStateFlow<UiState<AuctionDetail>>(UiState.Idle)
    val selectedAuctionDetail: StateFlow<UiState<AuctionDetail>> = _selectedAuctionDetail

    // Tracks the state of auction creation
    private val _createState = MutableStateFlow<UiState<AuctionDetail>>(UiState.Idle)
    val createState: StateFlow<UiState<AuctionDetail>> = _createState

    // Tracks the state of bidding
    private val _bidState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val bidState: StateFlow<UiState<Unit>> = _bidState

    // Tracks the state of auction finalization
    private val _finalizeState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val finalizeState: StateFlow<UiState<Unit>> = _finalizeState

    // Tracks the state of auction deletion
    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState

    /** Loads all auctions, optionally filtered by a search string */
    fun loadAuctions(search: String? = null) {
        viewModelScope.launch {
            repository.getAuctions(search).onSuccess { list ->
                _auctionList.value = list
            }
        }
    }

    /** Loads the details of a specific auction by ID */
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

    /** Creates a new auction with optional image */
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

    /** Posts a bid for a specific number and amount in an auction */
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

    /** Finalizes the auction by declaring a winner (admin only) */
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

    /** Deletes an auction by ID */
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
