package com.example.stocker.data.model

import com.google.gson.annotations.SerializedName

data class StockMetaData(
    //When accessing the meta using time_series an interval parameter is added, do we need it?
    val symbol: String,
    val exchange: String,
    val exchange_timezone: String,
    val country: String,
    val currency: String
){

}