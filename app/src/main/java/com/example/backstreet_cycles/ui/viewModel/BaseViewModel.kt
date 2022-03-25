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
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.example.backstreet_cycles.domain.utils.PlannerHelper
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

    protected val mApplication = getApplication(applicationContext)
    protected val mContext = applicationContext

}