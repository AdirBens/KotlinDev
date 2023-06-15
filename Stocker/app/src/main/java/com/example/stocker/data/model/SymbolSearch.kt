package com.example.stocker.data.model

import androidx.room.Entity

@Entity(tableName = "symbol_search_table")
data class SymbolSearch(
        val data: ArrayList<StockMetaData>,
        val status: String
) {

}