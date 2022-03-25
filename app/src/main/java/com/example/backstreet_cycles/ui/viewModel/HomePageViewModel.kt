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
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.JourneyRepository
import com.example.backstreet_cycles.data.repository.LocationRepository
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.example.backstreet_cycles.domain.utils.BitmapHelper
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.example.backstreet_cycles.ui.views.HomePageActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.core.MapboxNavigation
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
    @ApplicationContext applicationContext: Context
): ViewModel() {

    private val mApplication = getApplication(applicationContext)
    private val mContext = applicationContext
    private val locationRepository: LocationRepository = LocationRepository(mApplication)
    private var showAlert: MutableLiveData<Boolean> = MutableLiveData(false)
    private val firestore = Firebase.firestore
    private var numUsers = 1

    private val mapRepository: JourneyRepository = JourneyRepository(mApplication,firestore)
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

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

    fun addAllStops(checkpoints: MutableList<Locations>){
        locationRepository.addAllStops(checkpoints)
    }

    fun getStops(): MutableList<Locations>{
        return locationRepository.getStops()
    }

    fun addStop(stop: Locations){
        locationRepository.addStop(stop)
    }

    fun addStop(index: Int, stop: Locations){
        locationRepository.addStop(index, stop)
    }

    fun removeStop(stop: Locations){
        locationRepository.removeStop(stop)
    }

    fun removeStopAt(index: Int){
        locationRepository.removeStopAt(index)
    }

    fun getTouristAttractions(): List<Locations> {
        return locationRepository.getTouristLocations()
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

    //New code

    fun getDock()
    {
        getDockUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("New dock", result.data?.size.toString())
                    val points = PlannerHelper.setPoints(MapRepository.location)
                    //fetchRoute(mapboxNavigation, points, "cycling", false)

                    //isReadyMutableLiveData.postValue(true)
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

    fun fetchRoute(context: Context,
                   mapboxNavigation: MapboxNavigation,
                   points: MutableList<Point>,
                   profile: String,
                   info: Boolean)
    {

        val routeOptions: RouteOptions

        MapRepository.location.distinct()
        points.distinct()

        if(!info)
        {
            MapRepository.distances.clear()
            MapRepository.durations.clear()
            MapRepository.wayPoints.addAll(points)

            routeOptions = when(profile)
            {
                "walking" -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_WALKING)
                else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
            }

//            TflHelper.getDock(context = mContext,
//                object : CallbackResource<MutableList<Dock>> {
//                    override fun getResult(objects: MutableList<Dock>) {
//
////                        val bundle = Bundle()
////                        bundle.putSerializable("current_route", MapRepository.currentRoute)
////                        intent.putExtra("current_bundle",bundle)
//                        fetchPoints()
//                    }
//                }
//            )

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

    fun fetchPoints()
    {
        MapRepository.wayPoints.clear()
        MapRepository.currentRoute.clear()

        MapRepository.location.addAll(locationRepository.getStops())

        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        val checkForARunningJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!checkForARunningJourney){
            showAlert.postValue(true)
        } else{
            val locationPoints = PlannerHelper.setPoints(MapRepository.location)
            fetchRoute(mContext, HomePageActivity.mapboxNavigation, locationPoints, "cycling", false)
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
        fetchRoute(mContext, HomePageActivity.mapboxNavigation, listPoints, "cycling", false)
    }

    fun continueWithNewJourney(newStops: MutableList<Locations>){
        val listPoints = PlannerHelper.setPoints(newStops)
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(newStops, Locations::class.java)
        fetchRoute(mContext, HomePageActivity.mapboxNavigation, listPoints, "cycling", false)
    }

    fun initLocationComponent(mapboxMap: MapboxMap): LocationComponent
    {
        return mapboxMap.locationComponent
    }

//    Why are we suppressing it?
    @SuppressLint("MissingPermission")
    @JvmName("initCurrentLocation1")
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

    fun displayingAttractions(mapView: MapView, mapboxMap: MapboxMap, loadedMapStyle: Style, data: List<Locations>) {
        val textSize = 15.0F
        val textColor = "black"


        val symbolManager = SymbolManager(mapView, mapboxMap, loadedMapStyle)
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

    fun getCurrentLocation(locationComponent: LocationComponent): Location? {
        return locationComponent.lastKnownLocation

    }

    fun initialisePlaceAutoComplete(activity: Activity): Intent {
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

    fun updateCamera(mapboxMap: MapboxMap, latitude: Double, longitude: Double) {
        mapboxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(latitude,longitude))
                    .zoom(14.0)
                    .build()
            ), 4000
        )
    }

    fun clearAllStops() {
        locationRepository.clearAllStops()
    }

}