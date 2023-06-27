package com.yehudadir.stocker.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yehudadir.stocker.data.model.Stock

@Dao
interface StocksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStock(stock: Stock)

    @Delete
    suspend fun deleteStock(vararg stock: Stock)

    @Update
    suspend fun updateStock(updatedStock: Stock)

    @Query("SELECT * FROM stocks_table ORDER BY ticker_symbol ASC")
    fun getStocks() : LiveData<List<Stock>>

    @Query("SELECT * FROM stocks_table WHERE favorite = 1 ORDER BY ticker_symbol ASC")
    fun getFavoriteStocks(): LiveData<List<Stock>>

    @Query("SELECT * FROM stocks_table WHERE ticker_symbol like :tickerSymbol")
    fun getStock(tickerSymbol:String): LiveData<Stock>

    @Query("DELETE FROM stocks_table")
    suspend fun deleteAll()
}