package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.utils.JsonHelper
import com.example.backstreet_cycles.domain.utils.ConvertHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class JourneyHistoryViewModel @Inject constructor(
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
) {

    private var stops: MutableList<Locations> = mutableListOf()
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val message: MutableLiveData<String> = MutableLiveData()
    private var showAlert: MutableLiveData<Boolean> = MutableLiveData(false)
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

    override fun getRoute() {
        super.getRoute()
        setCurrentJourney(stops)
        checkCurrentJourney()
    }

    private fun getMapBoxRoute(routeOptions: RouteOptions) {
        mapboxRepository.requestRoute(mapboxNavigation, routeOptions).onEach { result ->
            when (result) {
                is Resource.Success -> {
//                    isReadyMutableLiveData.postValue(true)
                    isReadyMutableLiveData.value = true
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

    private fun fetchRoute(
        context: Context,
        locations: MutableList<Locations>
    ) {

        resetNumCyclists()
        clearDuplication(locations)
        val points = locations.map { ConvertHelper.convertLocationToPoint(it) }

        clearInfo()
        setCurrentWayPoint(locations)
        val routeOptions = setCustomiseRoute(context, points)
        getMapBoxRoute(routeOptions)
    }

    private fun checkCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney) {
//            showAlert.postValue(true)
            showAlert.value = true
        } else {
            fetchRoute(mContext, getJourneyLocations())
        }
    }

    override fun continueWithCurrentJourney() {
        super.continueWithCurrentJourney()
        fetchRoute(
            mContext,
            getJourneyLocations()
        )
    }

    override fun continueWithNewJourney(newStops: MutableList<Locations>) {
        super.continueWithNewJourney(newStops)
        fetchRoute(
            mContext,
            newStops
        )
    }

    fun getJourneyHistory(userDetails: Users): MutableList<List<Locations>> {
        val listLocations = emptyList<List<Locations>>().toMutableList()
        for (journey in userDetails.journeyHistory) {
            val serializedObject: String = journey
            JsonHelper.stringToObject(serializedObject, Locations::class.java)
                ?.let { listLocations.add(it) }
        }
        return listLocations
    }

    fun addAllStops(checkpoints: MutableList<Locations>) {
        stops.addAll(checkpoints)
    }

    fun clearAllStops() {
        stops.clear()
    }

    fun getMapBoxNavigation(): MapboxNavigation {
        return mapboxNavigation
    }

    fun destroyMapboxNavigation() {
        MapboxNavigationProvider.destroy()
    }

    fun setShowAlert(bool: Boolean) {
//        showAlert.postValue(bool)
        showAlert.value = bool
    }

    fun getShowAlertMutableLiveData(): MutableLiveData<Boolean> {
        return showAlert
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean> {
        return isReadyMutableLiveData
    }

    fun updateCurrentLocation(currentLocation: Location) {
        for (stop in stops) {
            if (stop.name == "Current Location") {

                val longitude = currentLocation.longitude
                val latitude = currentLocation.latitude

                stop.lat = latitude
                stop.lon = longitude
            }
        }
    }

    fun getMessage(): MutableLiveData<String>
    {
        return message
    }

}