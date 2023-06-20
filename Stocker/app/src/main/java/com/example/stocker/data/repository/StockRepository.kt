package com.example.stocker.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.stocker.data.local_db.PortfolioDao

import com.example.stocker.data.local_db.StocksDao
import com.example.stocker.data.model.Portfolio
import com.example.stocker.data.model.Stock
import com.example.stocker.data.remote_db.StockRemoteDataSource
import com.example.stocker.utils.performRemoteFetching
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    application:Application,
    private val remoteDataSource: StockRemoteDataSource,
    private val localStocksDataSource: StocksDao,
    private val localPortfolioDataSource: PortfolioDao
    ) {

    fun getStocks() = localStocksDataSource.getStocks()

    fun getStock(symbol: String) = localStocksDataSource.getStock(symbol)

    fun getPortfolio(id:Int) = localPortfolioDataSource.getPortfolio(id)

    suspend fun addStock(stock: Stock) {
        localStocksDataSource.addStock(stock)
    }

    suspend fun updateStock(stock: Stock) {
        localStocksDataSource.updateStock(stock)
    }

    suspend fun deleteStock(stock: Stock) {
        localStocksDataSource.deleteStock(stock)
    }

    suspend fun deleteAll() {
        localStocksDataSource.deleteAll()
    }

    suspend fun updatePortfolio(portfolio: Portfolio) {
        localPortfolioDataSource.updatePortfolio(portfolio)
    }



    suspend fun deletePortfolio(portfolio: Portfolio) {
        localPortfolioDataSource.deletePortfolio(portfolio)
    }

    suspend fun addPortfolio(portfolio: Portfolio) {
        localPortfolioDataSource.addPortfolio(portfolio)
    }

    fun getSymbolSearch (keyword: String) = performRemoteFetching {
        remoteDataSource.getSymbolSearchResult(keyword)
    }

    fun getQuote(symbol: String) = performRemoteFetching {
        remoteDataSource.getQuote(symbol)
    }

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
