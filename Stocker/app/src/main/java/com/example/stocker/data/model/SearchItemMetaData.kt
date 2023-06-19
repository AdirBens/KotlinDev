package com.example.stocker.data.model

data class SearchItemMetaData(
val symbol: String,
val instrument_name: String,
val exchange: String,
val exchange_timezone: String,
val country: String,
val currency: String,
val access: StockAccess
)