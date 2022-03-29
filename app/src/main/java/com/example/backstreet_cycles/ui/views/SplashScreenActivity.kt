package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.SplashScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashScreenActivity() : AppCompatActivity() {

    private val splashScreenViewModel: SplashScreenViewModel by viewModels()

    /**
     * Initialise the contents within the display of the SplashScreenActivity
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initObservers()

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed({

            lifecycleScope.launch { splashScreenViewModel.loadData() }

        }, Constants.SPLASH_TIME)
    }

    /**
     * Starts Homepage activity if the user is logged in, otherwise, Log In activity is started
     */
    private fun initObservers() {
        splashScreenViewModel.getIsReadyMutableLiveData().observe(this) { ready ->
            if (ready) {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    startActivity(Intent(this, HomePageActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, LogInActivity::class.java))
                }

                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }


}