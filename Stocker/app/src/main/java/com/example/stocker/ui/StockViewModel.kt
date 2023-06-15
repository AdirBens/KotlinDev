package com.example.stocker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocker.data.model.Stock
import com.example.stocker.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockRepository : StockRepository) : ViewModel() {

    val stocks : LiveData<List<Stock>>? = stockRepository.getStocks()

    private val _chosenStock = MutableLiveData<Stock>()
    val chosenStock: MutableLiveData<Stock> get() = _chosenStock


    fun setChosenStock(stock: Stock) {
        _chosenStock.value = stock
    }

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            stockRepository.addStock(stock)
        }
    }

    fun deleteStock(stock: Stock) {
        viewModelScope.launch {
            stockRepository.deleteStock(stock)
        }
    }

    fun deleteAllStocks() {
        viewModelScope.launch {
            stockRepository.deleteAll()
        }
    }

    fun updateStock(stock: Stock)
    {
        viewModelScope.launch {
            stockRepository.updateStock(stock)
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