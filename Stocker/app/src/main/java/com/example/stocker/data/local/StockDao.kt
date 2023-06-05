package com.example.stocker.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stocker.data.model.Stock

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStock(stock: Stock)

    @Delete
    suspend fun deleteStock(vararg stock: Stock)


    @Update
//    TODO: Not implemented yet
    suspend fun updateStock(stock: Stock)

    @Query("SELECT * FROM stocks_table ORDER BY ticker_symbol ASC")
    fun getStocks() : LiveData<List<Stock>>

    @Query("SELECT * FROM stocks_table WHERE ticker_symbol like :tickerSymbol")
    suspend fun getStock(tickerSymbol:String) : Stock

    @Query("DELETE FROM stocks_table")
    suspend fun deleteAll()
}