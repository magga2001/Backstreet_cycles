package com.example.backstreet_cycles.ui.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.*
import com.example.backstreet_cycles.domain.utils.BitmapHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * View model for Home Page Activity responsible for implementing map with locations and adding stops for a journey
 */
@HiltViewModel
class HomePageViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    application: Application,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(
    tflRepository,
    mapboxRepository,
    cyclistRepository,
    userRepository,
    application,
    applicationContext
) {

    private var showAlert: MutableLiveData<Boolean> = MutableLiveData()
    private var stops: MutableList<Locations> = mutableListOf()
    private var updateInfo: Boolean = false

    private val isReady: MutableLiveData<Boolean> = MutableLiveData()
    private val hasCurrentLocation: MutableLiveData<Boolean> = MutableLiveData()
    private val hasCurrentJourney: MutableLiveData<Boolean> = MutableLiveData()
    private val hasDuplicationLocation: MutableLiveData<Boolean> = MutableLiveData()
    private val updateMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val message: MutableLiveData<String> = MutableLiveData()
    private val logout: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Returns the location component for the map
     * @param mapboxMap
     * @return LocationComponent
     */
    fun initLocationComponent(mapboxMap: MapboxMap): LocationComponent {
        return mapboxMap.locationComponent
    }

    /**
     * Initialisation of current location of user
     * @param loadedMapStyle styling of map
     * @param locationComponent for map
     */
    @SuppressLint("MissingPermission")
    fun initCurrentLocation(loadedMapStyle: Style, locationComponent: LocationComponent) {
        val customLocationComponentOptions = customiseLocationPuck()
        activateLocationComponent(locationComponent, loadedMapStyle, customLocationComponentOptions)
        showLocationComponent(locationComponent)
    }

    private fun customiseLocationPuck(): LocationComponentOptions {
        return LocationComponentOptions.builder(mApplication)
            .pulseEnabled(true)
            .build()
    }

    /**
     * Enable location component for application
     * @param locationComponent for map
     * @param loadedMapStyle styling of map
     * @param customLocationComponentOptions
     */
    private fun activateLocationComponent(
        locationComponent: LocationComponent,
        loadedMapStyle: Style,
        customLocationComponentOptions: LocationComponentOptions
    ) {
        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(mApplication, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()
        )
    }

    /**
     * Display location component on map
     * @param locationComponent for map
     */
    @SuppressLint("MissingPermission")
    private fun showLocationComponent(locationComponent: LocationComponent) {
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING
        locationComponent.renderMode = RenderMode.COMPASS
    }

    /**
     * Adding a searched location
     * @param stop
     */
    fun addStop(stop: Locations) {
        stops.add(stop)
    }

    /**
     * Adding a searched location by index
     * @param index
     * @param stop
     */
    fun addStop(index: Int, stop: Locations) {
        stops.add(index, stop)
    }

    /**
     * Removing a searched location
     * @param stop
     */
    fun removeStop(stop: Locations) {
        stops.remove(stop)
    }

    /**
     * Removing a searched location by index
     * @param index
     * @param stop
     */
    fun removeStopAt(index: Int) {
        stops.removeAt(index)
    }

    /**
     * Getter function for the list of searched stops
     * @return MutableList<Locations> of searched locations
     */
    fun getStops(): MutableList<Locations> {
        return stops
    }

    /**
     * Checks for duplicate stops
     * @param location represent searched location
     * @return Boolean true or false to indicate a duplicate stop
     */
    fun checkIfAlreadyInStops(location: Locations): Boolean {
        return stops.contains(location)
    }

    /**
     * Getter function for the list of tourist attractions
     * @return MutableList<Locations> of tourist attractions
     */
    fun getTouristAttractions(): List<Locations> {
        return locationRepository.getTouristLocations()
    }

    /**
     * Record information
     * @param info
     */
    fun setUpdateInfo(info: Boolean) {
        updateInfo = info
    }

    /**
     * Check if info received
     * @return Boolean
     */
    fun getUpdateInfo(): Boolean {
        return updateInfo
    }

    /**
     * Getter function for the route of the journey
     */
    override fun getRoute() {
        super.getRoute()
        clearJourneyLocations()
        setJourneyLocations(stops)
        checkCurrentJourney()
    }

    /**
     * Display tourist attractions on the map for the user
     * @param symbolManager
     * @param loadedMapStyle styling of map
     * @param data tourist attraction locations
     */
    fun displayingAttractions(
        symbolManager: SymbolManager,
        loadedMapStyle: Style,
        data: List<Locations>
    ) {
        val textSize = 15.0F
        val textColor = mContext.getString(R.string.black)

        symbolManager.iconAllowOverlap = true
        val bitmap = BitmapHelper.bitmapFromDrawableRes(
            mApplication,
            R.drawable.tourist_attraction_icon
        ) as Bitmap
        loadedMapStyle.addImage(mContext.getString(R.string.my_marker), Bitmap.createScaledBitmap(bitmap, 80, 80, false))
        for (attraction in data) {
            symbolManager.create(
                SymbolOptions()
                    .withLatLng(LatLng(attraction.lat, attraction.lon))
                    .withIconImage(mContext.getString(R.string.my_marker))
                    .withIconAnchor(mContext.getString(R.string.right))
                    .withTextField(attraction.name)
                    .withTextSize(textSize)
                    .withTextColor(textColor)
                    .withTextAnchor(mContext.getString(R.string.left))
            )
        }
    }

    /**
     * Check whether one of the added locations is the current location itself
     */
    fun checkCurrentLocation() {
        val stop = stops.map { it.name }.contains(mContext.getString(R.string.location))
        hasCurrentLocation.value = stop
    }

    /**
     * Update the current location of user in the map
     * @param locationComponent of map
     */
    fun updateCurrentLocation(locationComponent: LocationComponent) {
        try {
            for (stop in stops) {
                if (stop.name == mContext.getString(R.string.location)) {

                    val longitude = getCurrentLocation(locationComponent)!!.longitude
                    val latitude = getCurrentLocation(locationComponent)!!.latitude

                    stop.lat = latitude
                    stop.lon = longitude
                }
            }
        } catch(e: NullPointerException){

        }
    }

    /**
     * Getter function to return recent location of user
     * @param locationComponent of map
     * @return Location
     */
    fun getCurrentLocation(locationComponent: LocationComponent): Location? {
        return locationComponent.lastKnownLocation
    }

    /**
     * Initialise the search bar with an autocomplete feature for search locations
     * @param activity
     * @return Intent
     */
    fun initPlaceAutoComplete(activity: Activity): Intent {
        return PlaceAutocomplete.IntentBuilder()
            .accessToken(mApplication.getString(R.string.mapbox_access_token)).placeOptions(
                PlaceOptions.builder()
                    .bbox(-0.240, 51.455, -0.0005, 51.600)
                    .country(mContext.getString(R.string.country_GB)) // Restricts searches to just Great Britain
                    .backgroundColor(Color.parseColor(mContext.getString(R.string.color)))
                    .limit(10)
                    .build(PlaceOptions.MODE_CARDS)
            )
            .build(activity)
    }

    /**
     * Obtain necessary attributes of search location such as latitude and longitude
     * @param mapboxMap
     * @param selectedCarmenFeature
     * @param style of map
     */
    fun searchLocation(mapboxMap: MapboxMap, selectedCarmenFeature: CarmenFeature, style: Style) {
        val location = Locations(
            selectedCarmenFeature.placeName().toString(),
            selectedCarmenFeature.center()!!.latitude(),
            selectedCarmenFeature.center()!!.longitude()
        )
        if (!checkIfAlreadyInStops(location)) {
            updateStops(mapboxMap, selectedCarmenFeature, style)
        } else {
            hasDuplicationLocation.value = true
        }
    }

    /**
     * Update the added stops in the map
     * @param mapboxMap
     * @param selectedCarmenFeature
     * @param style of map
     */
    private fun updateStops(
        mapboxMap: MapboxMap,
        selectedCarmenFeature: CarmenFeature,
        style: Style
    ) {

        val latitude = selectedCarmenFeature.center()!!.latitude()
        val longitude = selectedCarmenFeature.center()!!.longitude()

        style.getSourceAs<GeoJsonSource>(MapboxConstants.GEO_JSON_SOURCE_LAYER_ID)?.setGeoJson(
            FeatureCollection.fromFeatures(
                arrayOf(
                    Feature.fromJson(
                        selectedCarmenFeature.toJson()
                    )
                )
            )
        )
        updateCamera(mapboxMap, latitude, longitude)
        postUpdateInfo()
    }

    private fun postUpdateInfo() {
        if (getUpdateInfo()) {
            updateMutableLiveData.value = updateInfo
            setUpdateInfo(false)
        } else {
            updateMutableLiveData.value = updateInfo
        }
    }

    /**
     * Update the camera of the map
     * @param mapboxMap
     * @param latitude
     * @param longitude
     */
    private fun updateCamera(mapboxMap: MapboxMap, latitude: Double, longitude: Double) {
        mapboxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(latitude, longitude))
                    .zoom(14.0)
                    .build()
            ), 4000
        )
    }

    /**
     * Check whether a current journey exists
     */
    private fun checkCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney) {
            showAlert.value = true
        }
        else {
            clearAllSharedPreferences()
            isReady.value = true
        }
    }

    /**
     * Getter function to return the current journey
     */
    fun getCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        if (!SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)) {
            val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
            clearJourneyLocations()
            setJourneyLocations(listOfLocations)
            isReady.value = true
        } else {
            hasCurrentJourney.value = false
        }
    }

    /**
     * Obtain necessary data of current journey
     */
    override fun continueWithCurrentJourney() {
        super.continueWithCurrentJourney()
        isReady.value = true
    }

    /**
     * Obtain necessary data for the newly calculated journey
     * @param newStops added recently by the user
     */
    override fun continueWithNewJourney(newStops: MutableList<Locations>) {
        super.continueWithNewJourney(newStops)
        isReady.value = true
    }

    fun cancelWork() {
        WorkHelper.cancelWork(mContext)
    }

    /**
     * Setting state of showing an alert
     * @param bool state of alert
     */
    fun setShowAlert(bool: Boolean) {
        showAlert.value = bool
    }

    /**
     * Getter function for the ShowAlertMutableLiveData
     * @return MutableLiveData containing the alert
     */
    fun getShowAlertMutableLiveData(): MutableLiveData<Boolean> {
        return showAlert
    }

    /**
     * Getter function to return the isReady
     * @return MutableLiveData
     */
    fun getIsReady(): MutableLiveData<Boolean> {
        return isReady
    }

    /**
     * Getter function to determine whether there is a current location
     * @return MutableLiveData
     */
    fun hasCurrentLocation(): MutableLiveData<Boolean> {
        return hasCurrentLocation
    }

    /**
     * Getter function to determine whether there current journey mutable live data
     * @return MutableLiveData
     */
    fun getHasCurrentJourney(): MutableLiveData<Boolean> {
        return hasCurrentJourney
    }

    /**
     * Getter function to determine whether there is a duplicate location
     * @return MutableLiveData
     */
    fun getHasDuplicationLocation(): MutableLiveData<Boolean> {
        return hasDuplicationLocation
    }

    /**
     * Getter function to return updated mutable live data
     * @return MutableLiveData
     */
    fun getUpdateMutableLiveData(): MutableLiveData<Boolean> {
        return updateMutableLiveData
    }

    /**
     * Attempt to log out user out of the application
     */
    fun logOut() {
        userRepository.logOut()
        clearAllSharedPreferences()
        logout.value = true
    }

    /**
     * Getter function to determine whether the user is logged out
     * @return MutableLiveData<Boolean> true or false to determine state
     */
    fun getLogout(): MutableLiveData<Boolean> {
        return logout
    }

    /**
     * Getter function to return a message
     * @return MutableLiveData<String> representing the message
     */
    fun getMessage(): MutableLiveData<String> {
        return message
    }
}