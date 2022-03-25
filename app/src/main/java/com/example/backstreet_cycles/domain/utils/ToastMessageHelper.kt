package com.example.backstreet_cycles.domain.utils

import android.app.Application
import android.widget.Toast

object ToastMessageHelper {

    fun createToastMessage(application:Application,stringMessage: String?) {
        Toast.makeText(application, stringMessage, Toast.LENGTH_LONG)
            .show()
    }
}