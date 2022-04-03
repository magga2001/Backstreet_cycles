package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
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
import com.example.backstreet_cycles.domain.utils.*
import com.example.backstreet_cycles.interfaces.Planner
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class JourneyViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    application: Application,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(
    tflRepository,
    mapboxRepository,
    cyclistRepository,
    userRepository,
    application,
    applicationContext
), Planner {

    private val mapboxNavigation: MapboxNavigation by lazy {
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
    private var journeyState = JourneyState.START_WALKING
    private var isUpdateMap: Boolean = true
    private val updateMap: MutableLiveData<Boolean> = MutableLiveData()
    private val isReady: MutableLiveData<Boolean> = MutableLiveData()
    private val distanceMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val durationMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val priceMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val message: MutableLiveData<String> = MutableLiveData()

    /**
     * Getter function to obtain the relevant dock
     */
    override suspend fun getDock() {
        tflRepository.getDocks().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data != null && result.data.isNotEmpty()) {
                        tflRepository.setCurrentDocks(result.data)
                        val dockJSON = JsonHelper.objectToString(result.data, Dock::class.java)
                        JsonHelper.writeJsonFile(mContext, "localDocks.json", dockJSON)
                    }
                    isUpdateMap = false
                    fetchRoute(
                        context = mContext,
                        getJourneyLocations(),
                        MapboxConstants.CYCLING,
                        false
                    )
                }

                is Resource.Error -> {
                    val docksJson = JsonHelper.readJsonFile(mContext, "localDocks.json").toString()
                    val docks = JsonHelper.stringToObject(docksJson, Dock::class.java)
                    tflRepository.setCurrentDocks(docks!!.toMutableList())

                }

                is Resource.Loading -> {
                    Log.i("New dock", "Loading...")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun registerObservers(
        routesObserver: RoutesObserver,
        routeProgressObserver: RouteProgressObserver
    ) {
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
    }

    fun unregisterObservers(
        routesObserver: RoutesObserver,
        routeProgressObserver: RouteProgressObserver
    ) {
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
    }

    /**
     * Calculate fare of journey
     */
    fun calcBicycleRental() {
        PlannerHelper.calcBicycleRental(
            mApplication,
            getCurrentDocks(),
            getJourneyLocations(),
            getNumCyclists(),
            plannerInterface = this
        )
    }

    /**
     * Getter function to obtain the summary of the journey
     */
    fun getJourneyOverview() {
        journeyState = JourneyState.OVERVIEW
        getRoute(mContext, getJourneyLocations(), MapboxConstants.CYCLING, false)
    }

    /**
     * Update markers within the map
     * @param annotationApi
     */
    fun updateMapMarkerAnnotation(annotationApi: AnnotationPlugin) {
        MapAnnotationHelper.removeAnnotations()
        MapAnnotationHelper.addAnnotationToMap(
            context = mContext,
            getJourneyWayPointsLocations(),
            annotationApi,
            getJourneyState()
        )
    }

    /**
     * Getter function to obtain the route of journey
     * @param context
     * @param locations
     * @param profile
     * @param info
     */
    private fun getRoute(
        context: Context,
        locations: MutableList<Locations>,
        profile: String,
        info: Boolean
    ) {
        isUpdateMap = true
        fetchRoute(context = context, locations, profile, info)
    }

    /**
     * Getter function to obtain the route of journey
     * Avoids duplication of locations
     * @param context
     * @param locations
     * @param profile
     * @param info
     */
    private fun fetchRoute(
        context: Context,
        locations: MutableList<Locations>,
        profile: String,
        info: Boolean
    ) {
        clearDuplication(locations)
        val routeOptions: RouteOptions
        val points = locations.map { ConvertHelper.convertLocationToPoint(it) }

        if (!info) {
            clearInfo()
            setCurrentWayPoint(locations)
            routeOptions = setCustomiseRoute(context, points, profile)
            updateMapRouteLine(routeOptions, info)
        } else {
            routeOptions = setOverviewRoute(context, points)
            updateMapInfo(routeOptions, info)
        }
    }

    /**
     * Update the route line on the map
     * @param routeOptions
     * @param info
     */
    private fun updateMapRouteLine(routeOptions: RouteOptions, info: Boolean) {
        mapboxRepository.requestRoute(mapboxNavigation, routeOptions, info).onEach { result ->
            when (result) {
                is Resource.Success -> {
//                    isReadyMutableLiveData.postValue(status)
                    updateMap.value = isUpdateMap
                }

                is Resource.Error -> {
                    //Fail
//                    message.postValue(result.message!!)
                    message.value = result.message!!
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Update information on the map
     * @param routeOptions
     * @param info
     */
    private fun updateMapInfo(routeOptions: RouteOptions, info: Boolean) {
        mapboxRepository.requestRoute(mapboxNavigation, routeOptions, info).onEach { result ->
            when (result) {
                is Resource.Success -> {
//                    isReadyMutableLiveData.postValue(status)
                    updateMap.value = isUpdateMap
                    calcJourneyInfo()
                }

                is Resource.Error -> {
                    //Fail
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Calculate the statistics of the journey
     */
    private fun calcJourneyInfo() {
        distanceMutableLiveData.value =
            ConvertHelper.convertMToKm(mapboxRepository.getJourneyDistances()).toString()

        durationMutableLiveData.value =
            ConvertHelper.convertMsToS(mapboxRepository.getJourneyDurations()).toString()

        displayPrice()
    }

    /**
     * Updating the current selected journey
     * @param location
     * @param profile
     * @param locations
     * @param state
     */
    override fun onSelectedJourney(
        location: Locations,
        profile: String,
        locations: MutableList<Locations>,
        state: JourneyState
    ) {
        clearView()
        journeyState = state
        getRoute(context = mContext, locations, profile, false)
    }

    /**
     * Obtain the locations of the journey
     * @param locations
     */
    override fun onFetchJourney(locations: MutableList<Locations>) {
        journeyState = JourneyState.OVERVIEW
        getRoute(context = mContext, locations, MapboxConstants.CYCLING, true)
    }

    /**
     * Getter function to obtain planner interface
     * @return Planner
     */
    fun getPlannerInterface(): Planner {
        return this
    }

    /**
     * Initialise the route of the journey
     */
    fun setRoute() {
        mapboxNavigation.setRoutes(mapboxRepository.getJourneyCurrentRoute())
    }

    /**
     * Clear the route of the navigation
     */
    fun clearRoute() {
        mapboxNavigation.setRoutes(listOf())
    }

    /**
     * Terminate the journey
     * @param userDetails
     */
    fun finishJourney(userDetails: Users) {
        SharedPrefHelper.initialiseSharedPref(application,Constants.LOCATIONS)
        addJourneyToJourneyHistory(
            SharedPrefHelper.getSharedPref(Locations::class.java),
            userDetails
        )

        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.DOCKS_LOCATIONS)
        SharedPrefHelper.clearSharedPreferences()
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.NUM_USERS)
        SharedPrefHelper.clearSharedPreferences()
        SharedPrefHelper.initialiseSharedPref(mApplication,Constants.CHECKED_BOXES)
        SharedPrefHelper.clearSharedPreferences()
    }

    /**
     * Save the journey by recording it to the journey history
     * @param locations
     */
    private fun addJourneyToJourneyHistory(locations: MutableList<Locations>, user: Users){

        userRepository.addJourneyToJourneyHistory(locations, user).onEach { result ->

            when (result) {
                is Resource.Success -> {
                    message.value = result.data!!
                    isReady.value = true
                    SharedPrefHelper.clearSharedPreferences()
                }

                is Resource.Error -> {
                    message.value = result.message!!
                    isReady.value = false
                }

                is Resource.Loading -> {
                }
            }

        }.launchIn(viewModelScope)
    }

    /**
     * Indicate the cycle fare
     */
    private fun displayPrice() {
        val price = MapInfoHelper.getRental(getJourneyDurations())
        priceMutableLiveData.value = (price * getNumCyclists()).toString()
    }

    /**
     * Getter function to return the state of the journey
     * @return JourneyState
     */
    fun getJourneyState(): JourneyState{
        return journeyState
    }

    /**
     * Getter function to obtain the most recent data in the map
     * @return MutableLiveData
     */
    fun getUpdateMap(): MutableLiveData<Boolean> {
        return updateMap
    }

    /**
     * Getter function to return the state of isReady
     * @return MutableLiveData<Boolean>
     */
    fun getIsReady(): MutableLiveData<Boolean> {
        return isReady
    }

    /**
     * Getter function to return the distance
     * @return MutableLiveData<String>
     */
    fun getDistanceMutableLiveData(): MutableLiveData<String> {
        return distanceMutableLiveData
    }

    /**
     * Getter function to return the duration
     * @return MutableLiveData<String>
     */
    fun getDurationMutableLiveData(): MutableLiveData<String> {
        return durationMutableLiveData
    }

    /**
     * Getter function to return the price
     * @return MutableLiveData<String>
     */
    fun getPriceMutableLiveData(): MutableLiveData<String> {
        return priceMutableLiveData
    }

    /**
     * Getter function to return the message
     * @return MutableLiveData<String>
     */
    fun getMessage(): MutableLiveData<String> {
        return message
    }

    /**
     * Getter function to obtain the state of the checked boxes
     * @return List<String>
     */
    fun getTheCheckedBoxes(): List<String> {
        SharedPrefHelper.initialiseSharedPref(application,Constants.CHECKED_BOXES)
        return SharedPrefHelper.getSharedPref(String::class.java)
    }

    /**
     * Getter function to obtain the state of the checked boxes shared preferences
     * @return checkedBoxes
     */
    fun storeCheckedBoxesSharedPref(checkedBoxes: List<String>) {
        SharedPrefHelper.initialiseSharedPref(application,Constants.CHECKED_BOXES)
        SharedPrefHelper.overrideSharedPref(checkedBoxes.toMutableList(),String::class.java)
    }
}