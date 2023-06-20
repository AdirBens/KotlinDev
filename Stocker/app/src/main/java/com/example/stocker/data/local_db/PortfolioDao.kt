package com.example.stocker.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stocker.data.model.Portfolio

@Dao
interface PortfolioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPortfolio(portfolio: Portfolio)

    @Delete
    suspend fun deletePortfolio(portfolio: Portfolio)

    @Update
    suspend fun updatePortfolio(portfolio: Portfolio)

    @Query("SELECT * FROM portfolio_table WHERE id like :id")
    fun getPortfolio(id: Int): LiveData<Portfolio>

}