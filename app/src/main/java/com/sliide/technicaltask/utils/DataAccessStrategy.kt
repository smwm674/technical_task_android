package com.sliide.technicaltask.utils

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers


fun <A> NetworkOnly(
    networkCall: suspend () -> Resource<A>
): LiveData<Resource<A>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val responseStatus = networkCall.invoke()
        if (responseStatus.status == Resource.Status.SUCCESS) {
            Resource.success(responseStatus.code, responseStatus.data)
            emitSource(MutableLiveData(responseStatus))
        } else if (responseStatus.status == Resource.Status.ERROR) {
            emit(Resource.error(responseStatus.message!!))
        }
    }