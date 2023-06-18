package com.example.stocker.data.model

import android.net.Uri

data class StockImageURL (
    val metaData: StockMetaData,
    val url: String,
    val status: String? = null
    ){

}