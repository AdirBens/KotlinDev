package com.yehudadir.stocker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yehudadir.stocker.common.Resource
import com.yehudadir.stocker.data.model.entities.Portfolio
import com.yehudadir.stocker.data.model.entities.PortfolioTimeSeriesValue
import com.yehudadir.stocker.data.model.entities.Stock
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockTimeSeriesValue
import com.yehudadir.stocker.data.repository.StockRepository
import com.yehudadir.stocker.utils.convertDateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
    private val _chosenStock = MutableLiveData<Stock>()
    val chosenStock: MutableLiveData<Stock> get() = _chosenStock
    val stocks: LiveData<Resource<List<Stock>>> = stockRepository.getAllStocks()
    var portfolio: LiveData<Portfolio> = stockRepository.getPortfolio(1)
    private val portfolioLock = Any()

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            stockRepository.addStock(stock)
        }
    }

    fun updateStock(oldStock: Stock, stock: Stock) {
        removeStockDataFromPortfolio(oldStock)
        addStockDataToPortfolio(stock)
        viewModelScope.launch(Dispatchers.IO) {
            stockRepository.updateStock(stock)
        }
    }

    fun deleteStock(stock: Stock) {
        removeStockDataFromPortfolio(stock)
        viewModelScope.launch {
            stockRepository.deleteStock(stock)
        }
    }

    fun deleteAllStocks() {
        viewModelScope.launch {
            val emptyPortfolio = Portfolio(1, 0f, 0f, 0f, ConcurrentLinkedQueue())
            stockRepository.updatePortfolio(emptyPortfolio)
            stockRepository.deleteAll()
        }
    }

    fun addStockDataToPortfolio(stock: Stock) {
        viewModelScope.launch(Dispatchers.IO) {
            synchronized(portfolioLock) {
                val portfolioValueTimeSeries =
                    portfolio.value?.portfolioValueTimeSeries ?: mutableListOf()
                val stocksSeriesValues = stock.stockTimeSeries?.values!!
                val index = getIndexByDate(stocksSeriesValues, stock.buyingDate!!)

                stock.stockTimeSeries?.values?.slice(0 until index + 1)
                    ?.forEach { timeSeriesValue ->
                        val currentTimeSeriesValue =
                            portfolioValueTimeSeries.find { it.date == timeSeriesValue.datetime }

                        if (currentTimeSeriesValue != null) {
                            currentTimeSeriesValue.close += timeSeriesValue.close.toFloat() * stock.buyingAmount!!.toFloat()
                            currentTimeSeriesValue.open += timeSeriesValue.open.toFloat() * stock.buyingAmount!!.toFloat()
                            currentTimeSeriesValue.numOfStocks += 1
                        } else {
                            val timeSeriesToAdd = PortfolioTimeSeriesValue(
                                timeSeriesValue.datetime,
                                timeSeriesValue.close.toFloat() * stock.buyingAmount!!.toFloat(),
                                timeSeriesValue.open.toFloat() * stock.buyingAmount!!.toFloat(),
                                1
                            )
                            portfolioValueTimeSeries.add(timeSeriesToAdd)
                        }
                    }
            }

            val stockQuoteClose = stock.stockQuote?.close?.toFloat() ?: 0.0f
            val buyingPrice = stock.buyingPrice ?: 0.0
            val buyingAmount = stock.buyingAmount?.toFloat() ?: 0

            portfolio.value!!.currentValue += stockQuoteClose * buyingAmount.toFloat()
            portfolio.value!!.buyingValue += buyingPrice.toFloat() * buyingAmount.toFloat()
            stockRepository.updatePortfolio(portfolio.value!!)
        }
    }

    private fun removeStockDataFromPortfolio(stock: Stock) {
        viewModelScope.launch(Dispatchers.IO) {
            synchronized(portfolioLock) {
                val portfolioValueTimeSeries =
                    portfolio.value?.portfolioValueTimeSeries ?: mutableListOf()
                val stocksSeriesValues = stock.stockTimeSeries?.values!!
                val index = getIndexByDate(stocksSeriesValues, stock.buyingDate!!)

                stock.stockTimeSeries?.values?.slice(0 until index + 1)
                    ?.forEach { timeSeriesValue ->
                        val currentTimeSeriesValue =
                            portfolioValueTimeSeries.find { it.date == timeSeriesValue.datetime }

                        currentTimeSeriesValue!!.close -= timeSeriesValue.close.toFloat() * stock.buyingAmount!!.toInt()
                        currentTimeSeriesValue!!.open -= timeSeriesValue.open.toFloat() * stock.buyingAmount!!.toInt()

                        if (currentTimeSeriesValue?.numOfStocks == 1) {
                            portfolioValueTimeSeries.remove(currentTimeSeriesValue)
                        } else {
                            currentTimeSeriesValue!!.numOfStocks -= 1
                        }
                    }
            }

            val stockQuoteClose = stock.stockQuote?.close?.toFloat() ?: 0.0f
            val buyingPrice = stock.buyingPrice ?: 0.0
            val buyingAmount = stock.buyingAmount?.toFloat() ?: 0

            portfolio.value!!.currentValue -= stockQuoteClose * buyingAmount.toFloat()
            portfolio.value!!.buyingValue -= buyingPrice.toFloat() * buyingAmount.toFloat()
            stockRepository.updatePortfolio(portfolio.value!!)
        }
    }

    fun addPortfolio(portfolio: Portfolio) {
        viewModelScope.launch(Dispatchers.IO) {
            stockRepository.addPortfolio(portfolio)
        }
    }

    fun setChosenStock(stock: Stock) {
        _chosenStock.value = stock
    }

    private fun getIndexByDate(
        stocksSeriesValues: List<StockTimeSeriesValue>,
        buyingDate: String
    ): Int {
        val searchDate = convertDateFormat(buyingDate)
        var index = stocksSeriesValues.binarySearch {
            String.CASE_INSENSITIVE_ORDER.reversed().compare(it.datetime, searchDate)
        }

        if (index < 0) {
            index = stocksSeriesValues.size - 1
        }

        return index
    }
}