package com.example.stocker.data.remote_db

import com.example.stocker.data.model.StockCurrentPrice
import com.example.stocker.data.model.StockImageURL
import com.example.stocker.data.model.StockQuote
import com.example.stocker.data.model.StockTimeSeries
import retrofit2.Response
import com.example.stocker.data.model.SymbolSearch
import retrofit2.http.GET
import retrofit2.http.Query

interface StockService {

    @GET("symbol_search")
    suspend fun getSymbolSearch(
        @Query("symbol") keywords: String,
        @Query("outputsize") outputSize: String? = "120",
        @Query("show_plan") showPlan: Boolean? = true,
    ): Response<SymbolSearch>

    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String
    ): Response<StockQuote>

    @GET("avgprice")
    suspend fun getTimeSeries(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("outputsize") outputSize: String? = null,
        ): Response<StockTimeSeries>

    @GET("logo")
    suspend fun getStockLogo(
        @Query("symbol") symbol: String
    ): Response<StockImageURL>

    @GET("price")
    suspend fun getCurrentPrice(
        @Query("symbol") symbol: String
    ): Response<StockCurrentPrice>
}