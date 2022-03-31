package com.example.backstreet_cycles.domain.utils

import android.app.Application
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar

import com.google.android.material.snackbar.Snackbar

/**
 * A class created to help us with the implementation of the snackbar
 */
object SnackBarHelper {

    fun displaySnackBar(view: View?, s: String?) {
        val snack = Snackbar.make(view!!, s!!, Snackbar.LENGTH_LONG)
        snack.show()
    }

}