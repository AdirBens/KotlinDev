package com.example.stocker.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson

data class StockTimeSeries (
    val meta : StockMetaData,
    val values: List<StockTimeSeriesValue>
        )

class StockTimeSeriesConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): StockTimeSeries? {
        return gson.fromJson(value, StockTimeSeries::class.java)
    }

    @TypeConverter
    fun toString(value: StockTimeSeries?): String? {
        return gson.toJson(value)
    }
}