package com.example.stocker.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson

data class StockQuote(
    val symbol: String,
    val name: String,
    val exchange: String,
    val mic_code: String,
    val currency: String,
    val datetime: String,
    val timestamp: Long,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    val previous_close: String,
    val change: String,
    val percent_change: String,
    val average_volume: String,
    val rolling_1d_change: String,
    val rolling_7d_change: String,
    val rolling_period_change: String,
    val is_market_open: Boolean,

)