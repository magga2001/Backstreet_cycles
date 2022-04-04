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
import com.example.backstreet_cycles.ui.viewModel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_loading.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoadingActivity() : AppCompatActivity() {

    private val loadingViewModel: LoadingViewModel by viewModels()

    /**
     * Initialise the contents within the display of the SplashScreenActivity
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        initObservers()

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        if(!hasConnection()){

            SnackBarHelper.displaySnackBar(loadingActivity, getString(R.string.no_connection))
            onNoConnection()

        }else{
            lifecycleScope.launch { loadingViewModel.getDock() }
        }

    }

    /**
     * Starts Homepage activity if the user is logged in, otherwise, Log In activity is started
     */
    private fun initObservers() {

        // Leads to the journey page with the searched locations
        loadingViewModel.getIsReady().observe(this) { ready ->
            if (ready) {
                loadingViewModel.saveJourney()
                val intent = Intent(this, JourneyActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                onBackHomePage()
            }
        }

        loadingViewModel.getMessage().observe(this){
            SnackBarHelper.displaySnackBar(loadingActivity, it)
        }
    }

    /**
     * Terminate the Loading display when back button is clicked and go to HomePage
     */
    private fun onBackHomePage(){
        Handler(Looper.getMainLooper()).postDelayed({
            
            finish()

        }, Constants.INFORM_SPLASH_TIME)
    }

    /**
     * Resets the Mapbox Navigation inside the journeyHistoryViewModel
     * each time the Loading activity starts
     */
    override fun onStart() {
        super.onStart()
        loadingViewModel.destroyMapboxNavigation()
        loadingViewModel.getMapBoxNavigation()
    }

    private fun onNoConnection() {
        Handler(Looper.getMainLooper()).postDelayed({

            super.onBackPressed()

        }, Constants.SPLASH_TIME)
    }

    private fun hasConnection(): Boolean{

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo?.isConnected ?: false
    }

}