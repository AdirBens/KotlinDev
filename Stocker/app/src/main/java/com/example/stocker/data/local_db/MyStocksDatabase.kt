package com.example.stocker.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stocker.data.model.ModelConverter
import com.example.stocker.data.model.Portfolio
import com.example.stocker.data.model.Stock

@Database(entities = [Stock::class, Portfolio::class], version = 1, exportSchema = false)
@TypeConverters(ModelConverter::class)
abstract class MyStocksDatabase : RoomDatabase() {

    abstract fun portfolioDao(): PortfolioDao
    abstract fun myStockDao(): StocksDao

    companion object {

        @Volatile
        private var INSTANCE: MyStocksDatabase? = null

        fun getDatabase(context: Context): MyStocksDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyStocksDatabase::class.java,
                    "stocks_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
