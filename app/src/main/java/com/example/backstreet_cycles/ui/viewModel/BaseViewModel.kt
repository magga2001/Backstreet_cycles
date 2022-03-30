package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    protected val tflRepository: TflRepository,
    protected val mapboxRepository: MapboxRepository,
    protected val cyclistRepository: CyclistRepository,
    protected val userRepository: UserRepository,
    @ApplicationContext applicationContext: Context
) : ViewModel() {

    protected val mApplication: Application = getApplication(applicationContext)
    protected val mContext = applicationContext
    private val userDetail: MutableLiveData<Users> = MutableLiveData()

    //GENERIC

    open fun incrementNumCyclists() {
        cyclistRepository.incrementNumCyclists()
    }

    open fun decrementNumCyclists() {
        cyclistRepository.decrementNumCyclists()
    }

    open fun resetNumCyclists() {
        cyclistRepository.resetNumCyclist()
    }

    open fun getNumCyclists(): Int {
        return cyclistRepository.getNumCyclists()
    }

    //FIREBASE

    open fun getUserDetails() {
        userRepository.getUserDetails().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    userDetail.postValue(result.data!!)
                }
                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
            }
        }.launchIn(viewModelScope)
    }

    fun getUserDetailsData(): MutableLiveData<Users>
    {
        return userDetail
    }

    //TFL

    open fun getCurrentDocks(): MutableList<Dock> {
        return tflRepository.getCurrentDocks()
    }


    //MAPBOX

    open fun getJourneyCurrentRoute(): MutableList<DirectionsRoute> {
        return mapboxRepository.getJourneyCurrentRoute()
    }

    open fun getJourneyDistances(): MutableList<Double> {
        return mapboxRepository.getJourneyDistances()
    }

    open fun getJourneyDurations(): MutableList<Double> {
        return mapboxRepository.getJourneyDurations()
    }

    open fun getJourneyLocations(): MutableList<Locations> {
        return mapboxRepository.getJourneyLocations()
    }

    open fun getJourneyWayPointsLocations(): MutableList<Locations> {
        return mapboxRepository.getJourneyWayPointsLocations()
    }

    open fun setCurrentJourney(stops: MutableList<Locations>) {
        mapboxRepository.setJourneyLocations(stops)
    }

    open fun setCurrentWayPoint(locations: MutableList<Locations>) {
        mapboxRepository.setJourneyWayPointsLocations(locations)
    }

    open fun clearView() {
        mapboxRepository.clearJourneyWayPointsLocations()
        mapboxRepository.clearJourneyCurrentRoute()
    }

    open fun clearInfo() {
        mapboxRepository.clearJourneyDistances()
        mapboxRepository.clearJourneyDurations()
    }

    open fun clearDuplication(locations: MutableList<Locations>) {
        mapboxRepository.distinctJourneyLocations()
        locations.distinct()
    }

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

    open fun setOverviewRoute(context: Context, points: List<Point>): RouteOptions {
        return customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
    }

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

    open suspend fun getDock() {

        tflRepository.getDocks().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("New dock", result.data?.size.toString())

                    if (result.data != null && result.data.isNotEmpty()) {
                        tflRepository.setCurrentDocks(result.data!!)
                    }
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

    //SHARED PREF

    open fun continueWithCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
        mapboxRepository.clearJourneyLocations()
        mapboxRepository.setJourneyLocations(listOfLocations)
    }

    open fun continueWithNewJourney(newStops: MutableList<Locations>) {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(newStops, Locations::class.java)
    }
}