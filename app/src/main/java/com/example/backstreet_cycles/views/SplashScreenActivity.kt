package com.example.backstreet_cycles.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.MainActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.SplashViewModel

class SplashScreenActivity: AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        splashViewModel.getIsReadyMutableLiveData().observe(this) {ready ->
            if(ready)
            {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        splashViewModel.loadDock()
    }
}