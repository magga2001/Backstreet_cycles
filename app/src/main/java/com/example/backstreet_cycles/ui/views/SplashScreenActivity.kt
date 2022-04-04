package com.example.backstreet_cycles.ui.views

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.domain.utils.SnackBarHelper
import com.example.backstreet_cycles.ui.viewModel.SplashScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.launch

/**
 * This activity launches Splash Screen page which is responsible for fetching data when the app starts
 */
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


        if(!hasConnection()) {
            SnackBarHelper.displaySnackBar(
                splashScreenActivity,
                "No connection. Please connect to internet"
            )
        }

        loadData()


    }

    /**
     * Starts Homepage activity if the user is logged in, otherwise, Log In activity is started
     */
    private fun initObservers() {
        splashScreenViewModel.getIsReady().observe(this) { ready ->
            if (ready) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null && currentUser.isEmailVerified) {
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

    private fun loadData(){
        Handler(Looper.getMainLooper()).postDelayed({

            lifecycleScope.launch { splashScreenViewModel.loadData() }

        }, Constants.SPLASH_TIME)
    }

    private fun hasConnection(): Boolean{

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo?.isConnected ?: false
    }

}