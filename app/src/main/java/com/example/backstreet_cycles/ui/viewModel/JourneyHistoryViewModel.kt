package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.data.repository.JourneyRepository
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.data.repository.UserRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.reflect.Type
import javax.inject.Inject

@HiltViewModel
class JourneyHistoryViewModel @Inject constructor(
    private val getMapboxUseCase: GetMapboxUseCase,
    @ApplicationContext applicationContext: Context
): ViewModel()  {

    private val fireStore = Firebase.firestore
    private val mApplication = Contexts.getApplication(applicationContext)
    private val mContext = applicationContext
    private val mapRepository: JourneyRepository = JourneyRepository(mApplication,fireStore)
    private val userRepository = UserRepository(mApplication, Firebase.firestore, FirebaseAuth.getInstance())
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

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

    fun getShowAlertMutableLiveData(): MutableLiveData<Boolean> {
        return showAlert
    }

    fun setShowAlert(bool: Boolean){
        showAlert.postValue(bool)
    }

    fun continueWithCurrentJourney(){
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val listOfLocations = SharedPrefHelper.getSharedPref(Locations::class.java)
        MapRepository.location = listOfLocations
        val listPoints = PlannerHelper.setPoints(listOfLocations)
        fetchRoute(mContext, listPoints, MapboxConstants.CYCLING, false)
    }

    fun continueWithNewJourney(newStops: MutableList<Locations>){
        val listPoints = PlannerHelper.setPoints(newStops)
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        SharedPrefHelper.overrideSharedPref(newStops, Locations::class.java)
        fetchRoute(mContext, listPoints, MapboxConstants.CYCLING, false)
    }

    fun getUserDetails() {
        return userRepository.getUserDetails()
    }

    fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
        return userDetailsMutableLiveData
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

    fun clearAllStops() {
        stops.clear()
    }

    fun addAllStops(checkpoints: MutableList<Locations>){
        stops.addAll(checkpoints)
    }

    private fun clearView()
    {
        MapRepository.wayPoints.clear()
        MapRepository.currentRoute.clear()
    }

    private fun clearInfo()
    {
        MapRepository.distances.clear()
        MapRepository.durations.clear()
    }

    private fun clearDuplication(points: MutableList<Point>)
    {
        MapRepository.location.distinct()
        points.distinct()
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
            MapRepository.wayPoints.addAll(points)

            routeOptions = when(profile)
            {
                "walking" -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_WALKING)
                else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
            }

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

    private fun checkCurrentJourney()
    {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        val noCurrentJourney = SharedPrefHelper.checkIfSharedPrefEmpty(Constants.LOCATIONS)
        if (!noCurrentJourney){
            showAlert.postValue(true)
        } else{
            val locationPoints = PlannerHelper.setPoints(MapRepository.location)
            fetchRoute(mContext, locationPoints, MapboxConstants.CYCLING, false)
        }
    }

    fun getRoute()
    {
        clearView()
        MapRepository.location.addAll(stops)
        checkCurrentJourney()

    }

    fun getJourneyHistory(userDetails: Users):MutableList<List<Locations>> {
        val listLocations = emptyList<List<Locations>>().toMutableList()
        for (journey in userDetails.journeyHistory){
            val serializedObject: String = journey
            listLocations.add(convertJSON(serializedObject))
        }
        return listLocations
    }

    private fun convertJSON(serializedObject: String): List<Locations> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Locations?>?>() {}.type
        return gson.fromJson(serializedObject, type)
    }
}