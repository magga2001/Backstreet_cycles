package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.model.TflRepository
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val tflRepository: TflRepository
    private val dockMutableLiveData : MutableLiveData<Dock>
    private val isReadyMutableLiveData: MutableLiveData<Boolean>
//    private val result = dockMutableLiveData.switchMap { dockName ->
//            liveData{
//                emit(dockName)
//            }
//        }

    init{
        tflRepository = TflRepository(application)
        dockMutableLiveData = tflRepository.getDockMutableLiveData()
        isReadyMutableLiveData = tflRepository.getIsReadyMutableLiveData()

    }

    suspend fun readADock(dockName: String): Dock
    {

        lateinit var dock: Dock

        viewModelScope.launch {
            dock = tflRepository.readADock(dockName)!!
        }.join()

        return dock
//        return liveData {
//            emit(tflRepository.readADock(dockName))
//        }

//        return dockMutableLiveData.switchMap { id ->
//            liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
//                emit(tflRepository.readADock(dockName))
//            }
//        }
    }

    fun loadDock()
    {
        tflRepository.loadDock()
    }

    fun getDockMutableLiveData(): LiveData<Dock>
    {
        return dockMutableLiveData
    }

    fun getIsReadyMutableLiveData(): LiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

}