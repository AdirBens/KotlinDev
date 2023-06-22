package com.example.stocker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocker.data.model.Portfolio
import com.example.stocker.data.model.PortfolioTimeSeriesValue
import com.example.stocker.data.model.Stock
import com.example.stocker.data.repository.StockRepository
import com.example.stocker.utils.convertDateFormat
import com.example.stocker.utils.convertStringToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {

    private val _chosenStock = MutableLiveData<Stock>()
    val chosenStock: MutableLiveData<Stock> get() = _chosenStock

    val stocks: LiveData<List<Stock>> = stockRepository.getStocks()

    var portfolio: LiveData<Portfolio> = stockRepository.getPortfolio(1)

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            stockRepository.addStock(stock)
        }
    }

    fun updateStock(oldStock:Stock, stock: Stock)
    {
        viewModelScope.launch {
            removeStockDataFromPortfolio(oldStock)
            addStockDataToPortfolio(stock)
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
            portfolio.value!!.currentValue = 0f
            portfolio.value!!.buyingValue = 0f
            portfolio.value!!.portfolioValueTimeSeries.clear()
            stockRepository.updatePortfolio(portfolio.value!!)
            stockRepository.deleteAll()
        }
    }

    fun addStockDataToPortfolio(stock: Stock) {
        viewModelScope.launch {
            stock.stockTimeSeries?.values?.forEach { it1 ->
                if (convertStringToDate(it1.datetime) >= convertStringToDate(convertDateFormat(stock.buyingDate!!)) &&
                    convertStringToDate(it1.datetime) < Calendar.getInstance()
                ) {
                    val currentTimeSeriesValue = portfolio.value?.portfolioValueTimeSeries?.find { it.date == it1.datetime }
                    if (currentTimeSeriesValue != null) {
                        currentTimeSeriesValue.close += it1.close.toFloat() * stock.buyingAmount!!.toFloat()
                        currentTimeSeriesValue.open += it1.open.toFloat() * stock.buyingAmount!!.toFloat()
                        currentTimeSeriesValue.numOfStocks += 1
                    } else {
                        val timeSeriesToAdd = PortfolioTimeSeriesValue(
                            it1.datetime,
                            it1.close.toFloat() * stock.buyingAmount!!.toFloat(),
                            it1.open.toFloat() * stock.buyingAmount!!.toFloat(),
                            1
                        )
                        portfolio.value!!.portfolioValueTimeSeries.add(timeSeriesToAdd)
                    }
                }
            }
            portfolio.value!!.currentValue += stock.stockQuote?.close!!.toFloat() * stock.buyingAmount!!.toFloat()
            portfolio.value!!.buyingValue += stock.buyingPrice!! * stock.buyingAmount!!.toFloat()
            stockRepository.updatePortfolio(portfolio.value!!)
        }
    }

    fun removeStockDataFromPortfolio(stock: Stock) {
        viewModelScope.launch {
            stock.stockTimeSeries?.values?.forEach { it1 ->
                if (convertStringToDate(it1.datetime) >= convertStringToDate(convertDateFormat(stock.buyingDate!!))&& convertStringToDate(
                        it1.datetime
                    ) != Calendar.getInstance()) {
                    val currentTimeSeriesValue = portfolio.value?.portfolioValueTimeSeries?.find { it.date == it1.datetime }
                    currentTimeSeriesValue!!.close -= it1.close.toFloat() * stock.buyingAmount!!.toInt()
                    currentTimeSeriesValue!!.open -= it1.open.toFloat() * stock.buyingAmount!!.toInt()
                if (currentTimeSeriesValue?.numOfStocks == 1) {
                    portfolio.value!!.portfolioValueTimeSeries.remove(currentTimeSeriesValue)
                } else {
                    currentTimeSeriesValue!!.numOfStocks -= 1
                }
                }
            }
            portfolio.value!!.currentValue -= stock.stockQuote?.close!!.toFloat() * stock.buyingAmount!!.toFloat()
            portfolio.value!!.buyingValue -= stock.buyingPrice!! * stock.buyingAmount!!.toFloat()
            stockRepository.updatePortfolio(portfolio.value!!)
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

