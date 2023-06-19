package com.example.stocker.data.model

data class StockImageURL (
    val metaData: StockMetaData,
    val url: String,
    val status: String? = null
    )