package com.yehudadir.stocker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.yehudadir.stocker.common.Error
import com.yehudadir.stocker.common.Resource
import com.yehudadir.stocker.common.Success
import kotlinx.coroutines.Dispatchers

fun <T> performLocalFetching(localDbFetch: () -> LiveData<T>): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val source = localDbFetch().map { Resource.success(it) }
        emitSource(source)
    }

fun <A> performRemoteFetching(remoteDbFetch: suspend () -> Resource<A>): LiveData<Resource<A>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val fetchResource = remoteDbFetch()
        if (fetchResource.status is Success) {
            emit(Resource.success(fetchResource.status.data!!))
        } else if (fetchResource.status is Error) {
            emit(Resource.error(fetchResource.status.message))
        }
    }

fun <T> performLocalFetchingNoResource(localDbFetch: () -> LiveData<T>): LiveData<T> =
    liveData(Dispatchers.IO) {
        val source = localDbFetch().map { it }
        emitSource(source)
    }