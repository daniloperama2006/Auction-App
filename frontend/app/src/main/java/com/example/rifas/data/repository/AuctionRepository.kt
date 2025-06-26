package com.example.rifas.data.repository

import com.example.rifas.data.model.AuctionSummary
import com.example.rifas.data.model.AuctionDetail
import com.example.rifas.data.network.AuctionApiService
import com.example.rifas.data.network.BidRequest
import com.example.rifas.data.network.WinnerRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository responsible for communicating with the Auction API.
 * Handles all auction-related operations and wraps results in a Kotlin Result.
 */
open class AuctionRepository(private val api: AuctionApiService) {

    suspend fun getAuctions(search: String? = null): Result<List<AuctionSummary>> {
        return try {
            val response = api.getAuctions(search)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    suspend fun getAuctionDetail(id: Int): Result<AuctionDetail> {
        return try {
            val response = api.getAuctionById(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response in detail"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    /**
     * Creates a new auction with optional image.
     */
    suspend fun createAuction(
        name: String,
        date: String,
        minOffer: Long,
        imagePart: MultipartBody.Part?
    ): Result<AuctionDetail> {
        return try {
            val namePart = name.toRequestBody("text/plain".toMediaType())
            val datePart = date.toRequestBody("text/plain".toMediaType())
            val minOfferPart = minOffer.toString().toRequestBody("text/plain".toMediaType())
            val response = api.createAuction(namePart, datePart, minOfferPart, imagePart)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response when creating auction"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    /**
     * Posts a bid to the given auction.
     */
    suspend fun postBid(auctionId: Int, number: Int, amount: Long): Result<Unit> {
        return try {
            val response = api.postBid(auctionId, BidRequest(number, amount))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    /**
     * Finalizes an auction by providing the winner's number.
     */
    suspend fun finalizeAuction(auctionId: Int, winnerNumber: Int): Result<Unit> {
        return try {
            val response = api.finalizeAuction(auctionId, WinnerRequest(winnerNumber))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    /**
     * Deletes an auction by ID.
     */
    suspend fun deleteAuction(auctionId: Int): Result<Unit> {
        return try {
            val response = api.deleteAuction(auctionId)
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
