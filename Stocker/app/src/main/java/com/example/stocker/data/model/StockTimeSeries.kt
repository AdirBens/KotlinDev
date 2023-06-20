package com.example.stocker.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson

data class StockTimeSeries (
    val meta : StockMetaData,
    val values: List<StockTimeSeriesValue>
    )

data class StockTimeSeriesValue (
    val datetime: String,
    val avgprice: String
)
