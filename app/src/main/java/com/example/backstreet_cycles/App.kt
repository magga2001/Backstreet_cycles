package com.example.backstreet_cycles;

import android.app.Application;
import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider

class App : Application() {

     companion object{

         lateinit var context:Context

     }
     override fun onCreate() {
         super.onCreate()

         context = this
     }

}
