package com.example.backstreet_cycles.domain.utils

import android.view.View

import com.google.android.material.snackbar.Snackbar

/**
 * Helper class for managing implementation of the snackbar
 */
object SnackBarHelper {

    fun displaySnackBar(view: View?, s: String?) {
        val snack = Snackbar.make(view!!, s!!, Snackbar.LENGTH_LONG)
        snack.show()
    }

}