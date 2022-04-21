package com.sliide.technicaltask.utils

data class Resource<out T>(val code: Int, val status: Status, val data: T?, val message: String?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(code: Int, data: T?): Resource<T> {
            return Resource(code, Status.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(0,Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(0,Status.LOADING, data, null)
        }
    }
}