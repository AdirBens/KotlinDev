package com.example.stocker.ui

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.stocker.data.model.Stock
import com.example.stocker.data.model.StockCurrentPrice
import com.example.stocker.data.model.StockImageURL
import com.example.stocker.data.model.StockQuote
import com.example.stocker.data.model.StockTimeSeries
import com.example.stocker.data.repository.StockRepository
import com.example.stocker.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockRepository : StockRepository) : ViewModel() {

    private val _chosenStockSymbol = MutableLiveData<String>()
    val chosenStockSymbol: MutableLiveData<String> get() = _chosenStockSymbol

    private val _chosenStock = MutableLiveData<Stock>()
    val chosenStock: MutableLiveData<Stock> get() = _chosenStock

    private var _stockLogo = _chosenStockSymbol.switchMap {
        stockRepository.getStockLogo(it)
    }
    val stockLogo: LiveData<Resource<StockImageURL>> = _stockLogo

    private var _stockQuote = _chosenStockSymbol.switchMap {
        stockRepository.getQuote(it)
    }
    val stockQuote: LiveData<Resource<StockQuote>> = _stockQuote

    private var _stockTimeSeries = _chosenStockSymbol.switchMap {
        stockRepository.getTimeSeries(it, interval = "1day", outputSize = "5000")
    }
    val stockTimeSeries: LiveData<Resource<StockTimeSeries>> = _stockTimeSeries

    private var _stockCurrentPrice = _chosenStockSymbol.switchMap {
        stockRepository.getCurrentPrice(it)
    }
    val stockCurrentPrice: LiveData<Resource<StockCurrentPrice>> = _stockCurrentPrice

    fun setSymbol(symbol: String) {
        _chosenStockSymbol.value = symbol
    }

    fun setChosenStock(stock: Stock) {
        _chosenStock.value = stock
        setSymbol(stock.tickerSymbol)
    }

    fun updateStock(stock: Stock)
    {
        viewModelScope.launch {
            stockRepository.updateStock(stock)
        }
    }

    fun isStockEntryValid(stock: Stock): Boolean {
        if (stock.buyingAmount?.equals("")==true) {
            return false
        }
        if (stock.buyingDate?.equals("")==true) {
            return false
        }
        return true
    }
}