package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.service.WorkHelper
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
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

    private val message: MutableLiveData<String> = MutableLiveData()

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        userRepository.register(firstName, lastName, email, password).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    message.postValue(result.data!!)
                    emailVerification(firstName, lastName, email)
                }

                is Resource.Error -> {
                    message.postValue(
                        mApplication.getString(R.string.REGISTRATION_FAILED) + result.message!!
                    )
                }
                is Resource.Loading -> {

                }
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun emailVerification(
        firstName: String,
        lastName: String,
        email: String
    ) {
        userRepository.emailVerification(firstName, lastName, email).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    message.postValue(result.data!!)
                }
                is Resource.Error -> {
                    message.postValue(result.message!!)
                }
                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getMessage(): LiveData<String>
    {
        return message
    }
}