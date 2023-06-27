package com.yehudadir.stocker.data.remote_db


import com.yehudadir.common.Resource
import retrofit2.Response

abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val result = call()
            if (result.isSuccessful) {

                val body = result.body()
                if (result.code() > 300){
                    return Resource.error("Too many requests")
                }
                if (body != null) {
                    return Resource.success(body)
                }
            }
            return Resource.error(
                "Network call has failed for the following reason: ${result.message()} ${result.code()}"
            )
        } catch (e: Exception) {
            return Resource.error(
                "Network call has failed for the following reason: ${e.localizedMessage ?: e.toString()}"
            )
        }
    }
}