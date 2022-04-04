package com.example.backstreet_cycles.common

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource


/**
 * Class for fetching remote data for espresso tests
 */
object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}