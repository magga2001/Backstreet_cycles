package com.example.backstreet_cycles.ui.views


import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.adapter.JourneyHistoryAdapter
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.utils.SnackBarHelper
import com.example.backstreet_cycles.ui.viewModel.JourneyHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.activity_journey.*
import kotlinx.android.synthetic.main.activity_journey_history.*
import kotlinx.coroutines.launch

/**
 * This activity launches Journey History page which contains the history of all user's journeys
 */
@AndroidEntryPoint
class JourneyHistoryActivity : AppCompatActivity() {

    private lateinit var userCredentials: Users
    private val journeyHistoryViewModel: JourneyHistoryViewModel by viewModels()

    private lateinit var nAdapter: JourneyHistoryAdapter
    private var journeys: MutableList<List<Locations>> =
        emptyList<List<Locations>>().toMutableList()

    /**
     * Initialise the contents within the display of the Journey History
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        journeyHistoryViewModel.getIsReady().observe(this) { ready ->
            if (ready) {
                journeyHistoryViewModel.resetNumCyclists()
                startActivity(Intent(this, LoadingActivity::class.java))
                journeyHistoryViewModel.getIsReady().value = false
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        journeyHistoryViewModel.getShowAlertMutableLiveData().observe(this) {
            if (it) {
                alertDialog(journeyHistoryViewModel.getJourneyLocations())
            }
        }

        journeyHistoryViewModel.getMessage().observe(this){
            SnackBarHelper.displaySnackBar(homePageActivity, it)
        }

        journeyHistoryViewModel.getUserDetails()
        journeyHistoryViewModel.getUserInfo().observe(this) { userDetails ->
            if (userDetails != null) {
                journeys = journeyHistoryViewModel.getJourneyHistory(userDetails).reversed().toMutableList()
                userCredentials = userDetails
            }
            init()
        }
    }

    /**
     * Initialise the Card View behaviour
     */

    private fun init() {
        initCardView()
    }

    private fun initCardView() {

        nAdapter = JourneyHistoryAdapter(journeys)
        nAdapter.setOnItemClickListener(object : JourneyHistoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                journeyHistoryViewModel.clearAllStops()
                journeyHistoryViewModel.addAllStops(journeys[position].toMutableList())
                val currentLocation: Location = intent.getParcelableExtra("User Location")!!
                journeyHistoryViewModel.updateCurrentLocation(currentLocation)
                lifecycleScope.launch { journeyHistoryViewModel.getDock() }
            }
        })
        journey_history_recycler_view.layoutManager = LinearLayoutManager(this)
        journey_history_recycler_view.adapter = nAdapter
    }

    /**
     * Resets the Mapbox Navigation inside the journeyHistoryViewModel
     * each time the JourneyHistory activity starts
     */

    override fun onStart() {
        super.onStart()
        journey_mapView?.onStart()
        journeyHistoryViewModel.destroyMapboxNavigation()
        journeyHistoryViewModel.getMapBoxNavigation()
    }

    /**
     * Implement the behaviour of the pop out window with alert dialog
     */

    private fun alertDialog(newStops: MutableList<Locations>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Planner Alert")
        builder.setMessage(
            "You currently have a planned journey. " +
                    "Would you like to return to the current journey or create a new one?"
        )

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

    /**
     * Go to the HomePage when Home button from navigation menu is clicked
     */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Go to the HomePage when back button is clicked
     */

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}