package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * View model for Forgot Password Activity responsible for password reset
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
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

    private val resetPassword: MutableLiveData<String> = MutableLiveData()

    /**
     * Acknowledging the need to reset the user's password. Link sent via email
     * Stores the new password entered by the user
     * @param email of the registered user
     */
    fun resetPassword(email: String) {
        userRepository.resetPassword(email).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    resetPassword.value = result.data!!
                }

                is Resource.Error -> {
                    resetPassword.value = result.message!!
                }
                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Getter function for the updated password
     * @return MutableLiveData containing the updated password
     */
    fun getResetPassword(): MutableLiveData<String> {
        return resetPassword
    }
}