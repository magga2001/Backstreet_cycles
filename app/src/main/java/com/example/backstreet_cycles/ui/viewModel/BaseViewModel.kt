package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    protected val getDockUseCase: GetDockUseCase,
    protected val getMapboxUseCase: GetMapboxUseCase,
    protected val locationRepository: LocationRepository,
    @ApplicationContext applicationContext: Context
): ViewModel(){

    protected val mApplication: Application = getApplication(applicationContext)
    protected val mContext = applicationContext
    protected val userRepository: UserRepositoryImpl = UserRepositoryImpl(mApplication, Firebase.firestore, FirebaseAuth.getInstance())

    //FIREBASE

    open fun getUserDetails() {
        return userRepository.getUserDetails()
    }


    //MAPBOX

    open fun clearView()
    {
        BackstreetApplication.wayPoints.clear()
        BackstreetApplication.currentRoute.clear()
    }

    open fun clearInfo()
    {
        BackstreetApplication.distances.clear()
        BackstreetApplication.durations.clear()
    }

    open fun clearDuplication(points: MutableList<Point>)
    {
        BackstreetApplication.location.distinct()
        points.distinct()
    }

    open fun customiseRouteOptions(context: Context, points: List<Point>, criteria: String): RouteOptions
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

    open fun getRoute()
    {
        clearView()
    }

    open fun getDock(){

        getDockUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("New dock", result.data?.size.toString())
                    BackstreetApplication.docks = result.data!!
                    getRoute()
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
}