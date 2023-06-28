package com.yehudadir.stocker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.yehudadir.stocker.data.model.entities.Stock
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockCurrentPrice
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockImageURL
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockQuote
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockTimeSeries
import com.yehudadir.stocker.data.repository.StockRepository
import com.yehudadir.stocker.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {
    private val _chosenStockSymbol = MutableLiveData<String>()
    private val _chosenStock = MutableLiveData<Stock>()

    private var _stockLogo = _chosenStockSymbol.switchMap {
        stockRepository.getStockLogo(it)
    }
    private var _stockQuote = _chosenStockSymbol.switchMap {
        stockRepository.getQuote(it)
    }
    private var _stockTimeSeries = _chosenStockSymbol.switchMap {
        stockRepository.getTimeSeries(it, interval = "1day", outputSize = "5000")
    }
    private var _stockCurrentPrice = _chosenStockSymbol.switchMap {
        stockRepository.getCurrentPrice(it)
    }

    val stockLogo: LiveData<Resource<StockImageURL>> = _stockLogo
    val chosenStockSymbol: MutableLiveData<String> get() = _chosenStockSymbol
    val stockQuote: LiveData<Resource<StockQuote>> = _stockQuote
    val stockTimeSeries: LiveData<Resource<StockTimeSeries>> = _stockTimeSeries
    val stockCurrentPrice: LiveData<Resource<StockCurrentPrice>> = _stockCurrentPrice
    val chosenStock: MutableLiveData<Stock> get() = _chosenStock

    fun setSymbol(symbol: String) {
        _chosenStockSymbol.value = symbol
    }

    fun setChosenStock(stock: Stock) {
        _chosenStock.value = stock
        setSymbol(stock.tickerSymbol)
    }

    fun isStockEntryValid(stock: Stock): Boolean {
        if (stock.buyingAmount?.equals("") == true) {
            return false
        }
        if (stock.buyingDate?.equals("") == true) {
            return false
        }
        return true
    }
}