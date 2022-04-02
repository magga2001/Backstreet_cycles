package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.*
import com.example.backstreet_cycles.domain.utils.ConvertHelper
import com.example.backstreet_cycles.domain.utils.JsonHelper
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
class LoadingViewModel @Inject constructor(
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

    private val message: MutableLiveData<String> = MutableLiveData()
    private val isReadyMutableLiveData = MutableLiveData<Boolean>()

    /**
     * Getter function for the route of the journey
     */
    override fun getRoute() {
        super.getRoute()
        fetchRoute(mContext, getJourneyLocations())
    }

    /**
     * Receive the route of the journey
     * @param context
     * @param locations
     */
    private fun fetchRoute(
        context: Context,
        locations: MutableList<Locations>
    ) {
        clearDuplication(locations)
        val points = locations.map { ConvertHelper.convertLocationToPoint(it) }

        clearInfo()
        setCurrentWayPoint(locations)
        val routeOptions = setCustomiseRoute(context, points)
        getMapBoxRoute(routeOptions)
    }

    /**
     * Getter function for the representation of the route of the journey
     */
    private fun getMapBoxRoute(routeOptions: RouteOptions) {
        mapboxRepository.requestRoute(mapboxNavigation, routeOptions).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    isReadyMutableLiveData.value = true
                }

                is Resource.Error -> {
                    //Fail
                    message.postValue(result.message!!)
                    isReadyMutableLiveData.value = false
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Obtain necessary data to save the journey
     */
    fun saveJourney() {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.NUM_USERS)
        SharedPrefHelper.overrideSharedPref(
            mutableListOf(getNumCyclists().toString()),
            String::class.java
        )
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(getJourneyLocations(), Locations::class.java)
    }

    /**
     * Getter function to check status of data set up
     * @return MutableLiveData in the form of a boolean to confirm status
     */
    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean> {
        return isReadyMutableLiveData
    }

    /**
     * Getter function to return the Mapbox navigation
     * @return MapboxNavigation
     */
    fun getMapBoxNavigation(): MapboxNavigation {
        return mapboxNavigation
    }

    /**
     * Terminate the Mapbox navigation
     */
    fun destroyMapboxNavigation() {
        MapboxNavigationProvider.destroy()
        mapboxNavigation.onDestroy()
    }

    /**
     * Getter function to return a message
     * @return MutableLiveData<String> representing the message
     */
    fun getMessage(): MutableLiveData<String> {
        return message
    }

}