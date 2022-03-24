package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.HomePageRepository
import com.example.backstreet_cycles.data.repository.JourneyRepository
import com.example.backstreet_cycles.data.repository.LocationRepository
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.ui.views.HomePageActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
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
    private val homePageRepository: HomePageRepository = HomePageRepository(mApplication)
    private val locationRepository: LocationRepository = LocationRepository(mApplication)
    var stops: MutableLiveData<MutableList<Locations>> = MutableLiveData(locationRepository.getStops())
    private val firestore = Firebase.firestore

    private val mapRepository: JourneyRepository = JourneyRepository(mApplication,firestore)
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun initialiseLocationComponent(mapboxMap: MapboxMap): LocationComponent
    {
        return homePageRepository.initialiseLocationComponent(mapboxMap)
    }

    fun initialiseCurrentLocation(loadedMapStyle: Style, locationComponent: LocationComponent)
    {
        homePageRepository.initialiseCurrentLocation(loadedMapStyle, locationComponent)
    }

    fun displayingAttractions(mapView: MapView, mapboxMap: MapboxMap, loadedMapStyle: Style, data: List<Locations>)
    {
        homePageRepository.displayingAttractions(mapView, mapboxMap, loadedMapStyle,data)
    }

    fun getCurrentLocation(locationComponent: LocationComponent): Location?
    {
        return homePageRepository.getCurrentLocation(locationComponent)
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

    fun initialisePlaceAutoComplete(activity: HomePageActivity): Intent
    {
        return homePageRepository.initialisePlaceAutoComplete(activity)
    }

    fun updateCamera(mapboxMap: MapboxMap, latitude: Double, longitude: Double)
    {
        homePageRepository.updateCamera(mapboxMap, latitude, longitude)
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

        MapRepository.location.addAll(HomePageActivity.stops)

        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        val checkForARunningJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!checkForARunningJourney){
//            AlertHelper.alertDialog(MapRepository.location,mApplication)
//            alertDialog(MapRepository.location)
        } else{
            val locationPoints = PlannerHelper.setPoints(MapRepository.location)
            fetchRoute(mContext, HomePageActivity.mapboxNavigation, locationPoints, "cycling", false)
        }
    }

    private fun alertDialog(newStops: MutableList<Locations>) {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle("Planner Alert")
        builder.setMessage("There is already a planned journey that you are currently using." +
                "Do you want to continue with the current journey or with the newly created one?")

        builder.setPositiveButton(R.string.continue_with_current_journey) { dialog, which ->
            SharedPrefHelper.initialiseSharedPref(mApplication,"LOCATIONS")
            val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
            MapRepository.location = listOfLocations
            val listPoints = PlannerHelper.setPoints(listOfLocations)
            fetchRoute(mContext, HomePageActivity.mapboxNavigation, listPoints, "cycling", false)
        }

        builder.setNegativeButton(R.string.continue_with_newly_set_journey) { dialog, which ->
            val listPoints = PlannerHelper.setPoints(newStops)
            SharedPrefHelper.initialiseSharedPref(mApplication,"LOCATIONS")
            SharedPrefHelper.overrideSharedPref(newStops,Locations::class.java)
            fetchRoute(mContext, HomePageActivity.mapboxNavigation, listPoints, "cycling", false)
        }
        builder.show()
    }


}