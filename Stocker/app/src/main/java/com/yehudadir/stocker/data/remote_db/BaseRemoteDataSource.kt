package com.yehudadir.stocker.data.remote_db

import android.content.res.Resources
import com.yehudadir.stocker.R
import retrofit2.Response
import java.lang.Exception

import com.yehudadir.stocker.common.Resource


abstract class BaseRemoteDataSource {
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val results = call()

            if (results.isSuccessful) {
                val headers = results.headers()
                val body = results.body()

                if (headers["Api-Credits-Left"] == "0") {
                    return Resource.error(Resources.getSystem().getString(R.string.out_of_credit))
                }

                else if (body != null) {
                    return Resource.success(body)
                }
            }

            return Resource.error(
                "Network call has failed for the following reason: ${results.message()} ${results.code()}"
            )
        }

        catch (e: Exception) {
            return Resource.error(
                "Network call has failed for the following reason: ${e.localizedMessage ?: e.toString()}"
            )
        }
    }
}