package com.example.rifas.data.model

import com.google.gson.annotations.SerializedName

data class AuctionDetail(
    val id: Int,
    val name: String,
    val date: String,             // ISO “YYYY-MM-DD”
    val matrix: List<List<Int>>,  // lista de 10 listas de 10 ints
    val currentMaxOffer: Long,
    val minOffer: Long,
    val imageUrl: String?,
    @SerializedName("inscritos")
    val enrolledPeople: Int,      // ahora mapea el campo "inscritos"
    val isFinished: Boolean,
    val winnerNumber: Int?,
    val userRole: String          // p.ej. "ADMIN" o "USER"
)
