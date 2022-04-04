package com.example.backstreet_cycles.ui.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.adapter.StopsAdapter
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.domain.utils.SnackBarHelper
import com.example.backstreet_cycles.domain.utils.TouchScreenCallBack
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.homepage_bottom_sheet.*
import java.util.*

@AndroidEntryPoint
class HomePageActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var mapboxMap: MapboxMap
    private lateinit var locationComponent: LocationComponent
    private lateinit var sheetBehavior: BottomSheetBehavior<*>

    private lateinit var addStopButton: Button
    private lateinit var myLocationButton: FloatingActionButton
    private lateinit var nextPageButton: FloatingActionButton
    private lateinit var stopsAdapter: StopsAdapter

    private lateinit var navigationView: NavigationView
    private lateinit var header: View


    private lateinit var user_name: TextView
    private lateinit var nav_header_textView_email: TextView

    private lateinit var selectedCarmenFeature: CarmenFeature
    private var positionOfStop: Int = 0

    private val homePageViewModel: HomePageViewModel by viewModels()
    /**
     * Initialise the contents within the display of the HomePage
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_homepage)

        homepage_mapView?.onCreate(savedInstanceState)
        homepage_mapView?.getMapAsync(this)

        homePageViewModel.setUpdateInfo(false)
        init()
    }

    // Initialisation
    private fun init() {
        initIncrementAndDecrementUsersFunc()
        initNavigationDrawer()
        initObservers()
    }

    /**
     * Initialise observers for Homepage
     */
    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        homePageViewModel.getShowAlertMutableLiveData().observe(this) {
            if (it) {
                alertDialog(homePageViewModel.getJourneyLocations())
            }
        }

        // Return to login page when logged out
        homePageViewModel.getLogout()
            .observe(this) { logOut ->
                if (logOut) {
                    startActivity(Intent(this@HomePageActivity, LogInActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            }

        // Greets the user in the nav menu
        homePageViewModel.getUserInfo().observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                user_name.text = "Hello, ${firebaseUser.firstName}"
                nav_header_textView_email.text = firebaseUser.email
            }
        }

        // Leads to the journey page with the searched locations
        homePageViewModel.getIsReady().observe(this) { ready ->
            if (ready) {
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        // Determines whether to enable or disable the current location button
        homePageViewModel.hasCurrentLocation().observe(this) { currentLocation ->
            myLocationButton.isEnabled = !currentLocation
        }

        // Checks whether a current journey exists
        homePageViewModel.gethasCurrentJourney().observe(this) { hasCurrentJourney ->
            if (!hasCurrentJourney) {
                SnackBarHelper.displaySnackBar(homePageActivity, "There is no current journey")
            }else{
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        // Ensures that the user doesn't add duplicate locations
        homePageViewModel.getHasDuplicationLocation().observe(this) { duplicate ->
            if (duplicate) {
                SnackBarHelper.displaySnackBar(homePageActivity, "Location already in list")
            }
        }

        // Update the card viewer
        homePageViewModel.getUpdateMutableLiveData().observe(this) { updateInfo ->
            if (updateInfo) {
                removeAndAddInfo(
                    selectedCarmenFeature.placeName().toString(),
                    selectedCarmenFeature.center()!!.latitude(),
                    selectedCarmenFeature.center()!!.longitude()
                )
            } else {
                addInfo(
                    selectedCarmenFeature.placeName().toString(),
                    selectedCarmenFeature.center()!!.latitude(),
                    selectedCarmenFeature.center()!!.longitude()
                )
            }
        }

        homePageViewModel.getMessage().observe(this){
            SnackBarHelper.displaySnackBar(homePageActivity, it)
        }

        homePageViewModel.getIncreaseCyclist().observe(this){ increase ->
            if(increase){
                UserNumber.text = "${homePageViewModel.getNumCyclists()}"
            }else{
                SnackBarHelper.displaySnackBar(homePageActivity, "Cannot have more than 4 users")
            }
        }

        homePageViewModel.getDecreaseCyclist().observe(this){ increase ->
            if(increase){
                UserNumber.text = "${homePageViewModel.getNumCyclists()}"
            }else{
                SnackBarHelper.displaySnackBar(homePageActivity, "Cannot have less than one user")
            }
        }

    }

    /**
     * Increase and Decreases user count
     * Ensures the count remains in the range 1-4
     */
    private fun initIncrementAndDecrementUsersFunc() {

        UserNumber.text = "${homePageViewModel.getNumCyclists()}"
        incrementButton.setOnClickListener {
            homePageViewModel.incrementNumCyclists()
        }

        decrementButton.setOnClickListener {
            homePageViewModel.decrementNumCyclists()
        }
    }

    /**
     * Add a new card to the display containing the searched location
     * @param name of the searched location
     * @param lat of the searched location
     * @param long of the searched location
     */
    private fun addInfo(name: String, lat: Double, long: Double) {
        LayoutInflater.from(this)
        homePageViewModel.addStop(Locations(name, lat, long))
        stopsAdapter.notifyItemChanged(positionOfStop)
        SnackBarHelper.displaySnackBar(homePageActivity, "Adding Stop")
        enableNextPageButton()
        enableMyLocationButton()
    }

    /**
     * Update the clicked displayed card with the new data
     * @param name of the searched location
     * @param lat of the searched location
     * @param long of the searched location
     */
    private fun removeAndAddInfo(name: String, lat: Double, long: Double) {
        homePageViewModel.removeStop(homePageViewModel.getStops()[positionOfStop])
        LayoutInflater.from(this)
        homePageViewModel.addStop(positionOfStop, Locations(name, lat, long))
        stopsAdapter.notifyItemChanged(positionOfStop)
        SnackBarHelper.displaySnackBar(homePageActivity, "Changing Location Of Stop")
        enableNextPageButton()
        enableMyLocationButton()
    }

    /**
     * Enable location button. Enabled when a card with the current location isn't displayed
     */
    private fun enableMyLocationButton() {
        myLocationButton.isEnabled = false
        homePageViewModel.checkCurrentLocation()
    }

    /**
     * Enable next button. Enabled when at least two cards are displayed
     */
    private fun enableNextPageButton() {
        nextPageButton.isEnabled = homePageViewModel.getStops().size >= 2
    }

    /**
     * Initialise the navigation menu
     */
    private fun initNavigationDrawer() {
        navigationView = findViewById(R.id.homepage_nav_view)
        header= navigationView.getHeaderView(0);
        user_name = header.findViewById(R.id.user_name)
        nav_header_textView_email = header.findViewById(R.id.nav_header_textView_email)

        toggle = ActionBarDrawerToggle(this, homePageActivity, R.string.open, R.string.close)
        homePageActivity.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        homepage_nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.changePassword -> {
                    homePageViewModel.getUserDetails()
                    startActivity(Intent(this@HomePageActivity, ChangePasswordActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                R.id.profile -> {
                    homePageViewModel.getUserDetails()
                    startActivity(
                        Intent(
                            this@HomePageActivity,
                            EditUserProfileActivity::class.java
                        )
                    )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                R.id.about -> {
                    startActivity(Intent(this@HomePageActivity, AboutActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                R.id.faq -> {
                    startActivity(Intent(this@HomePageActivity, FAQActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                R.id.journeyHistory -> {
                    val intent = Intent(this@HomePageActivity, JourneyHistoryActivity::class.java)
                    intent.putExtra(
                        "User Location",
                        homePageViewModel.getCurrentLocation(locationComponent)
                    )
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                R.id.currentJourney -> {
                    homePageViewModel.getCurrentJourney()
                }

                R.id.logout -> {
                    homePageViewModel.logOut()
                    homePageViewModel.cancelWork()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }
            true
        }
    }

    /**
     * Initialise the bottom sheet with their components
     */
    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(homepage_bottom_sheet_view)
        addStopButton = findViewById(R.id.addingBtn)
        myLocationButton = findViewById(R.id.myLocationButton)
        myLocationButton.isEnabled = false
        nextPageButton = findViewById(R.id.nextPageButton)
        nextPageButton.isEnabled = false

        createListOfItems()
        itemTouchMethods()
    }

    /**
     * Add functionality for components within the bottom sheet
     */
    private fun bottomSheetFunctionality() {
        initBottomSheet()

        addStopButton.setOnClickListener {
            val intent = homePageViewModel.initPlaceAutoComplete(activity = this)
            startActivityForResult(intent, Constants.REQUEST_AUTO_COMPLETE)
        }

        stopsAdapter.setOnItemClickListener(object : StopsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                homePageViewModel.setUpdateInfo(true)
                val intent =
                    homePageViewModel.initPlaceAutoComplete(activity = this@HomePageActivity)
                startActivityForResult(intent, Constants.REQUEST_AUTO_COMPLETE)
                this@HomePageActivity.positionOfStop = position
            }
        })

        myLocationButton.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            val currentLocation = homePageViewModel.getCurrentLocation(locationComponent)
            addInfo(
                getString(R.string.current_location),
                currentLocation!!.latitude,
                currentLocation.longitude
            )
        }

        nextPageButton.setOnClickListener {
            homePageViewModel.updateCurrentLocation(locationComponent)
            homePageViewModel.getRoute()
        }

        stopsAdapter.getCollapseBottomSheet()
            .observe(this) {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
    }

    /**
     * Initialise the card viewer
     */
    private fun createListOfItems() {
        homePageViewModel.addStop(
            Locations(
                getString(R.string.current_location),
                0.0,
                0.0
            )
        )
        stopsAdapter = StopsAdapter(homePageViewModel.getStops())
        homepage_recyclerView.layoutManager = LinearLayoutManager(this)
        homepage_recyclerView.adapter = stopsAdapter
    }

    /**
     * Ability of dragging and dropping locations in the card viewer
     * Ability of swiping the card to delete the saved location
     */
    private fun itemTouchMethods() {
        val touchScreenCallBack = object : TouchScreenCallBack() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder.absoluteAdapterPosition != 0) {
                    val position = viewHolder.absoluteAdapterPosition
                    homePageViewModel.removeStopAt(position)
                    homepage_recyclerView.adapter?.notifyItemRemoved(position)
                    enableMyLocationButton()
                    enableNextPageButton()
                } else {
                    SnackBarHelper.displaySnackBar(homePageActivity, "Cannot remove location")
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition
                Collections.swap(homePageViewModel.getStops(), fromPosition, toPosition)
                recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
                return true
            }
        }

        val itemTouchHelper = ItemTouchHelper(touchScreenCallBack)
        itemTouchHelper.attachToRecyclerView(homepage_recyclerView)
    }

    /**
     * Initialise the map
     * @param mapboxMap used from the Mapbox API
     */
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->
            enableLocationComponent(style)
            setUpSymbol(homepage_mapView, mapboxMap, style)
            setUpSource(style)
            setUpLayer(style)
            setUpMarker(style)
        }
    }

    /**
     * Ensures location is enabled through the use of permissions
     * @param loadedMapStyle used for the styling of the map
     */
    private fun enableLocationComponent(loadedMapStyle: Style) { // Check if permissions are enabled and if not requested
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = homePageViewModel.initLocationComponent(mapboxMap)
            homePageViewModel.initCurrentLocation(loadedMapStyle, locationComponent)

            bottomSheetFunctionality()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    /**
     * Display tourist attractions on the map
     * @param mapView used as the map component
     * @param mapboxMap used from the Mapbox API
     * @param loadedMapStyle used for the styling of the map
     */
    private fun setUpSymbol(mapView: MapView, mapboxMap: MapboxMap, loadedMapStyle: Style) {
        val symbolManager = SymbolManager(mapView, mapboxMap, loadedMapStyle)
        homePageViewModel.displayingAttractions(
            symbolManager,
            loadedMapStyle,
            homePageViewModel.getTouristAttractions()
        )
    }

    /**
     * Sets the styling of the map
     * @param loadedMapStyle used for the styling of the map
     */
    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(MapboxConstants.GEO_JSON_SOURCE_LAYER_ID))
    }

    /**
     * Initialises the map
     * @param loadedMapStyle used for the styling of the map
     */
    private fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(
            SymbolLayer(
                MapboxConstants.SYMBOL_LAYER_ID,
                MapboxConstants.GEO_JSON_SOURCE_LAYER_ID
            ).withProperties(
                PropertyFactory.iconImage(MapboxConstants.SYMBOL_ICON_ID),
                PropertyFactory.iconOffset(arrayOf(0f, -8f))
            )
        )
    }

    /**
     * Used for marking searched locations
     * @param loadedStyle used for the styling of the map
     */
    private fun setUpMarker(loadedStyle: Style) {
        val drawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_baseline_location_on_red_24dp,
            null
        )
        val bitmapUtils = BitmapUtils.getBitmapFromDrawable(drawable)
        loadedStyle.addImage(MapboxConstants.SYMBOL_ICON_ID, bitmapUtils!!)
    }

    /**
     * Alert used to make the user decide between continuing an existing journey or creating a new one
     * @param newStops stores the new searched locations as a list
     */
    private fun alertDialog(newStops: MutableList<Locations>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Planner Alert")
        builder.setMessage(
            "There is already a planned journey that you are currently using." +
                    " Do you want to continue with the current journey or with the newly created one?"
        )

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

    /**
     * Sets up the map
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_AUTO_COMPLETE) {
            selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            val style = mapboxMap.style
            if (style != null) {
                homePageViewModel.searchLocation(mapboxMap, selectedCarmenFeature, style)
            }
        }
    }

    /**
     * Manages the permissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Indicates when a permission is needed
     */
    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        SnackBarHelper.displaySnackBar(
            homePageActivity,
            R.string.user_location_permission_explanation.toString()
        )
    }

    /**
     * Checks whether the location permission was granted by the user
     * @param granted
     */
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap.getStyle { style -> enableLocationComponent(style) }
        } else {
            SnackBarHelper.displaySnackBar(
                homePageActivity,
                R.string.user_location_permission_not_granted.toString()
            )
            finish()
        }
    }

    /**
     * Determines whether an item from the menu is selected
     * @param item
     * @return true if selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return true
    }

    /**
     * Initialises the HomePage
     */
    override fun onStart() {
        super.onStart()
        homepage_mapView?.onStart()
        homePageViewModel.getUserDetails()
    }

    /**
     * Called when the overall system is running low on memory,
     * and actively running processes should trim their memory usage
     */
    override fun onLowMemory() {
        super.onLowMemory()
        homepage_mapView?.onLowMemory()
    }

    /**
     * Called before termination of activity
     */
    override fun onStop() {
        super.onStop()
        homepage_mapView?.onStop()
    }

    /**
     * Terminates the HomePage
     */
    override fun onDestroy() {
        super.onDestroy()
        homepage_mapView?.onDestroy()
    }
}