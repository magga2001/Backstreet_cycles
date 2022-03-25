package com.example.backstreet_cycles.ui.views


import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.domain.adapter.JourneyHistoryAdapter
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.ui.viewModel.JourneyHistoryViewModel
import com.mapbox.navigation.core.MapboxNavigationProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.activity_journey.*
import kotlinx.android.synthetic.main.activity_journey_history.*

@AndroidEntryPoint
class JourneyHistoryActivity : AppCompatActivity() {

    private lateinit var userCredentials: Users
    private val journeyHistoryViewModel: JourneyHistoryViewModel by viewModels()

    private lateinit var nAdapter: JourneyHistoryAdapter
    private var journeys: MutableList<List<Locations>> = emptyList<List<Locations>>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        journeyHistoryViewModel.getIsReadyMutableLiveData().observe(this) { ready ->
            if(ready)
            {
                startActivity(Intent(this, JourneyActivity::class.java))
                journeyHistoryViewModel.getIsReadyMutableLiveData().value = false
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            }
        }

        journeyHistoryViewModel.getShowAlertMutableLiveData().observe(this) {
            if (it) {
                alertDialog(BackstreetApplication.location)
            }
        }

        journeyHistoryViewModel.getUserDetails()
        journeyHistoryViewModel.getUserDetailsMutableLiveData().observe(this) { userDetails ->
            if (userDetails != null) {
                journeys = journeyHistoryViewModel.getJourneyHistory(userDetails)
                userCredentials = userDetails
            }
            init()
        }
    }

    private fun init() {
        initCardView()
    }

    private fun initCardView() {

        nAdapter = JourneyHistoryAdapter(journeys)
        nAdapter.setOnItemClickListener(object : JourneyHistoryAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                journeyHistoryViewModel.clearAllStops()
                journeyHistoryViewModel.addAllStops(journeys[position].toMutableList())
                journeyHistoryViewModel.getDock()
            }
        })
        journey_history_recycler_view.layoutManager = LinearLayoutManager(this)
        journey_history_recycler_view.adapter = nAdapter
    }

    override fun onStart() {
        super.onStart()
        journey_mapView?.onStart()
        journeyHistoryViewModel.destroyMapboxNavigation()
        journeyHistoryViewModel.getMapBoxNavigation()
    }

    private fun alertDialog(newStops: MutableList<Locations>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Planner Alert")
        builder.setMessage("You currently have a planned journey. " +
                "Would you like to return to the current journey or create a new one?")

        builder.setPositiveButton(R.string.continue_with_current_journey) { dialog, which ->
            journeyHistoryViewModel.continueWithCurrentJourney()
            journeyHistoryViewModel.setShowAlert(false)
        }

        builder.setNegativeButton(R.string.continue_with_newly_set_journey) { dialog, which ->
            journeyHistoryViewModel.continueWithNewJourney(newStops)
            journeyHistoryViewModel.setShowAlert(false)
        }
        builder.show()
    }

}