package com.example.stocker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks_table")
data class Stock (
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
    var favorite:Boolean = false
)
    {
    @PrimaryKey(autoGenerate = true)
        var id:Long = 0
    }
