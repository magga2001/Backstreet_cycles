package com.example.backstreet_cycles.ui.views

//---------------------------------
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.adapter.StopsAdapter
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.SnackbarHelper
import com.example.backstreet_cycles.domain.utils.TouchScreenCallBack
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import kotlinx.android.synthetic.main.nav_header.*
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

    private lateinit var selectedCarmenFeature: CarmenFeature
    private var positionOfStop:Int = 0

    private lateinit var textOfNumberOfUsers : TextView
    private lateinit var plusBtn : Button
    private lateinit var minusBtn : Button

    private val homePageViewModel: HomePageViewModel by viewModels()
    private val loggedInViewModel: LoggedInViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_homepage)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        //Maybe check the functionality
        homePageViewModel.setUpdateInfo(false)

        init()
    }

    private fun init(){
        initIncrementAndDecrementUsersFunc()
        initObservers()
        initNavigationDrawer()
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers(){

        homePageViewModel.getShowAlertMutableLiveData().observe(this) {
            if (it) {
                alertDialog(BackstreetApplication.location)
            }
        }

        loggedInViewModel.getLoggedOutMutableLiveData()
            .observe(this) { loggedOut ->
                if (loggedOut) {
                    startActivity(Intent(this@HomePageActivity, LogInActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                }
            }

        loggedInViewModel.getUserDetails()
        loggedInViewModel.getUserDetailsMutableLiveData().observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                user_name.text = "Hello: ${firebaseUser.firstName}"
                tv_email.text = firebaseUser.email
            }
        }

        homePageViewModel.getIsReadyMutableLiveData().observe(this) {ready ->
            if(ready)
            {
                val intent = Intent(this, JourneyActivity::class.java)
                intent.putExtra(Constants.NUM_USERS,homePageViewModel.getNumUsers())
                homePageViewModel.saveJourney()
                startActivity(intent)
                homePageViewModel.getIsReadyMutableLiveData().value = false
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            }
        }

        homePageViewModel.hasCurrentLocation().observe(this){ currentLocation ->

            myLocationButton.isEnabled = !currentLocation
        }

        homePageViewModel.getHasCurrentJourneyMutableLiveData().observe(this){ hasCurrentJourney ->
            if(!hasCurrentJourney)
            {
                SnackbarHelper.displaySnackbar(HomePageActivity,"There is no current journey")
            }
        }

        homePageViewModel.getHasDuplicationLocation().observe(this){ duplicate ->
            if(duplicate)
            {
                SnackbarHelper.displaySnackbar(HomePageActivity, "Location already in list")
            }
        }

        homePageViewModel.getUpdateMutableLiveData().observe(this){ updateInfo ->
            if(updateInfo)
            {
                addAndRemoveInfo(
                    selectedCarmenFeature.placeName().toString(),
                    selectedCarmenFeature.center()!!.latitude(),
                    selectedCarmenFeature.center()!!.longitude()
                )
            }else
            {
                addInfo(
                    selectedCarmenFeature.placeName().toString(),
                    selectedCarmenFeature.center()!!.latitude(),
                    selectedCarmenFeature.center()!!.longitude()
                )
            }

        }
    }

    private fun initIncrementAndDecrementUsersFunc(){
        textOfNumberOfUsers = findViewById(R.id.UserNumber)
        plusBtn = findViewById(R.id.incrementButton)
        minusBtn = findViewById(R.id.decrementButton)
        textOfNumberOfUsers.text = "${homePageViewModel.getNumUsers()}"

        plusBtn.setOnClickListener{
          if(homePageViewModel.getNumUsers()>3){
              SnackbarHelper.displaySnackbar(HomePageActivity, "Cannot have more than 4 users")
          }
          else{
              homePageViewModel.incrementNumUsers()
              textOfNumberOfUsers.text = "${homePageViewModel.getNumUsers()}"
          }

        }
        minusBtn.setOnClickListener{
            if(homePageViewModel.getNumUsers()>=2){
                homePageViewModel.decrementNumUsers()
                textOfNumberOfUsers.text = "${homePageViewModel.getNumUsers()}"
            }
            else{
                SnackbarHelper.displaySnackbar(HomePageActivity, "Cannot have less than one user")
            }
        }
    }

    private fun addInfo(name:String, lat: Double, long: Double) {
        LayoutInflater.from(this)
        homePageViewModel.addStop(Locations(name,lat, long))
        stopsAdapter.notifyItemChanged(positionOfStop)
        SnackbarHelper.displaySnackbar(HomePageActivity, "Adding Stop")
        enableNextPageButton()
        enableMyLocationButton()
    }

    private fun addAndRemoveInfo(name:String, lat: Double, long: Double) {
        homePageViewModel.removeStop(homePageViewModel.getStops()[positionOfStop])
        LayoutInflater.from(this)
        homePageViewModel.addStop(positionOfStop, Locations(name,lat, long))
        stopsAdapter.notifyItemChanged(positionOfStop)
        SnackbarHelper.displaySnackbar(HomePageActivity, "Changing Location Of Stop")
        enableNextPageButton()
        enableMyLocationButton()
    }

    private fun enableMyLocationButton(){
        myLocationButton.isEnabled = false
        homePageViewModel.checkCurrentLocation()
    }

    private fun enableNextPageButton(){
        nextPageButton.isEnabled = homePageViewModel.getStops().size >= 2
    }

    private fun initNavigationDrawer() {
        toggle = ActionBarDrawerToggle(this, HomePageActivity, R.string.open, R.string.close)
        HomePageActivity.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.changePassword -> {
                    loggedInViewModel.getUserDetails()
                    startActivity(Intent(this@HomePageActivity, ChangePasswordActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)

                }
                R.id.profile -> {
                    loggedInViewModel.getUserDetails()
                    startActivity(Intent(this@HomePageActivity, EditUserProfileActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                }
                R.id.about -> {
                    startActivity(Intent(this@HomePageActivity, AboutActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                }

                R.id.faq -> {
                    startActivity(Intent(this@HomePageActivity, FAQActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                }

                R.id.journeyHistory -> {
                        val intent = Intent(this@HomePageActivity, JourneyHistoryActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                }

                R.id.currentJourney -> {
                    homePageViewModel.getCurrentJourney()
                }
                R.id.logout -> {
                    loggedInViewModel.logOut()
                    homePageViewModel.cancelWork()
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                }
            }
            true
        }
    }
    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_view)
        addStopButton = findViewById(R.id.addingBtn)
        myLocationButton = findViewById(R.id.myLocationButton)
        myLocationButton.isEnabled = false
        nextPageButton  = findViewById(R.id.nextPageButton)
        nextPageButton.isEnabled = false

        createListOfItems()
        itemTouchMethods()
    }

    private fun bottomSheetFunctionality(){
        initBottomSheet()

        addStopButton.setOnClickListener {
            val intent = homePageViewModel.initPlaceAutoComplete(activity = this)
            startActivityForResult(intent, Constants.REQUEST_AUTO_COMPLETE)
        }
        stopsAdapter.setOnItemClickListener(object : StopsAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                homePageViewModel.setUpdateInfo(true)
                val intent = homePageViewModel.initPlaceAutoComplete(activity = this@HomePageActivity)
                startActivityForResult(intent, Constants.REQUEST_AUTO_COMPLETE)
                this@HomePageActivity.positionOfStop = position
            }
        })

        myLocationButton.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            val currentLocation  = homePageViewModel.getCurrentLocation(locationComponent)
            addInfo(getString(R.string.current_location), currentLocation!!.latitude, currentLocation.longitude )
        }

        nextPageButton.setOnClickListener{

            homePageViewModel.updateCurrentLocation(locationComponent)
            homePageViewModel.getDock()
        }

        stopsAdapter.getCollapseBottomSheet()
            .observe(this) {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
    }

    private fun createListOfItems(){
        homePageViewModel.addStop(
            Locations(getString(R.string.current_location),
                0.0,
                0.0)
        )
        stopsAdapter = StopsAdapter(homePageViewModel.getStops())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = stopsAdapter
    }

    private fun itemTouchMethods(){
        val touchScreenCallBack = object : TouchScreenCallBack(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder.absoluteAdapterPosition != 0) {
                    val position = viewHolder.absoluteAdapterPosition
                    homePageViewModel.removeStopAt(position)
                    recyclerView.adapter?.notifyItemRemoved(position)
                    enableMyLocationButton()
                    enableNextPageButton()
                }
                else{
                    SnackbarHelper.displaySnackbar(HomePageActivity, "Cannot remove location")
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
                recyclerView.adapter!!.notifyItemMoved(fromPosition,toPosition)
                return true
            }
        }

        val itemTouchHelper = ItemTouchHelper(touchScreenCallBack)

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->
            enableLocationComponent(style)
            setUpSymbol(mapView, mapboxMap, style)
            setUpSource(style)
            setUpLayer(style)
            setUpMarker(style)
        }
    }

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

    private fun setUpSymbol(mapView: MapView, mapboxMap: MapboxMap, loadedMapStyle: Style)
    {
        val symbolManager = SymbolManager(mapView, mapboxMap, loadedMapStyle)
        homePageViewModel.displayingAttractions(symbolManager, loadedMapStyle, homePageViewModel.getTouristAttractions())
    }

    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(MapboxConstants.GEO_JSON_SOURCE_LAYER_ID))
    }

    private fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(
            SymbolLayer(MapboxConstants.SYMBOL_LAYER_ID, MapboxConstants.GEO_JSON_SOURCE_LAYER_ID).withProperties(
                PropertyFactory.iconImage(MapboxConstants.SYMBOL_ICON_ID),
                PropertyFactory.iconOffset(arrayOf(0f, -8f))
            )
        )
    }

    private fun setUpMarker(loadedStyle: Style)
    {
        val drawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_baseline_location_on_red_24dp,
            null
        )
        val bitmapUtils = BitmapUtils.getBitmapFromDrawable(drawable)
        loadedStyle.addImage(MapboxConstants.SYMBOL_ICON_ID, bitmapUtils!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_AUTO_COMPLETE) {
            selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            val style = mapboxMap.style
            if (style != null) {
                homePageViewModel.searchLocation(mapboxMap,selectedCarmenFeature,style)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        SnackbarHelper.displaySnackbar(HomePageActivity, R.string.user_location_permission_explanation.toString())
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap.getStyle { style -> enableLocationComponent(style) }
        } else {
            SnackbarHelper.displaySnackbar(HomePageActivity, R.string.user_location_permission_not_granted.toString())
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        homePageViewModel.destroyMapboxNavigation()
        homePageViewModel.getMapBoxNavigation()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    private fun alertDialog(newStops: MutableList<Locations>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Planner Alert")
        builder.setMessage("There is already a planned journey that you are currently using." +
                "Do you want to continue with the current journey or with the newly created one?")

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