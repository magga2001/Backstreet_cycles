package com.example.backstreet_cycles.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.Api
import com.example.backstreet_cycles.PlanJourneyActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.dto.Dock
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.concurrent.thread

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