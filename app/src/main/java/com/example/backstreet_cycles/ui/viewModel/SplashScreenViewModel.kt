package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val getDockUseCase: GetDockUseCase,
    private val locationRepository: LocationRepository,
    @ApplicationContext applicationContext: Context
): ViewModel(){

    private val isReadyMutableLiveData = MutableLiveData<Boolean>()
    private val mApplication = getApplication(applicationContext)

    fun loadData()
    {
        getDockUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    Log.i("Splash screen dock", result.data?.size.toString())

                    BackstreetApplication.docks = result.data!!
                    loadTouristAttractions()
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

    private fun loadTouristAttractions()
    {
        locationRepository.loadLocations(application = mApplication)
        isReadyMutableLiveData.postValue(true)
    }


    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

}