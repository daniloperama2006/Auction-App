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

class AuctionRepository(private val api: AuctionApiService) {

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
                    ?: Result.failure(Exception("Respuesta vacía en detalle"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

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
                    ?: Result.failure(Exception("Respuesta vacía al crear subasta"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

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
