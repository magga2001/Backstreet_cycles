package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.repositoryInt.*
import com.example.backstreet_cycles.domain.utils.JsonHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    application: Application,
    private val locationRepository: LocationRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(
    tflRepository,
    mapboxRepository,
    cyclistRepository,
    userRepository,
    application,
    applicationContext
) {

    private val isReadyMutableLiveData = MutableLiveData<Boolean>()

    suspend fun loadData() {
        tflRepository.getDocks().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("Splash screen dock", result.data?.size.toString())

                    if (result.data != null && result.data.isNotEmpty()) {
                        tflRepository.setCurrentDocks(result.data)
                        val dockJSON = JsonHelper.objectToString(result.data, Dock::class.java)
                        JsonHelper.writeJsonFile(mContext, "localdocks.json", dockJSON)
                        Log.i("Splash screen dock", JsonHelper.readJsonFile(mContext, "localdocks.json").toString())
                    }
                    loadTouristAttractions()
                }

                is Resource.Error -> {
                    Log.i("New dock", result.toString())

                }
                is Resource.Loading -> {
                    Log.i("New dock", "Loading...")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadTouristAttractions() {
        locationRepository.loadLocations(application = mApplication)
        isReadyMutableLiveData.value = true
    }


    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean> {
        return isReadyMutableLiveData
    }

}