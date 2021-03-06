package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.repositoryInt.*
import com.example.backstreet_cycles.domain.utils.JsonHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * View model for Splash Screen Activity responsible for fethcing application data from API
 */
@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    private val locationRepository: LocationRepository,
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

    private val isReady = MutableLiveData<Boolean>()

    /**
     * Retrieving necessary data for the application
     */
    suspend fun loadData() {
        tflRepository.getDocks().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data != null && result.data.isNotEmpty()) {
                        tflRepository.setCurrentDocks(result.data)
                        val dockJSON = JsonHelper.objectToString(result.data, Dock::class.java)
                        JsonHelper.writeJsonFile(mContext, mContext.getString(R.string.json_local_docks), dockJSON)
                    }
                    loadTouristAttractions()
                }

                is Resource.Error -> {
                    val docksJson = JsonHelper.readJsonFile(mContext, mContext.getString(R.string.json_local_docks)).toString()
                    val docks = JsonHelper.stringToObject(docksJson, Dock::class.java)
                    tflRepository.setCurrentDocks(docks!!.toMutableList())
                    isReady.value = true
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Setting up tourist attraction data for the map
     */
    private fun loadTouristAttractions() {
        locationRepository.loadLocations(application = mApplication)
        isReady.value = true
    }

    /**
     * Getter function to check status of data set up
     * @return MutableLiveData in the form of a boolean to confirm status
     */
    fun getIsReady(): MutableLiveData<Boolean> {
        return isReady
    }

}