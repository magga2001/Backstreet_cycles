package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.utils.JsonHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Generic view model from which all the other view models inherit from
 */
@HiltViewModel
open class BaseViewModel @Inject constructor(
    protected val tflRepository: TflRepository,
    protected val mapboxRepository: MapboxRepository,
    protected val cyclistRepository: CyclistRepository,
    protected val userRepository: UserRepository,
    protected val application: Application,
    @ApplicationContext applicationContext: Context,
) : ViewModel() {

    protected val mApplication: Application = application
    protected val mContext = applicationContext
    private val userDetail: MutableLiveData<Users> = MutableLiveData()
    private val increaseCyclist: MutableLiveData<Boolean> = MutableLiveData()
    private val decreaseCyclist: MutableLiveData<Boolean> = MutableLiveData()

    //GENERIC

    /**
     * Increment the number of cyclists
     */
    open fun incrementNumCyclists() {
        if (getNumCyclists() < 4) {
            cyclistRepository.incrementNumCyclists()
            increaseCyclist.value = true
        } else {
            increaseCyclist.value = false
        }
    }

    /**
     * Decrement the number of cyclists
     */
    open fun decrementNumCyclists() {
        if (getNumCyclists() >= 2) {
            cyclistRepository.decrementNumCyclists()
            decreaseCyclist.value = true
        } else {
            decreaseCyclist.value = false
        }
    }

    /**
     * Reset the number of cyclists
     */
    open fun resetNumCyclists() {
        cyclistRepository.resetNumCyclist()
    }

    /**
     * Get the number of cyclists
     */
    open fun getNumCyclists(): Int {
        return cyclistRepository.getNumCyclists()
    }

    //FIREBASE

    /**
     * Get user's details from Firebase
     */
    open fun getUserDetails() {
        userRepository.getUserDetails().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    userDetail.value = result.data!!
                }
                is Resource.Error -> {

                }
                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Get user's details in MutableLiveData format
     */
    fun getUserInfo(): MutableLiveData<Users> {
        return userDetail
    }

    //TFL

    /**
     * Get the list of current docks from TFL repository
     */
    open fun getCurrentDocks(): MutableList<Dock> {
        return tflRepository.getCurrentDocks()
    }

    //MAPBOX

    /**
     * Get current journey route from Mapbox repository
     */
    open fun getJourneyCurrentRoute(): MutableList<DirectionsRoute> {
        return mapboxRepository.getJourneyCurrentRoute()
    }

    /**
     * Get current journey's distance from Mapbox repository
     */
    open fun getJourneyDistances(): MutableList<Double> {
        return mapboxRepository.getJourneyDistances()
    }

    /**
     * Get current journey's duration from Mapbox repository
     */
    open fun getJourneyDurations(): MutableList<Double> {
        return mapboxRepository.getJourneyDurations()
    }

    /**
     * Get current journey's stops from Mapbox repository
     */
    open fun getJourneyLocations(): MutableList<Locations> {
        return mapboxRepository.getJourneyLocations()
    }

    /**
     * Get current journey's locations from Mapbox repository
     */
    open fun getJourneyWayPointsLocations(): MutableList<Locations> {
        return mapboxRepository.getJourneyWayPointsLocations()
    }

    /**
     * Set journey's stops
     * @param stops list of stops
     */
    open fun setJourneyLocations(stops: MutableList<Locations>) {
        mapboxRepository.setJourneyLocations(stops)
    }

    /**
     * Set journey's locations
     * @param locations list of locations
     */
    open fun setCurrentWayPoint(locations: MutableList<Locations>) {
        mapboxRepository.setJourneyWayPointsLocations(locations)
    }

    /**
     * Clear journey's stops
     */
    open fun clearJourneyLocations() {
        mapboxRepository.clearJourneyLocations()
    }

    /**
     * Clear journey's locations and current route
     */
    open fun clearView() {
        mapboxRepository.clearJourneyWayPointsLocations()
        mapboxRepository.clearJourneyCurrentRoute()
    }

    /**
     * Clear journey's distance and duration information
     */
    open fun clearInfo() {
        mapboxRepository.clearJourneyDistances()
        mapboxRepository.clearJourneyDurations()
    }

    /**
     * Clear journey's duplicate locations
     * @param locations list of locations
     */
    open fun clearDuplication(locations: MutableList<Locations>) {
        mapboxRepository.distinctJourneyLocations()
        locations.distinct()
    }

    /**
     * Set route
     * @param context
     * @param points list of points
     * @param profile type of route
     */
    open fun setCustomiseRoute(
        context: Context,
        points: List<Point>,
        profile: String = MapboxConstants.CYCLING
    ): RouteOptions {
        return when (profile) {
            MapboxConstants.WALKING -> customiseRouteOptions(
                context,
                points,
                DirectionsCriteria.PROFILE_WALKING
            )
            else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
        }
    }

    /**
     * Set route overview
     * @param context
     * @param points list of points
     */
    open fun setOverviewRoute(context: Context, points: List<Point>): RouteOptions {
        return customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
    }

    /**
     * Setting a custom route
     * @param context
     * @param points list of points
     * @param criteria type of route
     */
    open fun customiseRouteOptions(
        context: Context,
        points: List<Point>,
        criteria: String
    ): RouteOptions {
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

    open fun getRoute() {
        clearView()
    }

    /**
     * Get dock from TFL repository
     */
    open suspend fun getDock() {
        tflRepository.getDocks().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data != null && result.data.isNotEmpty()) {
                        tflRepository.setCurrentDocks(result.data)
                    }
                    getRoute()
                }

                is Resource.Error -> {
                    val docksJson = JsonHelper.readJsonFile(mContext, "localDocks.json").toString()
                    val docks = JsonHelper.stringToObject(docksJson, Dock::class.java)
                    tflRepository.setCurrentDocks(docks!!.toMutableList())

                }
                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    //SHARED PREF

    /**
     * Get user's current journey
     */
    open fun continueWithCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney) {
            val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
            clearJourneyLocations()
            mapboxRepository.setJourneyLocations(listOfLocations)
        }
    }

    /**
     * Set new journey for a user
     * @param newStops list of stops
     */
    open fun continueWithNewJourney(newStops: MutableList<Locations>) {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(newStops, Locations::class.java)
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.CHECKED_BOXES)
        SharedPrefHelper.clearSharedPreferences()
    }

    /**
     * Clear shared preferences for locations
     */
    open fun clearSaveLocations() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        SharedPrefHelper.clearSharedPreferences()
    }

    /**
     * Clear all shared preferences related to journey
     */
    open fun clearAllSharedPreferences(){

        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.LOCATIONS)
        SharedPrefHelper.clearSharedPreferences()
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.DOCKS_LOCATIONS)
        SharedPrefHelper.clearSharedPreferences()
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.NUM_USERS)
        SharedPrefHelper.clearSharedPreferences()
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.CHECKED_BOXES)
        SharedPrefHelper.clearSharedPreferences()
    }

    /**
     * Get number of cyclist incremented by 1
     */
    fun getIncreaseCyclist(): MutableLiveData<Boolean> {
        return increaseCyclist
    }

    /**
     * Get number of cyclist decremented by 1
     */
    fun getDecreaseCyclist(): MutableLiveData<Boolean> {
        return decreaseCyclist
    }
}