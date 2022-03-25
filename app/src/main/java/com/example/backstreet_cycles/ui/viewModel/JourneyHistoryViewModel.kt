package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.example.backstreet_cycles.domain.utils.JsonHelper
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
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
    getMapboxUseCase: GetMapboxUseCase, locationRepository: LocationRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(getDockUseCase, getMapboxUseCase, locationRepository, applicationContext)  {

    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var showAlert: MutableLiveData<Boolean> = MutableLiveData(false)
    private var stops: MutableList<Locations> = mutableListOf()
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

    override fun getRoute()
    {
        super.getRoute()
        BackstreetApplication.location.addAll(stops)
        checkCurrentJourney()
    }

    private fun getMapBox(mapboxNavigation: MapboxNavigation,routeOptions: RouteOptions,info :Boolean)
    {
        getMapboxUseCase(mapboxNavigation,routeOptions,info).onEach {

            Log.i("current Route succeed:", it.toString())

            isReadyMutableLiveData.postValue(true)

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
            BackstreetApplication.wayPoints.addAll(points)

            routeOptions = when(profile)
            {
                MapboxConstants.WALKING -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_WALKING)
                else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
            }

            getMapBox(mapboxNavigation, routeOptions, info)

        }else
        {
            routeOptions = customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)

            getMapBox(mapboxNavigation, routeOptions, info)

        }
    }

    private fun checkCurrentJourney()
    {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney){
            showAlert.postValue(true)
        } else{
            val locationPoints = PlannerHelper.setPoints(BackstreetApplication.location)
            fetchRoute(mContext, locationPoints, MapboxConstants.CYCLING, false)
        }
    }

    fun continueWithCurrentJourney(){
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
        BackstreetApplication.location = listOfLocations
        val listPoints = PlannerHelper.setPoints(listOfLocations)
        fetchRoute(mContext, listPoints, MapboxConstants.CYCLING, false)
    }

    fun continueWithNewJourney(newStops: MutableList<Locations>){
        val listPoints = PlannerHelper.setPoints(newStops)
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(newStops, Locations::class.java)
        fetchRoute(mContext, listPoints, MapboxConstants.CYCLING, false)
    }

    fun getJourneyHistory(userDetails: Users):MutableList<List<Locations>> {
        val listLocations = emptyList<List<Locations>>().toMutableList()
        for (journey in userDetails.journeyHistory){
            val serializedObject: String = journey
            listLocations.add(JsonHelper.convertJSON(serializedObject))
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

}