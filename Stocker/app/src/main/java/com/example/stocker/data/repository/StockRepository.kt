package com.example.stocker.data.repository

import android.app.Application
import com.example.stocker.data.local.StockDao
import com.example.stocker.data.local.StocksDatabase
import com.example.stocker.data.model.Stock

class StockRepository(application: Application){

    private var stockDao: StockDao?

    init {
        val db  = StocksDatabase.getDatabase(application)
        stockDao = db.stockDao()
    }

    fun getStocks() = stockDao?.getStocks()

    suspend fun addStock(stock: Stock) {
            stockDao?.addStock(stock)
    }

    suspend fun deleteStock(stock: Stock) {
            stockDao?.deleteStock(stock)
    }

    suspend fun deleteAll() {
        stockDao?.deleteAll()
    }
}
