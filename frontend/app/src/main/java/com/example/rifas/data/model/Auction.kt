// src/main/java/com/example/rifas/data/model/Auction.kt
package com.example.rifas.data.model

data class Auction(
    val id: Int = 0,
    val name: String,
    val date: String,
    val matrix: List<List<Int>>,
    val winnerNumber: Int? = null
)
