package com.example.backstreet_cycles.common

/**
 * An enum of classes that determine the state of processes that are successful, loading, or giving
 * errors
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {

    class Success<T>(data: T) : Resource<T>(data)

    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    class Loading<T>(data: T? = null) : Resource<T>(data)
}
