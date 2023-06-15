package com.example.stocker.di

import ApiKeyInterceptor
import android.content.Context
import com.example.stocker.data.local_db.MyStocksDatabase
import com.example.stocker.data.remote_db.StockRemoteDataSource
import com.example.stocker.data.remote_db.StockService
import com.example.stocker.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(Constants.API_KEY))
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    fun provideStockService(retrofit: Retrofit): StockService =
        retrofit.create(StockService::class.java)

    @Provides
    @Singleton
    fun provideLocalDataBase(@ApplicationContext appContext: Context): MyStocksDatabase =
        MyStocksDatabase.getDatabase(appContext)

    @Provides
    @Singleton
    fun provideStockDao(database: MyStocksDatabase) = database.myStockDao()


}