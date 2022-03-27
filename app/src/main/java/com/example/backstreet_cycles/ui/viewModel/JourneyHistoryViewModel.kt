package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.example.backstreet_cycles.domain.utils.JsonHelper
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
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
    getDockUseCase: GetDockUseCase,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    private val getMapboxUseCase: GetMapboxUseCase,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(getDockUseCase, cyclistRepository, userRepository,applicationContext)  {

    private var stops: MutableList<Locations> = mutableListOf()
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var showAlert: MutableLiveData<Boolean> = MutableLiveData(false)
    private val userDetailsMutableLiveData: MutableLiveData<Users> = userRepository.getUserDetailsMutableLiveData()
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
        getMapboxUseCase(mapboxNavigation,routeOptions).onEach {
            isReadyMutableLiveData.postValue(true)
        }.launchIn(viewModelScope)
    }

    private fun fetchRoute(context: Context,
                           locations: MutableList<Locations>) {

        resetNumCyclists()
        clearDuplication(locations)
        val points = locations.map { PlannerHelper.convertLocationToPoint(it) }

        clearInfo()
        setCurrentWayPoint(locations)
        val routeOptions = setCustomiseRoute(context, points)
        getMapBoxRoute(routeOptions)
    }

    private fun checkCurrentJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney){
            showAlert.postValue(true)
        } else{
            fetchRoute(mContext, BackstreetApplication.locations)
        }
    }

    override fun continueWithCurrentJourney(){
        super.continueWithCurrentJourney()
        fetchRoute(
            mContext,
            BackstreetApplication.locations
        )
    }

    override fun continueWithNewJourney(newStops: MutableList<Locations>){
        super.continueWithNewJourney(newStops)
        fetchRoute(
            mContext,
            newStops
        )
    }

    fun getJourneyHistory(userDetails: Users):MutableList<List<Locations>> {
        val listLocations = emptyList<List<Locations>>().toMutableList()
        for (journey in userDetails.journeyHistory){
            val serializedObject: String = journey
            JsonHelper.stringToObject(serializedObject,Locations::class.java)
                ?.let { listLocations.add(it) }
        }
        return listLocations
    }

    fun addAllStops(checkpoints: MutableList<Locations>){
        stops.addAll(checkpoints)
    }

    fun clearAllStops() {
        stops.clear()
    }

    fun getMapBoxNavigation(): MapboxNavigation
    {
        return mapboxNavigation
    }

    fun destroyMapboxNavigation()
    {
        MapboxNavigationProvider.destroy()
    }

    fun setShowAlert(bool: Boolean){
        showAlert.postValue(bool)
    }

    fun getShowAlertMutableLiveData(): MutableLiveData<Boolean> {
        return showAlert
    }

    fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
        return userDetailsMutableLiveData
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

    fun updateCurrentLocation(currentLocation: Location) {
        for(stop in stops){
            if(stop.name == "Current Location"){

                val longitude = currentLocation.longitude
                val latitude = currentLocation.latitude

                stop.lat = latitude
                stop.lon = longitude
            }
        }
    }

}