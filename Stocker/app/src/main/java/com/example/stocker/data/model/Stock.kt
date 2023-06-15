package com.example.stocker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks_table")
data class Stock (
    @PrimaryKey
    @ColumnInfo(name = "ticker_symbol")
    var tickerSymbol:String,

    @ColumnInfo(name = "description")
    var description:String,

    @ColumnInfo(name = "buying_price")
    var buyingPrice:String,

    @ColumnInfo(name = "buying_date")
    var buyingDate:String,

    @ColumnInfo(name = "image_uri")
    var imageUri:String?,

    @ColumnInfo(name = "favorite")
    var favorite:Boolean = false,

    @ColumnInfo(name = "exchange")
    var exchange:String? = null,

    @ColumnInfo(name = "exchange_timezone")
    var exchangeTimezone:String? = null,

    @ColumnInfo(name = "country")
    var country:String? = null,

    @ColumnInfo(name = "currency")
    var currency:String? = null
)
