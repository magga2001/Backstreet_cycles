package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.backstreet_cycles.R
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
 * View model for Sign Up Activity responsible for creating sign up page
 */
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

    /**
     * Validating the data entered by the user for setting up an account
     * @param firstName of the user
     * @param lastName of the user
     * @param email of the user
     * @param password of the user
     */
    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        userRepository.register(firstName, lastName, email, password).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    getMessage().value = result.data!!
                    emailVerification(firstName, lastName, email)
                }

                is Resource.Error -> {
                    message.value = mApplication.getString(R.string.REGISTRATION_FAILED) + result.message!!
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Checking whether the email is verified
     * @param firstName of the user
     * @param lastName of the user
     * @param email of the user
     */
    private fun emailVerification(
        firstName: String,
        lastName: String,
        email: String
    ) {
        userRepository.emailVerification(firstName, lastName, email).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    message.value = result.data!!
                }

                is Resource.Error -> {
                    message.value = result.message!!
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Getter function for the message
     * @return MutableLiveData containing the message
     */
    fun getMessage(): MutableLiveData<String> {
        return message
    }
}