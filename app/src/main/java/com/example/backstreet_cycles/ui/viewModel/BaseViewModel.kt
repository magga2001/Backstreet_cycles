package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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