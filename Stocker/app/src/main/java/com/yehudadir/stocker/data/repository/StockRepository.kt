package com.yehudadir.stocker.data.repository

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

import com.yehudadir.stocker.data.local_db.PortfolioDao
import com.yehudadir.stocker.data.local_db.StocksDao
import com.yehudadir.stocker.data.model.entities.Portfolio
import com.yehudadir.stocker.data.model.entities.Stock
import com.yehudadir.stocker.data.remote_db.StockRemoteDataSource
import com.yehudadir.stocker.utils.performLocalFetching
import com.yehudadir.stocker.utils.performRemoteFetching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Singleton
class StockRepository @Inject constructor(
    application:Application,
    private val remoteDataSource: StockRemoteDataSource,
    private val localStocksDataSource: StocksDao,
    private val localPortfolioDataSource: PortfolioDao
    ) {

    // ==============================
    //      On Local Database
    // ==============================
    fun getStock(symbol: String) = performLocalFetching {
        localStocksDataSource.getStock(symbol)
    }

    fun getAllStocks() = performLocalFetching {
        localStocksDataSource.getStocks()
    }

//    fun getPortfolio(id:Int) = localPortfolioDataSource.getPortfolio(id)
    fun getPortfolio(id:Int) = performLocalFetching {
        localPortfolioDataSource.getPortfolio(id)
    }

    suspend fun addStock(stock: Stock)  = withContext(Dispatchers.IO) {
        localStocksDataSource.addStock(stock)
    }

    suspend fun updateStock(stock: Stock)  = withContext(Dispatchers.IO) {
        localStocksDataSource.updateStock(stock)
    }

    suspend fun deleteStock(stock: Stock)  = withContext(Dispatchers.IO) {
        localStocksDataSource.deleteStock(stock)
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        localStocksDataSource.deleteAll()
    }

    suspend fun updatePortfolio(portfolio: Portfolio) = withContext(Dispatchers.IO) {
        localPortfolioDataSource.updatePortfolio(portfolio)
    }

    suspend fun deletePortfolio(portfolio: Portfolio) = withContext(Dispatchers.IO) {
        localPortfolioDataSource.deletePortfolio(portfolio)
    }

    suspend fun addPortfolio(portfolio: Portfolio) = withContext(Dispatchers.IO) {
        localPortfolioDataSource.addPortfolio(portfolio)
    }

    // ==============================
    //      On Remote Source
    // ==============================
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
