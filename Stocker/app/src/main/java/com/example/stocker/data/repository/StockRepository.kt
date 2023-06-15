package com.example.stocker.data.repository

import android.app.Application
import com.example.stocker.data.local_db.MyStocksDao
import com.example.stocker.data.local_db.MyStocksDatabase
import com.example.stocker.data.model.Stock
import com.example.stocker.data.remote_db.StockRemoteDataSource
import com.example.stocker.utils.performRemoteFetching
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    application:Application,
    private val remoteDataSource: StockRemoteDataSource,
    private val localDataSource: MyStocksDao
    ) {

    fun getStocks() = localDataSource.getStocks()


    suspend fun addStock(stock: Stock) {
        localDataSource.addStock(stock)
    }

    suspend fun updateStock(stock: Stock) {
        localDataSource.updateStock(stock)
    }

    suspend fun deleteStock(stock: Stock) {
        localDataSource.deleteStock(stock)
    }

    suspend fun deleteAll() {
        localDataSource.deleteAll()
    }

    fun getSymbolSearch (keyword: String) = performRemoteFetching {
        remoteDataSource.getSymbolSearchResult(keyword)
    }

    fun getQuote(symbol: String) =performRemoteFetching { remoteDataSource.getQuote(symbol) }

    fun getTimeSeries(symbol: String, interval: String) = performRemoteFetching {
        remoteDataSource.getTimeSeries(symbol, interval)
    }

    fun getStockImage(symbol: String) = performRemoteFetching {
        remoteDataSource.getStockImage(symbol)
    }
}
