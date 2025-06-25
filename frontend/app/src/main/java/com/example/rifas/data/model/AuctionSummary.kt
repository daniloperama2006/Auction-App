package com.example.rifas.data.model

import com.google.gson.annotations.SerializedName

data class AuctionSummary(
    val id: Int,
    val name: String,
    val date: String,           // ISO “YYYY-MM-DD”
    val currentMaxOffer: Long,  // coincide con JSON "currentMaxOffer"
    @SerializedName("inscritos")
    val enrolledPeople: Int,    // antes siempre 0, ahora mapea el campo "inscritos"
    val imageUrl: String?,      // puede venir null
    val isFinished: Boolean,
    val winnerNumber: Int?      // puede ser null
)
