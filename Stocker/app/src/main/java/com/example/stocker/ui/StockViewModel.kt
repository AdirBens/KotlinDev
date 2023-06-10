package com.example.stocker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stocker.data.model.Stock
import com.example.stocker.data.repository.StockRepository
import kotlinx.coroutines.launch

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StockRepository = StockRepository(application)
    val stocks : LiveData<List<Stock>>? = repository.getStocks()
    private val _chosenStock = MutableLiveData<Stock>()
    val chosenStock: LiveData<Stock> get() = _chosenStock

    fun setChosenStock(stock: Stock) {
        _chosenStock.value = stock
    }

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            repository.addStock(stock)
        }
    }

    fun deleteStock(stock: Stock) {
        viewModelScope.launch {
            repository.deleteStock(stock)
        }
    }

    fun deleteAllStocks() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun isStockEntryValid(stock: Stock): Boolean {
        if (stock.tickerSymbol.isBlank() ||
            stock.buyingPrice.isBlank() ||
            stock.buyingDate.isBlank()) {
            return false
        }
        return true
    }
}