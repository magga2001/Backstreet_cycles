package com.example.backstreet_cycles.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.utils.TflHelper
import com.example.backstreet_cycles.viewModel.SplashViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity: AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        splashViewModel.getIsReadyMutableLiveData().observe(this) {ready ->
            if(ready)
            {
                if(FirebaseAuth.getInstance().currentUser != null){
                    startActivity(Intent(this, HomePageActivity::class.java))
                    finish()
                }
                startActivity(Intent(this, LogInActivity::class.java))
                finish()

            }
        }

        splashViewModel.loadTouristLocations()
        splashViewModel.loadDocks()
        //TflHelper.getDock(applicationContext)
    }
}