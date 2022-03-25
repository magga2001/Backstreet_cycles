package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.*
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.interfaces.Planner
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class JourneyViewModel @Inject constructor(getDockUseCase: GetDockUseCase,
                                           getMapboxUseCase: GetMapboxUseCase, locationRepository: LocationRepository,
                                           @ApplicationContext applicationContext: Context
) : BaseViewModel(getDockUseCase, getMapboxUseCase, locationRepository, applicationContext), Planner{

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

    private var status: String = "UPDATE"
    private var numUser: Int = 1
    private val isReadyMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val distanceMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val durationMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val priceMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private var sharedPref: SharedPreferences =
        applicationContext.getSharedPreferences("LOCATIONS", Context.MODE_PRIVATE)
    private val fireStore = Firebase.firestore
//    private val mApplication = getApplication(applicationContext)
//    private val mContext = applicationContext

    fun getDock()
    {
        getDockUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("New dock", result.data?.size.toString())

                    val points = PlannerHelper.setPoints(MapRepository.location)

                    status = "REFRESH"
                    fetchRoute(context = mContext, mapboxNavigation, points, MapboxConstants.CYCLING, false)
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

    fun clearView()
    {
        MapRepository.wayPoints.clear()
        MapRepository.currentRoute.clear()
    }

    fun clearInfo()
    {
        MapRepository.distances.clear()
        MapRepository.durations.clear()
    }

    fun clearCurrentSession()
    {
        MapRepository.location.clear()
    }

    fun registerObservers(
                          routesObserver: RoutesObserver,
                          routeProgressObserver: RouteProgressObserver
    )
    {
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
    }

    fun unregisterObservers(
                            routesObserver: RoutesObserver,
                            routeProgressObserver: RouteProgressObserver
    )
    {
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
    }

    fun calcBicycleRental(users: Int)
    {
        PlannerUseCase.calcBicycleRental(mApplication, users, plannerInterface = this)
    }

    fun getJourneyOverview()
    {
        val points = PlannerHelper.setPoints(MapRepository.location)
        getRoute(mContext, mapboxNavigation, points, MapboxConstants.CYCLING, false)
    }

    fun updateMapMarkerAnnotation(annotationApi: AnnotationPlugin)
    {
        MapAnnotationUseCase.removeAnnotations()
        MapAnnotationUseCase.addAnnotationToMap(context = mContext, annotationApi)
    }

    private fun getRoute(context: Context,
                         mapboxNavigation: MapboxNavigation,
                         points: MutableList<Point>,
                         profile: String,
                         info: Boolean)
    {
        status = "UPDATE"
        fetchRoute(context = context, mapboxNavigation, points, profile, info)
    }

    private fun fetchRoute(context: Context,
                   mapboxNavigation: MapboxNavigation,
                   points: MutableList<Point>,
                   profile: String,
                   info: Boolean)
    {

        MapRepository.location.distinct()
        points.distinct()

        val routeOptions: RouteOptions

        if(!info) {
            clearInfo()
            MapRepository.wayPoints.addAll(points)

            routeOptions = when(profile) {
                "walking" -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_WALKING)
                else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
            }

            getMapboxUseCase(mapboxNavigation,routeOptions,info).onEach {

                isReadyMutableLiveData.postValue(status)

            }.launchIn(viewModelScope)

        }else {

            routeOptions =customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)

            getMapboxUseCase(mapboxNavigation,routeOptions,info).onEach {

                isReadyMutableLiveData.postValue(status)

                distanceMutableLiveData.postValue((MapRepository.distances.sum()/1000).roundToInt().toString())
                durationMutableLiveData.postValue((MapRepository.durations.sum()/60).roundToInt().toString())

                displayPrice()

            }.launchIn(viewModelScope)
        }
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

    override fun onSelectedJourney(
        location: Locations,
        profile: String,
        points: MutableList<Point>
    ) {
        clearView()
        getRoute(context = mContext, mapboxNavigation, points, profile, false)
    }

    override fun onFetchJourney(points: MutableList<Point>) {
        getRoute(context = mContext, mapboxNavigation, points, MapboxConstants.CYCLING, true)
    }

    fun getPlannerInterface(): Planner
    {
        return this
    }

    fun setRoute()
    {
        mapboxNavigation.setRoutes(MapRepository.currentRoute)
    }

    fun clearRoute()
    {
        mapboxNavigation.setRoutes(listOf())
    }

    fun setNumUser(numUser: Int)
    {
        this.numUser = numUser
    }

    fun getNumUser(): Int
    {
        return this.numUser
    }

    fun finishJourney(userDetails: Users)
    {
        SharedPrefHelper.initialiseSharedPref(mApplication, Constants.LOCATIONS)
        addJourneyToJourneyHistory(SharedPrefHelper.getSharedPref(Locations::class.java), userDetails)
        SharedPrefHelper.clearListLocations()
    }

    private fun displayPrice()
    {
        val price = MapInfoUseCase.getRental()

        priceMutableLiveData.postValue((price * numUser).toString())

    }

    fun getIsReadyMutableLiveData(): MutableLiveData<String>
    {
        return isReadyMutableLiveData
    }

    fun getDistanceMutableLiveData(): MutableLiveData<String>
    {
        return distanceMutableLiveData
    }

    fun getDurationMutableLiveData(): MutableLiveData<String>
    {
        return durationMutableLiveData
    }

    fun getPriceMutableLiveData(): MutableLiveData<String>
    {
        return priceMutableLiveData
    }

//--------------------------------
    //Tish stuff

    fun addLocationSharedPreferences(locations: MutableList<Locations>):Boolean {
        if (getListLocations().isEmpty()){
            overrideListLocation(locations)
            return false
        }
        return true
    }

    fun overrideListLocation(locations: MutableList<Locations>) {
        val gson = Gson();
        val json = gson.toJson(locations);
        with (sharedPref.edit()) {
            putString("LOCATIONS", json)
            apply()
        }
    }

    fun clearListLocations() {
        with (sharedPref.edit()) {
            clear()
            apply()
        }
    }

    fun getListLocations(): List<Locations> {
        val locations: List<Locations>
        val serializedObject: String? = sharedPref.getString("LOCATIONS", null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<Locations?>?>() {}.getType()
            locations = gson.fromJson<List<Locations>>(serializedObject, type)
        }else {
            locations = emptyList()
        }
        return locations
    }

    fun addJourneyToJourneyHistory(locations: MutableList<Locations>, userDetails: Users) =
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
                                //Toast.makeText(application,e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

    fun convertJSON(serializedObject: String): List<Locations> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Locations?>?>() {}.getType()
        return gson.fromJson(serializedObject, type)
    }

    fun getJourneyHistory(userDetails: Users):MutableList<List<Locations>> {
        val listLocations = emptyList<List<Locations>>().toMutableList()
        for (journey in userDetails.journeyHistory){
            val serializedObject: String = journey
            listLocations.add(convertJSON(serializedObject))
        }
        return listLocations
    }
}