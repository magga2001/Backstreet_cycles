package com.example.backstreet_cycles.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.PlanJourneyActivity
import com.example.backstreet_cycles.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashScreenActivity: AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        loadActivity()
        //unseed()
    }

    private fun loadActivity()
    {
        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this, PlanJourneyActivity::class.java))
            finish()
        },1000)
    }
}