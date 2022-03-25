package com.example.backstreet_cycles.ui.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.JourneyRepository
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.example.backstreet_cycles.domain.utils.BitmapHelper
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
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
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val getDockUseCase: GetDockUseCase,
    private val getMapboxUseCase: GetMapboxUseCase,
    private val locationRepository: LocationRepository,
    @ApplicationContext applicationContext: Context
): ViewModel() {

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

    private val mApplication = getApplication(applicationContext)
    private val mContext = applicationContext
    private var showAlert: MutableLiveData<Boolean> = MutableLiveData(false)
    private val fireStore = Firebase.firestore
    private var numUsers = 1
    private var stops: MutableList<Locations> = mutableListOf()
    private var updateInfo:Boolean = false

    private val mapRepository: JourneyRepository = JourneyRepository(mApplication,fireStore)
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val hasCurrentLocation: MutableLiveData<Boolean> = MutableLiveData()
    private val hasCurrentJourneyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val hasDuplicationLocation: MutableLiveData<Boolean> = MutableLiveData()
    private val updateMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun setShowAlert(bool: Boolean){
        showAlert.postValue(bool)
    }

    fun getShowAlertMutableLiveData(): MutableLiveData<Boolean> {
        return showAlert
    }

    fun incrementNumUsers(){
        ++numUsers
    }

    fun decrementNumUsers(){
        --numUsers
    }

    fun getNumUsers(): Int{
        return numUsers
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

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

    //New code

    fun getDock()
    {
        getDockUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("New dock", result.data?.size.toString())
                    getRoute()
                }

                is Resource.Error -> {
                    Log.i("New dock", "Error")

                }
                is Resource.Loading -> {
                    Log.i("New dock", "Loading...")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchRoute(context: Context,
                           points: MutableList<Point>,
                           profile: String,
                           info: Boolean)
    {

        val routeOptions: RouteOptions

        clearDuplication(points)

        if(!info)
        {
            clearInfo()
            MapRepository.wayPoints.addAll(points)

            routeOptions = when(profile)
            {
                "walking" -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_WALKING)
                else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
            }

            getMapboxUseCase(mapboxNavigation,routeOptions,info).onEach {

                Log.i("current Route succeed:", it.toString())

                isReadyMutableLiveData.postValue(true)

            }.launchIn(viewModelScope)

        }else
        {
            routeOptions = customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)

            getMapboxUseCase(mapboxNavigation,routeOptions,info).onEach {

                Log.i("current Route succeed:", it.toString())

                isReadyMutableLiveData.postValue(true)

            }.launchIn(viewModelScope)
        }
    }

    private fun customiseRouteOptions(context: Context, points: List<Point>, criteria: String): RouteOptions
    {
        return RouteOptions.builder()
            // applies the default parameters to route options
            .applyDefaultNavigationOptions(DirectionsCriteria.PROFILE_CYCLING)
            .applyLanguageAndVoiceUnitOptions(context)
            .profile(criteria)
            // lists the coordinate pair i.e. origin and destination
            // If you want to specify waypoints you can pass list of points instead of null
            .coordinatesList(points)
            // set it to true if you want to receive alternate routes to your destination
            .alternatives(true)
            .build()
    }

    fun getRoute()
    {
        clearView()
        MapRepository.location.addAll(stops)
        checkCurrentJourney()

    }

    fun getCurrentJourney()
    {
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        if (!SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)){
            val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
            MapRepository.location = listOfLocations
            val listPoints = PlannerHelper.setPoints(listOfLocations)
            fetchRoute(mContext, listPoints ,MapboxConstants.CYCLING, false)
        }else
        {
            hasCurrentJourneyMutableLiveData.postValue(false)
        }
    }

    private fun checkCurrentJourney()
    {
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney){
            showAlert.postValue(true)
        } else{
            val locationPoints = PlannerHelper.setPoints(MapRepository.location)
            fetchRoute(mContext, locationPoints, MapboxConstants.CYCLING, false)
        }
    }

    fun saveJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.NUM_USERS)
        SharedPrefHelper.overrideSharedPref(mutableListOf(getNumUsers().toString()),String::class.java)
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(MapRepository.location,Locations::class.java)
    }

    fun cancelWork() {
        WorkHelper.cancelWork(mContext)
    }

    fun continueWithCurrentJourney(){
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
        MapRepository.location = listOfLocations
        val listPoints = PlannerHelper.setPoints(listOfLocations)
        fetchRoute(mContext, listPoints, MapboxConstants.CYCLING, false)
    }

    fun continueWithNewJourney(newStops: MutableList<Locations>){
        val listPoints = PlannerHelper.setPoints(newStops)
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(newStops, Locations::class.java)
        fetchRoute(mContext, listPoints, MapboxConstants.CYCLING, false)
    }

    fun initLocationComponent(mapboxMap: MapboxMap): LocationComponent
    {
        return mapboxMap.locationComponent
    }

    @SuppressLint("MissingPermission")
    fun initCurrentLocation(loadedMapStyle: Style, locationComponent: LocationComponent) {
        val customLocationComponentOptions = LocationComponentOptions.builder(mApplication)
            .pulseEnabled(true)
            .build()

        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(mApplication, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()
        )

        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING
        locationComponent.renderMode = RenderMode.COMPASS
    }

    fun displayingAttractions(symbolManager: SymbolManager, loadedMapStyle: Style, data: List<Locations>) {
        val textSize = 15.0F
        val textColor = "black"

        symbolManager.iconAllowOverlap = true
        val bitmap = BitmapHelper.bitmapFromDrawableRes(mApplication, R.drawable.tourist_attraction_icon) as Bitmap
        loadedMapStyle.addImage("myMarker", Bitmap.createScaledBitmap(bitmap, 100, 120, false))
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

    fun checkCurrentLocation()
    {
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
                    .bbox(-0.309133,51.416601,0.075759,51.605545)
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

            if (getUpdateInfo()) {
                setUpdateInfo(false)
                updateMutableLiveData.postValue(updateInfo)
            } else {
                updateMutableLiveData.postValue(updateInfo)
            }
        }
        else{
            hasDuplicationLocation.postValue(true)
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

    private fun clearInfo()
    {
        MapRepository.distances.clear()
        MapRepository.durations.clear()
    }

    private fun clearView()
    {
        MapRepository.wayPoints.clear()
        MapRepository.currentRoute.clear()
    }

    private fun clearDuplication(points: MutableList<Point>)
    {
        MapRepository.location.distinct()
        points.distinct()
    }

    fun getMapBoxNavigation(): MapboxNavigation
    {
        return mapboxNavigation
    }

    fun destroyMapboxNavigation()
    {
        mapboxNavigation.onDestroy()
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

    fun hasCurrentLocation(): MutableLiveData<Boolean>
    {
        return hasCurrentLocation
    }

    fun getHasCurrentJourneyMutableLiveData(): MutableLiveData<Boolean>
    {
        return hasCurrentJourneyMutableLiveData
    }

    fun getHasDuplicationLocation(): MutableLiveData<Boolean>
    {
        return hasDuplicationLocation
    }

    fun getUpdateMutableLiveData(): MutableLiveData<Boolean>
    {
        return updateMutableLiveData
    }

}