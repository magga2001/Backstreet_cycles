package com.example.backstreet_cycles.domain.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * Helper class for managing user's shared preferences
 */
object SharedPrefHelper {

    /**
     * Initialise shared preferences
     */
    private lateinit var sharedPref: SharedPreferences
    private lateinit var key: String
    private lateinit var application: Application

    fun initialiseSharedPref(application: Application, key: String) {
        this.application = application
        this.key = key

        sharedPref = application.getSharedPreferences(
            key, Context.MODE_PRIVATE
        )
    }

        /**
         * Checks if shared preference is empty
         * @param key shared preference key
         * @return Boolean
         */
        fun checkIfSharedPrefEmpty(key: String): Boolean {
            val serializedObject: String? = sharedPref.getString(key, null)
            if (serializedObject == null) {
                return true
            }
            return false
        }

    /**
     * Edit share preferences
     * @param values
     * @param type
     */
    fun <T> overrideSharedPref(values: MutableList<T>, type: Class<T>) {
        val json = JsonHelper.objectToString(values, type)
        with(sharedPref.edit()) {
            putString(key, json)
            apply()
        }
    }

    /**
     * Clear shared preferences
     */
    fun clearSharedPreferences() {
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    /**
     * Get shared preferences
     */
    fun <T> getSharedPref(type: Class<T>): MutableList<T> {
        val serializedObject: String? =
            sharedPref.getString(key, null)
        return if (serializedObject != null) {
            JsonHelper.stringToObject(serializedObject, type)!!.toMutableList()
        } else {
            emptyList<T>().toMutableList()
        }
    }

    /**
     * Change key for shared preferences
     */
    fun changeSharedPref(key: String) {
        this.key = key
        sharedPref = application.getSharedPreferences(
            key, Context.MODE_PRIVATE
        )
    }

}