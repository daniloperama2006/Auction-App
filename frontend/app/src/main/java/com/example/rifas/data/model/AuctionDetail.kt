package com.example.rifas.data.model

import com.google.gson.annotations.SerializedName

/**
 * Detailed information for a single auction, used in the detail screen.
 *
 * @property id Auction identifier.
 * @property name Name of the auction.
 * @property date Raffle date in ISO format (YYYY-MM-DD).
 * @property matrix 10x10 number availability grid (0 = free, 1 = taken).
 * @property currentMaxOffer Highest bid currently placed.
 * @property minOffer Minimum allowed bid amount.
 * @property imageUrl Optional image of the auction item.
 * @property enrolledPeople Number of users who placed bids.
 * @property isFinished Indicates whether the auction is closed.
 * @property winnerNumber The selected winner number, if finished.
 * @property userRole Role of the current user ("ADMIN", "USER", etc.).
 */
data class AuctionDetail(
    val id: Int,
    val name: String,
    val date: String,             // ISO “YYYY-MM-DD”
    val matrix: List<List<Int>>,  // 10x10 grid
    val currentMaxOffer: Long,
    val minOffer: Long,
    val imageUrl: String?,
    @SerializedName("inscritos")
    val enrolledPeople: Int,      // maps from JSON "inscritos"
    val isFinished: Boolean,
    val winnerNumber: Int?,
    val userRole: String
)
