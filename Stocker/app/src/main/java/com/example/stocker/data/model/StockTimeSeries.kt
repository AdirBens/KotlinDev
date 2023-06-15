package com.example.stocker.data.model

data class StockTimeSeries (
    val meta : StockMetaData,
    val values: List<StockTimeSeriesValue>
        ){
}