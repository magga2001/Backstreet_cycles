package com.example.backstreet_cycles.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.MainActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.SplashViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

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
//        lifecycleScope.launch {test()}
    }

    private suspend fun test()
    {
        var startDock:Dock? = null

        coroutineScope {
            val start = async { splashViewModel.readADock("Park Street, Bankside") }

            startDock = start.await()
            Log.i("READ DOCK", startDock.toString() )

            delay(500)
        }
    }
}