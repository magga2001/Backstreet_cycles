package com.example.backstreet_cycles.presentation.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.presentation.viewModel.SplashViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity: AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel
    private val SPLASH_TIME: Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed( {

            if(FirebaseAuth.getInstance().currentUser != null){
                startActivity(Intent(this, HomePageActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, LogInActivity::class.java))
            }

            finish()
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)

        }, Constants.SPLASH_TIME)
    }

}