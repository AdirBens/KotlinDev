package com.example.stocker.data.remote_db

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRemoteDataSource @Inject constructor(
    private val stockService: StockService
) : BaseDataSource() {

    suspend fun getSymbolSearchResult(keywords: String) =
        getResult { stockService.getSymbolSearch(keywords) }

    suspend fun getQuote(symbol: String) = getResult { stockService.getQuote(symbol) }

    suspend fun getTimeSeries(symbol: String, interval: String) =
        getResult { stockService.getTimeSeries(symbol, interval) }

    suspend fun getStockImage(symbol: String) =
        getResult { stockService.getStockImageURL(symbol) }

}