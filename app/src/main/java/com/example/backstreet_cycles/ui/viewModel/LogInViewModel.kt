package com.example.backstreet_cycles.ui.viewModel;

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(tflRepository, mapboxRepository ,cyclistRepository, userRepository, applicationContext){

    private val mutableLiveData = userRepository.getMutableLiveData()

    fun  login(email: String, password: String){
         userRepository.login(email,password)
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
         return mutableLiveData
    }
}
