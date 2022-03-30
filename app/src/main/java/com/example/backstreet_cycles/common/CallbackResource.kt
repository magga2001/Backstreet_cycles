package com.example.backstreet_cycles.common

interface CallbackResource<T> {

    /**
     * A resource for callback listener
     *
     * @param objects - any generic type
     */
    fun getResult(objects: T)
}