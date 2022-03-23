package com.example.backstreet_cycles.common

interface CallbackResource<T> {

    fun getResult(objects: T)
}