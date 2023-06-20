package com.example.stocker.data.model

import androidx.room.Entity

@Entity(tableName = "symbol_search_table")
data class SymbolSearch(
    val data: ArrayList<SearchItemMetaData>,
    val status: String
)

data class SearchItemMetaData(
    val symbol: String,
    val instrument_name: String,
    val exchange: String,
    val exchange_timezone: String,
    val country: String,
    val currency: String,
    val access: StockAccess
)

data class StockAccess (
    val global: String,
    val plan: String
)