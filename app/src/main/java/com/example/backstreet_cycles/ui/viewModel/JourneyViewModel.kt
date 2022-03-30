package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.useCase.MapAnnotationUseCase
import com.example.backstreet_cycles.domain.useCase.MapInfoUseCase
import com.example.backstreet_cycles.domain.useCase.PlannerUseCase
import com.example.backstreet_cycles.domain.utils.JourneyState
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class JourneyViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(tflRepository, mapboxRepository, cyclistRepository, userRepository,applicationContext), Planner{

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
    private var status: String = "UPDATE"
    private val isReadyMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val distanceMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val durationMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val priceMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val fireStore = Firebase.firestore
    private val userDetailsMutableLiveData: MutableLiveData<Users> = userRepository.getUserDetailsMutableLiveData()

    override suspend fun getDock() {
        tflRepository.getDocks().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("New dock", result.data?.size.toString())

                    if(result.data != null && result.data.isNotEmpty()){
                        tflRepository.setCurrentDocks(result.data)
                    }
                    status = "REFRESH"
                    fetchRoute(context = mContext, getJourneyLocations(), MapboxConstants.CYCLING, false)
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

    fun clearCurrentSession() {
        mapboxRepository.clearJourneyLocations()
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
        PlannerUseCase.calcBicycleRental(
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
        MapAnnotationUseCase.removeAnnotations()
        MapAnnotationUseCase.addAnnotationToMap(context = mContext, getJourneyWayPointsLocations(), annotationApi, journeyState)
    }

    private fun getRoute(context: Context,
                         locations: MutableList<Locations>,
                         profile: String,
                         info: Boolean) {
        status = "UPDATE"
        fetchRoute(context = context, locations, profile, info)
    }

    private fun fetchRoute(context: Context,
                   locations: MutableList<Locations>,
                   profile: String,
                   info: Boolean) {

        clearDuplication(locations)
        val routeOptions: RouteOptions
        val points = locations.map { PlannerHelper.convertLocationToPoint(it) }

        if(!info) {
            clearInfo()
            setCurrentWayPoint(locations)
            routeOptions = setCustomiseRoute(context, points, profile)
            updateMapRouteLine(routeOptions, info)

        }else {

            routeOptions = setOverviewRoute(context, points)
            updateMapInfo(routeOptions,info)

        }
    }

    private fun updateMapRouteLine(routeOptions: RouteOptions, info: Boolean) {
        mapboxRepository.requestRoute(mapboxNavigation,routeOptions,info).onEach {
            isReadyMutableLiveData.postValue(status)
        }.launchIn(viewModelScope)
    }

    private fun updateMapInfo(routeOptions: RouteOptions, info: Boolean){
        mapboxRepository.requestRoute(mapboxNavigation,routeOptions,info).onEach {
            isReadyMutableLiveData.postValue(status)
            calcJourneyInfo()
        }.launchIn(viewModelScope)
    }

    private fun calcJourneyInfo() {
        distanceMutableLiveData.postValue(PlannerHelper.convertMToKm(mapboxRepository.getJourneyDistances()).toString())
        durationMutableLiveData.postValue(PlannerHelper.convertMsToS(mapboxRepository.getJourneyDurations()).toString())
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

    fun getPlannerInterface(): Planner
    {
        return this
    }

    fun setRoute()
    {
        mapboxNavigation.setRoutes(mapboxRepository.getJourneyCurrentRoute())
    }

    fun clearRoute()
    {
        mapboxNavigation.setRoutes(listOf())
    }

    fun finishJourney(userDetails: Users)
    {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        addJourneyToJourneyHistory(SharedPrefHelper.getSharedPref(Locations::class.java), userDetails)
        SharedPrefHelper.clearListLocations()
    }

    private fun addJourneyToJourneyHistory(locations: MutableList<Locations>, userDetails: Users) =
        CoroutineScope(Dispatchers.IO).launch {

            val user =  fireStore
                .collection("users")
                .whereEqualTo("email", userDetails.email)
                .get()
                .await()
            val gson = Gson()
            val jsonObject = gson.toJson(locations)
            if (jsonObject.isNotEmpty()){
                userDetails.journeyHistory.add(jsonObject)
                if (user.documents.isNotEmpty()) {
                    for (document in user) {

                        try {
                            fireStore.collection("users")
                                .document(document.id)
                                .update("journeyHistory",userDetails.journeyHistory)
                        }
                        catch (e: Exception) {
                            withContext(Dispatchers.Main) {
//                                ToastMessageHelper.createToastMessage(mApplication,e.message)
                            }
                        }
                    }
                }
            }
        }

    private fun displayPrice() {
        val price = MapInfoUseCase.getRental(getJourneyDurations())
        priceMutableLiveData.postValue((price * getNumCyclists()).toString())
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<String> {
        return isReadyMutableLiveData
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

    fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
        return userDetailsMutableLiveData
    }
}