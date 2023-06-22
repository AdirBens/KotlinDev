package com.example.stocker.data.model

data class StockTimeSeries (
    val meta : StockMetaData,
    val values: List<StockTimeSeriesValue>
    )

data class StockTimeSeriesValue (
    val datetime: String,
    var open: String,
    var close: String
)
