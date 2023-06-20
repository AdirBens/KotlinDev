package com.example.stocker.data.remote_db

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.*

@Singleton
class StockRemoteDataSource @Inject constructor(
    private val stockService: StockService
) : BaseDataSource() {




    suspend fun getSymbolSearchResult(keywords: String) =
        getResult { stockService.getSymbolSearch(keywords) }

    suspend fun getQuote(symbol: String) = getResult { stockService.getQuote(symbol) }

    suspend fun getTimeSeries(symbol: String, interval: String, outputSize:String) =
        getResult { stockService.getTimeSeries(symbol, interval, outputSize) }

    suspend fun getStockLogo(symbol: String) =
        getResult { stockService.getStockLogo(symbol) }

    suspend fun getCurrentPrice(symbol: String) =
        getResult { stockService.getCurrentPrice(symbol) }

}