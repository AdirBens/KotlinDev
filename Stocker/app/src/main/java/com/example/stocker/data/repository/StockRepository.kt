package com.example.stocker.data.repository

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.stocker.data.local_db.MyStocksDao
import com.example.stocker.data.model.Stock
import com.example.stocker.data.remote_db.StockRemoteDataSource
import com.example.stocker.utils.performFetchingAndSaving
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

    fun getStock(symbol: String) = localDataSource.getStock(symbol)

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

    fun getQuote(symbol: String) = performRemoteFetching {
        remoteDataSource.getQuote(symbol)
    }

//    fun getQuote(symbol: String) = performFetchingAndSaving(
//        {localDataSource.getStockQuote(symbol)},
//        {remoteDataSource.getQuote(symbol)},
//        {localDataSource.updateStock(it)}
//    }

    fun getTimeSeries(symbol: String, interval: String, outputSize:String) = performRemoteFetching {
        remoteDataSource.getTimeSeries(symbol, interval, outputSize)
    }

    fun getStockLogo(symbol: String) = performRemoteFetching {
        remoteDataSource.getStockLogo(symbol)
    }

    fun getCurrentPrice(symbol: String) = performRemoteFetching {
        remoteDataSource.getCurrentPrice(symbol)
    }

}
