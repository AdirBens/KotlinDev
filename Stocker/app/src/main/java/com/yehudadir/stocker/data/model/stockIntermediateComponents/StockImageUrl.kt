package com.yehudadir.stocker.data.model.stockIntermediateComponents

data class StockImageURL (
    val metaData: StockMetaData,
    val url: String,
    val status: String? = null
    )