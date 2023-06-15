package com.example.stocker.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stocker.data.model.Stock

@Database(entities = arrayOf(Stock::class), version = 1, exportSchema = false)
abstract class MyStocksDatabase : RoomDatabase(){

    abstract fun myStockDao(): MyStocksDao

    companion object{

        @Volatile
        private var instance: MyStocksDatabase? = null

        fun getDatabase(context: Context) : MyStocksDatabase =
            instance ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext,
                MyStocksDatabase::class.java,"stocks_database").fallbackToDestructiveMigration()
                .build().also { instance = it }
        }
    }
}
