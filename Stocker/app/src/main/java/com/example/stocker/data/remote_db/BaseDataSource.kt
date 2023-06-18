package com.example.stocker.data.remote_db

import android.util.Log
import com.example.stocker.utils.Resource
import retrofit2.Response

abstract class BaseDataSource {

    protected suspend fun <T>
            getResult(call : suspend () -> Response<T>) : Resource<T> {

        try {
            Log.d("PRF", "This is a debug message inside getResult" + Resource.success(call().body())) // Debug log

            val result  = call()
            if(result.isSuccessful) {
                val body = result.body()
                if(body != null) return  Resource.success(body)
            }
            return Resource.error("Network call has failed for the following reason: " +
                    "${result.message()} ${result.code()}")
        }catch (e : Exception) {
            return Resource.error("Network call has failed for the following reason: "
             + (e.localizedMessage ?: e.toString()))
        }
    }
}