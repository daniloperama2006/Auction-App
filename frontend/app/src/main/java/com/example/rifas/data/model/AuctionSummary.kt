package com.example.rifas.data.model

import com.google.gson.annotations.SerializedName

/**
 * Lightweight auction data used for displaying in lists.
 *
 * @property id Unique identifier of the auction.
 * @property name Auction title.
 * @property date Raffle date in ISO format (YYYY-MM-DD).
 * @property currentMaxOffer Highest bid placed so far.
 * @property enrolledPeople Number of participants (mapped from JSON field "inscritos").
 * @property imageUrl Optional image URL for the auction.
 * @property isFinished True if the auction has ended.
 * @property winnerNumber Winning number (null if not finalized).
 */
data class AuctionSummary(
    val id: Int,
    val name: String,
    val date: String,           // ISO “YYYY-MM-DD”
    val currentMaxOffer: Long,  // matches JSON "currentMaxOffer"
    @SerializedName("inscritos")
    val enrolledPeople: Int,
    val imageUrl: String?,
    val isFinished: Boolean,
    val winnerNumber: Int?
)
