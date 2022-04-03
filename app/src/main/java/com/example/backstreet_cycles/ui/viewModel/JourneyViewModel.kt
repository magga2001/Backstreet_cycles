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
import com.example.backstreet_cycles.domain.repositoryInt.*
import com.example.backstreet_cycles.domain.utils.*
import com.example.backstreet_cycles.interfaces.Planner
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
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
    private val distanceMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val durationMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val priceMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val message: MutableLiveData<String> = MutableLiveData()

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

    fun calcBicycleRental() {
        PlannerHelper.calcBicycleRental(
            mApplication,
            getCurrentDocks(),
            getJourneyLocations(),
            getNumCyclists(),
            plannerInterface = this
        )
    }

    fun getJourneyOverview() {
        journeyState = JourneyState.OVERVIEW
        getRoute(mContext, getJourneyLocations(), MapboxConstants.CYCLING, false)
    }

    fun updateMapMarkerAnnotation(annotationApi: AnnotationPlugin) {
        MapAnnotationHelper.removeAnnotations()
        MapAnnotationHelper.addAnnotationToMap(
            context = mContext,
            getJourneyWayPointsLocations(),
            annotationApi,
            getJourneyState()
        )
    }

    private fun getRoute(
        context: Context,
        locations: MutableList<Locations>,
        profile: String,
        info: Boolean
    ) {
        isUpdateMap = true
        fetchRoute(context = context, locations, profile, info)
    }

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

    private fun calcJourneyInfo() {
        distanceMutableLiveData.value =
            ConvertHelper.convertMToKm(mapboxRepository.getJourneyDistances()).toString()

        durationMutableLiveData.value =
            ConvertHelper.convertMsToS(mapboxRepository.getJourneyDurations()).toString()

        displayPrice()
    }

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

    override fun onFetchJourney(locations: MutableList<Locations>) {
        journeyState = JourneyState.OVERVIEW
        getRoute(context = mContext, locations, MapboxConstants.CYCLING, true)
    }

    fun getPlannerInterface(): Planner {
        return this
    }

    fun setRoute() {
        mapboxNavigation.setRoutes(mapboxRepository.getJourneyCurrentRoute())
    }

    fun clearRoute() {
        mapboxNavigation.setRoutes(listOf())
    }

    fun finishJourney(userDetails: Users) {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        addJourneyToJourneyHistory(
            SharedPrefHelper.getSharedPref(Locations::class.java),
            userDetails
        )
        SharedPrefHelper.clearSharedPreferences()
    }

    private fun addJourneyToJourneyHistory(locations: MutableList<Locations>, user: Users){

        userRepository.addJourneyToJourneyHistory(locations, user)
    }

    private fun displayPrice() {
        val price = MapInfoHelper.getRental(getJourneyDurations())
        priceMutableLiveData.value = (price * getNumCyclists()).toString()
    }

    fun getJourneyState(): JourneyState{
        return journeyState
    }

    fun getUpdateMap(): MutableLiveData<Boolean> {
        return updateMap
    }

    fun getDistanceMutableLiveData(): MutableLiveData<String> {
        return distanceMutableLiveData
    }

    fun getDurationMutableLiveData(): MutableLiveData<String> {
        return durationMutableLiveData
    }

    fun getPriceMutableLiveData(): MutableLiveData<String> {
        return priceMutableLiveData
    }

    fun getMessage(): MutableLiveData<String> {
        return message
    }
}