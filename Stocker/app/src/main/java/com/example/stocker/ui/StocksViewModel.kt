package com.example.stocker.ui

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocker.data.model.Portfolio
import com.example.stocker.data.model.Stock
import com.example.stocker.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {

    private val _chosenStock = MutableLiveData<Stock>()
    val chosenStock: MutableLiveData<Stock> get() = _chosenStock

    val stocks: LiveData<List<Stock>> = stockRepository.getStocks()

    val portfolio: LiveData<Portfolio> = stockRepository.getPortfolio(1)

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

    fun updatePortfolio(stock: Stock, context: Context) {
        viewModelScope.launch {
            portfolio.value!!.currentValue += stock.stockQuote?.close!!.toFloat() * stock.buyingAmount!!.toFloat()
            portfolio.value!!.buyingValue += stock.buyingPrice!! * stock.buyingAmount!!.toFloat()
            stockRepository.updatePortfolio(portfolio.value!!)
            Toast.makeText(context, "Portfolio: ${portfolio.value}", Toast.LENGTH_SHORT).show()
        }
    }

    fun addPortfolio(portfolio: Portfolio) {
        viewModelScope.launch {
            stockRepository.addPortfolio(portfolio)
        }
    }


    fun setChosenStock(stock: Stock) {
        _chosenStock.value = stock
    }
}
