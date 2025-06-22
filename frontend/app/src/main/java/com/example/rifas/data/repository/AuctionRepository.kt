package com.example.rifas.data.repository

import com.example.rifas.data.model.Auction
import com.example.rifas.data.network.AuctionApiService
import com.example.rifas.data.network.WinnerRequest
import retrofit2.HttpException
import java.io.IOException

class AuctionRepository(private val api: AuctionApiService) {

    suspend fun getAllAuctions(): Result<List<Auction>> {
        return try {
            val response = api.getAuctions()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    suspend fun getAuctionById(id: Int): Result<Auction> {
        return try {
            val response = api.getAuctionById(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    suspend fun createAuction(auction: Auction): Result<Auction> {
        return try {
            val response = api.createAuction(auction)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    suspend fun updateAuction(id: Int, auction: Auction): Result<Auction> {
        return try {
            val response = api.updateAuction(id, auction)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    suspend fun saveWinner(id: Int, winnerNumber: Int): Result<Unit> {
        return try {
            val response = api.saveWinner(id, WinnerRequest(winnerNumber))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
