package com.example.backstreet_cycles.ui.views


import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.data.remote.TflHelper
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.adapter.JourneyHistoryAdapter
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
import com.mapbox.navigation.core.MapboxNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.activity_journey_history.*

@AndroidEntryPoint
class JourneyHistoryActivity : AppCompatActivity() {

    private lateinit var userCredentials: Users
    private val journeyViewModel : JourneyViewModel by viewModels()
    private val homePageViewModel : HomePageViewModel by viewModels()
    private lateinit var loggedInViewModel: LoggedInViewModel
    private lateinit var nAdapter: JourneyHistoryAdapter
    private lateinit var mapboxNavigation: MapboxNavigation
    private var journeys: MutableList<List<Locations>> = emptyList<List<Locations>>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loggedInViewModel = ViewModelProvider(this)[LoggedInViewModel::class.java]

        homePageViewModel.getIsReadyMutableLiveData().observe(this) {ready ->
            if(ready)
            {
                startActivity(Intent(this, JourneyActivity::class.java))
                homePageViewModel.getIsReadyMutableLiveData().value = false
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            }
        }

        homePageViewModel.getShowAlertMutableLiveData().observe(this) {
            if (it) {
                alertDialog(MapRepository.location)
            }
        }

        loggedInViewModel.getUserDetails()
        loggedInViewModel.getUserDetailsMutableLiveData().observe(this) { userDetails ->
            if (userDetails != null) {
                journeys = journeyViewModel.getJourneyHistory(userDetails)
                userCredentials = userDetails
            }
            init()
        }
    }

    fun init() {
        initCardView()
    }

    private fun initCardView() {

        nAdapter = JourneyHistoryAdapter(journeys)
        nAdapter.setOnItemClickListener(object : JourneyHistoryAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                    TflHelper.getDock(context = applicationContext,
                        object : CallbackResource<MutableList<Dock>> {
                            override fun getResult(objects: MutableList<Dock>) {
                                homePageViewModel.clearAllStops()
                                homePageViewModel.addAllStops(journeys[position].toMutableList())
                                homePageViewModel.getRoute()
//                                SharedPrefHelper.initialiseSharedPref(application,Constants.LOCATIONS)
//                                if (!SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)){
//                                    stops.addAll(journeys[position])
//                                    homePageViewModel.fetchPoints()
////                                    alertDialog(stops)
//                                } else{
//                                    journeyViewModel.addJourneyToJourneyHistory(SharedPrefHelper.getSharedPref(Locations::class.java), userCredentials)
//                                    stops.addAll(journeys[position])
//                                    homePageViewModel.fetchPoints()
//                                }

                            }
                        })


            }
        })
        journey_history_recycler_view.layoutManager = LinearLayoutManager(this)
        journey_history_recycler_view.adapter = nAdapter
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        mapboxNavigation = homePageViewModel.initialiseMapboxNavigation()
    }

//    How to make an util class for those functions below!

//    private fun replaceCurrentLocation(){
//        val oldCurrentLocation = stops.find { it.name == "Current Location" }
//        if (oldCurrentLocation != null) {
//            val currentLocation  = homePageViewModel.getCurrentLocation(locationComponent)
//            Collections.replaceAll(stops,oldCurrentLocation,Locations("Current Location",currentLocation!!.latitude, currentLocation.longitude))
//        }
//    }


    private fun alertDialog(newStops: MutableList<Locations>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Planner Alert")
        builder.setMessage("There is already a planned journey that you are currently using." +
                "Do you want to change the journey to the current one or keep the same one?")

        builder.setPositiveButton(R.string.continue_with_current_journey) { dialog, which ->
            homePageViewModel.continueWithCurrentJourney()
            homePageViewModel.setShowAlert(false)
        }

        builder.setNegativeButton(R.string.continue_with_newly_set_journey) { dialog, which ->
            homePageViewModel.continueWithNewJourney(newStops)
            homePageViewModel.setShowAlert(false)
        }
        builder.show()
    }

}