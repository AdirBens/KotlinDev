package com.yehudadir.stocker.data.model.stockIntermediateComponents

data class StockTimeSeries (
    val meta : StockMetaData,
    val values: List<StockTimeSeriesValue>
    )

data class StockTimeSeriesValue (
    val datetime: String,
    var open: String,
    var close: String
)
