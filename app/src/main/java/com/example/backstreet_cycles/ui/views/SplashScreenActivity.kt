package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashScreenActivity(): AppCompatActivity() {

    private val journeyViewModel : JourneyViewModel by viewModels()
//    private val journeyViewModel: JourneyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        journeyViewModel.clear()
//        journeyViewModel.clear()

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