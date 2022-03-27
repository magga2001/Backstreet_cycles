package com.example.backstreet_cycles.ui.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.*
import com.example.backstreet_cycles.domain.utils.BitmapHelper
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
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
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(tflRepository, mapboxRepository, cyclistRepository, userRepository,applicationContext) {

    private val mapboxNavigation by lazy {

        if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            MapboxNavigationProvider.create(
                NavigationOptions.Builder(mApplication)
                    .accessToken(mApplication.getString(R.string.mapbox_access_token))
                    .build()
            )
        }
    }
    private var showAlert: MutableLiveData<Boolean> = MutableLiveData(false)
    private var stops: MutableList<Locations> = mutableListOf()
    private var updateInfo:Boolean = false

    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val hasCurrentLocation: MutableLiveData<Boolean> = MutableLiveData()
    private val hasCurrentJourneyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val hasDuplicationLocation: MutableLiveData<Boolean> = MutableLiveData()
    private val updateMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun initLocationComponent(mapboxMap: MapboxMap): LocationComponent {
        return mapboxMap.locationComponent
    }

    @SuppressLint("MissingPermission")
    fun initCurrentLocation(loadedMapStyle: Style, locationComponent: LocationComponent) {
        val customLocationComponentOptions = customiseLocationPuck()
        activateLocationComponent(locationComponent, loadedMapStyle, customLocationComponentOptions)
        showLocationComponent(locationComponent)
    }

    private fun customiseLocationPuck(): LocationComponentOptions
    {
        return LocationComponentOptions.builder(mApplication)
            .pulseEnabled(true)
            .build()
    }

    private fun activateLocationComponent(
        locationComponent: LocationComponent,
        loadedMapStyle: Style,
        customLocationComponentOptions: LocationComponentOptions
    )
    {
        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(mApplication, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()
        )
    }

    @SuppressLint("MissingPermission")
    private fun showLocationComponent(locationComponent: LocationComponent)
    {
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING
        locationComponent.renderMode = RenderMode.COMPASS
    }

    fun setShowAlert(bool: Boolean){
        showAlert.postValue(bool)
    }

    fun getShowAlertMutableLiveData(): MutableLiveData<Boolean> {
        return showAlert
    }

    fun addStop(stop: Locations){
        stops.add(stop)
    }

    fun addStop(index: Int, stop: Locations){
        stops.add(index, stop)
    }

    fun removeStop(stop: Locations){
        stops.remove(stop)
    }

    fun removeStopAt(index: Int){
        stops.removeAt(index)
    }

    fun getStops(): MutableList<Locations> {
        return stops
    }

    private fun checkIfAlreadyInStops(location : Locations): Boolean{
        return stops.contains(location)
    }

    fun getTouristAttractions(): List<Locations> {
        return locationRepository.getTouristLocations()
    }

    fun setUpdateInfo(info: Boolean)
    {
        updateInfo = info
    }

    private fun getUpdateInfo(): Boolean
    {
        return updateInfo
    }

    override fun getRoute()
    {
        super.getRoute()
        setCurrentJourney(stops)
        checkCurrentJourney()
    }

    private fun getMapBoxRoute(routeOptions: RouteOptions)
    {
        mapboxRepository.requestRoute(mapboxNavigation,routeOptions).onEach {
            if(it != DirectionsRoute.fromJson("")){
                isReadyMutableLiveData.postValue(true)
            }else
            {
                isReadyMutableLiveData.postValue(false)
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchRoute(context: Context,
                           locations: MutableList<Locations>) {
        clearDuplication(locations)
        val points = locations.map { PlannerHelper.convertLocationToPoint(it) }

        clearInfo()
        setCurrentWayPoint(locations)
        val routeOptions = setCustomiseRoute(context, points)
        getMapBoxRoute(routeOptions)
    }

    fun displayingAttractions(symbolManager: SymbolManager, loadedMapStyle: Style, data: List<Locations>) {
        val textSize = 15.0F
        val textColor = "black"

        symbolManager.iconAllowOverlap = true
        val bitmap = BitmapHelper.bitmapFromDrawableRes(mApplication,
            R.drawable.tourist_attraction_icon
        ) as Bitmap
        loadedMapStyle.addImage("myMarker", Bitmap.createScaledBitmap(bitmap, 80, 80, false))
        for (attraction in data) {
            symbolManager.create(
                SymbolOptions()
                    .withLatLng(LatLng(attraction.lat, attraction.lon))
                    .withIconImage("myMarker")
                    .withIconAnchor("right")
                    .withTextField(attraction.name)
                    .withTextSize(textSize)
                    .withTextColor(textColor)
                    .withTextAnchor("left")
            )
        }
    }

    fun checkCurrentLocation() {
        val stop = stops.map { it.name }.contains("Current Location")
        hasCurrentLocation.postValue(stop)
    }

    fun updateCurrentLocation(locationComponent: LocationComponent)
    {
        for(stop in stops){
            if(stop.name == "Current Location"){

                val longitude = getCurrentLocation(locationComponent)!!.longitude
                val latitude = getCurrentLocation(locationComponent)!!.latitude

                stop.lat = latitude
                stop.lon = longitude
            }
        }
    }

    fun getCurrentLocation(locationComponent: LocationComponent): Location? {
        return locationComponent.lastKnownLocation
    }

    fun initPlaceAutoComplete(activity: Activity): Intent {
        return PlaceAutocomplete.IntentBuilder()
            .accessToken(mApplication.getString(R.string.mapbox_access_token)).
            placeOptions(
                PlaceOptions.builder()
                    .bbox(-0.240,51.455,-0.0005,51.600)
                    .country("GB") // Restricts searches to just Great Britain
                    .backgroundColor(Color.parseColor("#EEEEEE"))
                    .limit(10)
                    .build(PlaceOptions.MODE_CARDS)
            )
            .build(activity)
    }

    fun searchLocation(mapboxMap: MapboxMap,selectedCarmenFeature: CarmenFeature, style: Style)
    {
        val location = Locations(
            selectedCarmenFeature.placeName().toString(),
            selectedCarmenFeature.center()!!.latitude(),
            selectedCarmenFeature.center()!!.longitude())
        if(!checkIfAlreadyInStops(location)){
            updateStops(mapboxMap, selectedCarmenFeature, style)
        }
        else{
            hasDuplicationLocation.postValue(true)
        }
    }

    private fun updateStops(mapboxMap: MapboxMap,
                            selectedCarmenFeature: CarmenFeature,
                            style: Style){

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
            updateMutableLiveData.postValue(updateInfo)
            setUpdateInfo(false)
        } else {
            updateMutableLiveData.postValue(updateInfo)
        }
    }

    private fun updateCamera(mapboxMap: MapboxMap, latitude: Double, longitude: Double) {
        mapboxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(latitude,longitude))
                    .zoom(14.0)
                    .build()
            ), 4000
        )
    }

    private fun checkCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney){
            showAlert.postValue(true)
        } else{
            fetchRoute(mContext, getJourneyLocations())
        }
    }

    fun getCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        if (!SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)){
            val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
            mapboxRepository.setJourneyLocations(listOfLocations)
            fetchRoute(mContext, listOfLocations)
        }else
        {
            hasCurrentJourneyMutableLiveData.postValue(false)
        }
    }

    override fun continueWithCurrentJourney(){
        super.continueWithCurrentJourney()
        fetchRoute(
            mContext,
            getJourneyLocations(),
        )
    }

    override fun continueWithNewJourney(newStops: MutableList<Locations>){
        super.continueWithNewJourney(newStops)
        fetchRoute(
            mContext,
            newStops,
        )
    }

    fun saveJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.NUM_USERS)
        SharedPrefHelper.overrideSharedPref(mutableListOf(getNumCyclists().toString()),String::class.java)
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(getJourneyLocations(),Locations::class.java)
    }

    fun cancelWork() {
        WorkHelper.cancelWork(mContext)
    }

    fun getMapBoxNavigation(): MapboxNavigation {
        return mapboxNavigation
    }

    fun destroyMapboxNavigation() {
        MapboxNavigationProvider.destroy()
        mapboxNavigation.onDestroy()
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean> {
        return isReadyMutableLiveData
    }

    fun hasCurrentLocation(): MutableLiveData<Boolean> {
        return hasCurrentLocation
    }

    fun getHasCurrentJourneyMutableLiveData(): MutableLiveData<Boolean> {
        return hasCurrentJourneyMutableLiveData
    }

    fun getHasDuplicationLocation(): MutableLiveData<Boolean> {
        return hasDuplicationLocation
    }

    fun getUpdateMutableLiveData(): MutableLiveData<Boolean> {
        return updateMutableLiveData
    }
}