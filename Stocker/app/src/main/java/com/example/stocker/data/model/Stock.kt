package com.example.stocker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "stocks_table")
@TypeConverters(
    StockMetaDataConverter::class,
    StockQuoteConverter::class,
    StockTimeSeriesConverter::class
)
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
    var stockTimeSeries: StockTimeSeries? = null
)
