package com.example.backstreet_cycles.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionHelper {

    companion object
    {
        fun checkPermission(context: Context, activity: Activity)
        {
            val TAG_CODE_PERMISSION_LOCATION = 0

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //ActivityCompat#requestPermissions
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    TAG_CODE_PERMISSION_LOCATION
                )
            }
        }


    }
}