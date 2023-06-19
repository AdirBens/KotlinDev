package com.example.stocker.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class StockMetaData(
    //When accessing the meta using time_series an interval parameter is added, do we need it?
    val symbol: String,
    val exchange: String,
    val exchange_timezone: String,
    val country: String,
    val currency: String,
    val access: StockAccess
) {
}
    class StockMetaDataConverter {
        private val gson = Gson()

        @TypeConverter
        fun fromString(value: String?): StockMetaData? {
            return gson.fromJson(value, StockMetaData::class.java)
        }

        @TypeConverter
        fun toString(value: StockMetaData?): String? {
            return gson.toJson(value)
        }
    }

