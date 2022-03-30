package com.example.backstreet_cycles.domain.utils

import android.app.Application
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar

import com.google.android.material.snackbar.Snackbar

object SnackbarHelper {

    fun displaySnackbar(view: View?, s: String?) {
        val snack = Snackbar.make(view!!, s!!, Snackbar.LENGTH_LONG)
        snack.show()
    }

}