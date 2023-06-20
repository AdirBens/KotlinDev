package com.example.stocker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolio_table")
data class Portfolio(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "current_value")
    var currentValue: Float,

    @ColumnInfo(name = "buying_value")
    var buyingValue: Float,

    @ColumnInfo(name = "portfolio_change_percent")
    var portfolioChangePercent: Float,


)
