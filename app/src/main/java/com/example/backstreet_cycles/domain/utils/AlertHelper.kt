package com.example.backstreet_cycles.domain.utils

import android.app.AlertDialog
import android.app.Application
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.views.HomePageActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
object AlertHelper: AppCompatActivity() {
    private val homePageViewModel: HomePageViewModel by viewModels()

    fun alertDialog(newStops: MutableList<Locations>, mApplication: Application) {
        val builder = AlertDialog.Builder(mApplication)
        builder.setTitle("Planner Alert")
        builder.setMessage("There is already a planned journey that you are currently using." +
                "Do you want to continue with the current journey or with the newly created one?")

        builder.setPositiveButton(R.string.continue_with_current_journey) { dialog, which ->
            SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
            val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
            MapRepository.location = listOfLocations
            val listPoints = PlannerHelper.setPoints(listOfLocations)
            homePageViewModel.fetchRoute(this, HomePageActivity.mapboxNavigation, listPoints, "cycling", false)
        }

        builder.setNegativeButton(R.string.continue_with_newly_set_journey) { dialog, which ->
            val listPoints = PlannerHelper.setPoints(newStops)
            SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
            SharedPrefHelper.overrideSharedPref(newStops, Locations::class.java)
            homePageViewModel.fetchRoute(this, HomePageActivity.mapboxNavigation, listPoints, "cycling", false)
        }
        builder.show()
    }

}