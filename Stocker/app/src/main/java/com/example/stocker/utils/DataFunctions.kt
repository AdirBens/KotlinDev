package com.example.stocker.utils

import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType
import okhttp3.RequestBody

fun <T,A> performFetchingAndSaving (localDbFetch: () -> LiveData<T>,
                                   remoteDbFetch: suspend () -> Resource<A>,
                                   localDbSave: suspend (A) -> Unit) : LiveData<Resource<T>> =

    liveData(Dispatchers.IO) {

        emit(Resource.loading())

        val source = localDbFetch().map { Resource.success(it) }
        emitSource(source)

        val fetchResource = remoteDbFetch()

        if(fetchResource.status is Success)
            localDbSave(fetchResource.status.data!!)

        else if(fetchResource.status is Error)
        {
            emit(Resource.error(fetchResource.status.message))
            emitSource(source)
        }
    }

fun <A> performRemoteFetching(remoteDbFetch: suspend () -> Resource<A>): LiveData<Resource<A>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())

        val fetchResource = remoteDbFetch()
        Log.d("PRF", "This is a debug message inside performRemoteFetch" + fetchResource.status.data.toString()) // Debug log
        if (fetchResource.status is Success) {
            emit(Resource.success(fetchResource.status.data!!))
        } else if (fetchResource.status is Error) {
            emit(Resource.error(fetchResource.status.message))
        }
    }