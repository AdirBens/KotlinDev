package com.yehudadir.stocker.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yehudadir.stocker.data.model.ModelConverter
import com.yehudadir.stocker.data.model.Portfolio
import com.yehudadir.stocker.data.model.Stock

@Database(entities = [Stock::class, Portfolio::class], version = 1, exportSchema = false)
@TypeConverters(ModelConverter::class)
abstract class MyStocksDatabase : RoomDatabase() {

    abstract fun portfolioDao(): com.yehudadir.stocker.data.local_db.PortfolioDao
    abstract fun myStockDao(): com.yehudadir.stocker.data.local_db.StocksDao

    companion object {

        @Volatile
        private var INSTANCE: com.yehudadir.stocker.data.local_db.MyStocksDatabase? = null

        fun getDatabase(context: Context): com.yehudadir.stocker.data.local_db.MyStocksDatabase {
            return com.yehudadir.stocker.data.local_db.MyStocksDatabase.Companion.INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.yehudadir.stocker.data.local_db.MyStocksDatabase::class.java,
                    "stocks_database"
                ).build()
                com.yehudadir.stocker.data.local_db.MyStocksDatabase.Companion.INSTANCE = instance
                instance
            }
        }
    }
}
