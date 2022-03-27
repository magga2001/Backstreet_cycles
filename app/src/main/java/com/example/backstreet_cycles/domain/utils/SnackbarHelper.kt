package com.example.backstreet_cycles.domain.utils

import android.view.View

import com.google.android.material.snackbar.Snackbar

object SnackbarHelper {

    fun displaySnackbar(view: View?, s: String?) {
        val snack = Snackbar.make(view!!, s!!, Snackbar.LENGTH_LONG)
        snack.show()
    }
}