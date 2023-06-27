package com.yehudadir.stocker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yehudadir.stocker.common.Resource
import com.yehudadir.stocker.data.model.entities.Portfolio
import com.yehudadir.stocker.data.model.entities.PortfolioTimeSeriesValue
import com.yehudadir.stocker.data.model.entities.Stock
import com.yehudadir.stocker.data.repository.StockRepository
import com.yehudadir.stocker.utils.convertDateFormat
import com.yehudadir.stocker.utils.convertStringToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
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
//            portfolio.value!!.status.data!!.currentValue = 0f
//            portfolio.value!!.status.data!!.buyingValue = 0f
//            portfolio.value!!.status.data!!.portfolioValueTimeSeries.clear()
//
//            stockRepository.updatePortfolio(portfolio.value!!.status.data!!)
            portfolio.value!!.currentValue = 0f
            portfolio.value!!.buyingValue = 0f
            portfolio.value!!.portfolioValueTimeSeries.clear()

            stockRepository.updatePortfolio(portfolio.value!!)
            stockRepository.deleteAll()
        }
    }

    fun addStockDataToPortfolio(stock: Stock) {
        GlobalScope.launch(Dispatchers.IO) {
            val portfolioValueTimeSeries =
                portfolio.value?.portfolioValueTimeSeries ?: mutableListOf()
            val currentDateTime = Calendar.getInstance()
            val buyingDate = convertStringToDate(convertDateFormat(stock.buyingDate!!))
            synchronized(portfolioLock) {
                stock.stockTimeSeries?.values?.forEach { timeSeriesValue ->
                    val timeSeriesDateTime = convertStringToDate(timeSeriesValue.datetime)
                    if (timeSeriesDateTime >= buyingDate && timeSeriesDateTime < currentDateTime) {
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
        GlobalScope.launch(Dispatchers.IO) {
            val portfolioValueTimeSeries =
                portfolio.value?.portfolioValueTimeSeries ?: mutableListOf()
            val currentDateTime = Calendar.getInstance()
            val buyingDate = convertStringToDate(convertDateFormat(stock.buyingDate!!))
            synchronized(portfolioLock) {
                stock.stockTimeSeries?.values?.forEach { timeSeriesValue ->
                    val timeSeriesDateTime = convertStringToDate(timeSeriesValue.datetime)

                    if (timeSeriesDateTime >= buyingDate && timeSeriesDateTime != currentDateTime) {
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
}

