package com.example.stocker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks_table")
data class Stock(
    @PrimaryKey
    @ColumnInfo(name = "ticker_symbol")
    var tickerSymbol: String,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "buying_date")
    var buyingDate: String? = null,

    @ColumnInfo(name = "buying_price")
    var buyingPrice: Float? = null,

    @ColumnInfo(name = "image_uri")
    var imageUri: String? = null,

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false,

    @ColumnInfo(name = "buying_amount")
    var buyingAmount: String? = null,

    @ColumnInfo(name = "stockQuote")
    var stockQuote: StockQuote? = null,

    @ColumnInfo(name = "stockTimeSeries")
    var stockTimeSeries: StockTimeSeries? = null,

    @ColumnInfo(name = "price")
    var stockCurrentPrice: StockCurrentPrice? = null,
)
