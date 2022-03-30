package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
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
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    tflRepository: TflRepository,
    mapboxRepository: MapboxRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(
    tflRepository,
    mapboxRepository,
    cyclistRepository,
    userRepository,
    applicationContext
) {

    private val updatePassword : MutableLiveData<String> = MutableLiveData()

    fun updatePassword(password: String, newPassword: String) {
        userRepository.updatePassword(password, newPassword).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    updatePassword.postValue(result.data!!)
                }

                is Resource.Error -> {
                    updatePassword.postValue(
                        "Update password " + result.message!!
                    )
                }
                is Resource.Loading -> {

                }
            }
        }.launchIn(viewModelScope)
    }

    fun getUpdateDetail(): MutableLiveData<String>
    {
        return updatePassword
    }
}