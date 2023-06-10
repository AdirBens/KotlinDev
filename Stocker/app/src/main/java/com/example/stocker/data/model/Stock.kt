package com.example.stocker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks_table")
data class Stock (
    @ColumnInfo(name = "ticker_symbol")
    val tickerSymbol:String,

    @ColumnInfo(name = "description")
    val description:String,

    @ColumnInfo(name = "buying_price")
    val buyingPrice:String,

    @ColumnInfo(name = "buying_date")
    val buyingDate:String,

    @ColumnInfo(name = "image_uri")
    val imageUri:String?)

    {
    @PrimaryKey(autoGenerate = true)
        var id:Long = 0
    }
