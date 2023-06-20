package com.example.stocker.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class StockMetaData(
    val symbol: String,
    val exchange: String,
    val exchange_timezone: String,
    val country: String,
    val currency: String,
    val access: StockAccess
)

