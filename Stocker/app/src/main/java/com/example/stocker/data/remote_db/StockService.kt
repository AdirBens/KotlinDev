package com.example.stocker.data.remote_db

import com.example.stocker.data.model.StockImageURL
import com.example.stocker.data.model.StockQuote
import com.example.stocker.data.model.StockTimeSeries
import retrofit2.Response
import com.example.stocker.data.model.SymbolSearch
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Query

interface StockService {

    @GET("symbol_search")
    suspend fun getSymbolSearch(
        @Query("symbol") keywords: String
    ): Response<SymbolSearch>

    @GET("quote")
    suspend fun getQuote(
        @Query("symbol")symbol: String
    ): Response<StockQuote>

    @GET("time_series")
    suspend fun getTimeSeries(
        @Query("symbol")symbol: String,
        @Query("interval")interval: String
    ): Response<StockTimeSeries>

    @GET("logo")
    suspend fun getStockImageURL(
        @Query("symbol")symbol: String
    ): Response<StockImageURL>
}