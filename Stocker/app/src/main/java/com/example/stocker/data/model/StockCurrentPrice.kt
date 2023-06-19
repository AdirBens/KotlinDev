package com.example.stocker.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson

data class StockCurrentPrice (
        val price: String
    )


class StockCurrentPriceConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): StockCurrentPrice? {
        return gson.fromJson(value, StockCurrentPrice::class.java)
    }

    @TypeConverter
    fun toString(value: StockCurrentPrice?): String? {
        return gson.toJson(value)
    }
}
