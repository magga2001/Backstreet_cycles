package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.PlannerUseCase
import com.example.backstreet_cycles.interfaces.Planner
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import dagger.hilt.android.internal.Contexts
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

@HiltViewModel
class JourneyViewModel @Inject constructor(
    private val getDockUseCase: GetDockUseCase,
    @ApplicationContext applicationContext: Context
): ViewModel(), Planner{

    private val isReadyMutableLiveData: MutableLiveData<Boolean>
    private val distanceMutableLiveData: MutableLiveData<String>
    private val durationMutableLiveData: MutableLiveData<String>
    private val priceMutableLiveData: MutableLiveData<String>
    private var sharedPref: SharedPreferences
    private val firestore = Firebase.firestore
    private val mApplication = getApplication(applicationContext)

    init {
        isReadyMutableLiveData = MutableLiveData()
        isReadyMutableLiveData.value = false
        distanceMutableLiveData = MutableLiveData()
        durationMutableLiveData = MutableLiveData()
        priceMutableLiveData = MutableLiveData()
        sharedPref = applicationContext.getSharedPreferences(R.string.preference_file_Locations.toString(), Context.MODE_PRIVATE)
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return (if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            MapboxNavigationProvider.create(
                NavigationOptions.Builder(mApplication)
                    .accessToken(mApplication.getString(R.string.mapbox_access_token))
                    .build()
            )
        })
    }

    //NEW CODE

    fun linkingToCycleHire(intent: Intent)
    {

    }

    fun clear()
    {
        MapRepository.wayPoints.clear()
        MapRepository.currentRoute.clear()
    }

    fun clearInfo()
    {
        MapRepository.distances.clear()
        MapRepository.durations.clear()
    }

    fun registerObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          routeProgressObserver: RouteProgressObserver
    )
    {
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
    }

    fun unregisterObservers(mapboxNavigation: MapboxNavigation,
                            routesObserver: RoutesObserver,
                            routeProgressObserver: RouteProgressObserver
    )
    {
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
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

    fun calcBicycleRental(users: Int)
    {
        PlannerUseCase.calcBicycleRental(mApplication, users, plannerInterface = this)
    }

    fun fetchRoute(mapboxNavigation: MapboxNavigation, points: MutableList<Point>, profile: String, info: Boolean)
    {
        //Mapbox api
    }

    override fun onSelectedJourney(
        location: Locations,
        profile: String,
        points: MutableList<Point>
    ) {
        clear()
        //fetchRoute(context = this, mapboxNavigation, points, profile, false)
    }

    override fun onFetchJourney(points: MutableList<Point>) {
        //fetchRoute(context = this, mapboxNavigation, points, "cycling", true)
    }

    fun getPlannerInterface(): Planner
    {
        return this
    }

    fun setRoute()
    {

    }

    fun clearRoute()
    {

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
            putString(com.example.backstreet_cycles.R.string.preference_file_Locations.toString(), json)
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
        val serializedObject: String? = sharedPref.getString(R.string.preference_file_Locations.toString(), null)
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

            val user =  firestore
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
                            firestore.collection("users")
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