package com.yehudadir.stocker.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yehudadir.stocker.data.model.entities.PortfolioTimeSeriesValue
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockCurrentPrice
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockImageURL
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockQuote
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockTimeSeries
import java.util.concurrent.ConcurrentLinkedQueue

class ModelConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromConcurrentLinkedQueueToString(value: ConcurrentLinkedQueue<PortfolioTimeSeriesValue>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToConcurrentLinkedQueue(value: String?): ConcurrentLinkedQueue<PortfolioTimeSeriesValue>? {
        val type = object : TypeToken<ConcurrentLinkedQueue<PortfolioTimeSeriesValue>?>() {}.type
        return gson.fromJson(value, type)
    }



//    @TypeConverter
//    fun fromArrayListToString(value: ArrayList<PortfolioTimeSeriesValue>?): String? {
//        return gson.toJson(value)
//    }
//
//    @TypeConverter
//    fun fromStringToArrayList(value: String?): ArrayList<PortfolioTimeSeriesValue>? {
//        val type = object : TypeToken<ArrayList<PortfolioTimeSeriesValue>?>() {}.type
//        return gson.fromJson(value, type)
//    }



    @TypeConverter
    fun fromStringToStockTimeSeries(value: String?): StockTimeSeries? {
        return gson.fromJson(value, StockTimeSeries::class.java)
    }

    @TypeConverter
    fun fromStockTimeSeriesToString(value: StockTimeSeries?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToStockQuote(value: String?): StockQuote? {
        return gson.fromJson(value, StockQuote::class.java)
    }

    @TypeConverter
    fun fromStockQuoteToString(value: StockQuote?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToStockImageURL(value: String?): StockImageURL? {
        return gson.fromJson(value, StockImageURL::class.java)
    }

    @TypeConverter
    fun fromStockImageURLToString(value: StockImageURL?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToStockCurrentPrice(value: String?): StockCurrentPrice? {
        return gson.fromJson(value, StockCurrentPrice::class.java)
    }

    @TypeConverter
    fun fromStockCurrentPriceToString(value: StockCurrentPrice?): String? {
        return gson.toJson(value)
    }
}
