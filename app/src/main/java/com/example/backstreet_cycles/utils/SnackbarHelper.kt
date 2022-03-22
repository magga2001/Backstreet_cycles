package com.example.backstreet_cycles.utils

import android.R
import android.view.View

import android.widget.TextView

import androidx.core.content.ContextCompat

import com.google.android.material.snackbar.Snackbar




object SnackbarHelper {

    fun displaySnackbar(view: View?, s: String?) {
        val snack = Snackbar.make(view!!, s!!, Snackbar.LENGTH_LONG)
        //al sbview: View = snack.view
        //  sbview.setBackgroundColor(ContextCompat.getColor(this, R.color.holo_purple))
        // val textView = sbview.findViewById(android.support.design.R.id.snackbar_text) as TextView
        snack.show()
    }
}