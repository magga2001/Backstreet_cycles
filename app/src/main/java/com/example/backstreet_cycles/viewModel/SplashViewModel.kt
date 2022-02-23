package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.backstreet_cycles.dto.Dock
import com.example.backstreet_cycles.model.TflRepository
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val tflRepository: TflRepository
    private val isReadyMutableLiveData: MutableLiveData<Boolean>

    init{
        tflRepository = TflRepository(application)
        isReadyMutableLiveData = tflRepository.getIsReadyMutableLiveData()

    }

    suspend fun readADock(dockName: String): Dock
    {
        lateinit var dock: Dock

        viewModelScope.launch {
            dock = tflRepository.readADock(dockName)!!
        }.join()

        return dock
    }

    fun loadDock()
    {
        tflRepository.loadDock()
    }

    fun getIsReadyMutableLiveData(): LiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

}