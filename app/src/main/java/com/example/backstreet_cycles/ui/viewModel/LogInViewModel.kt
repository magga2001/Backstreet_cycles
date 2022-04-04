package com.example.backstreet_cycles.ui.viewModel;

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
import com.example.backstreet_cycles.service.WorkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * View model for Log In Activity responsible for log in the user
 */
@HiltViewModel
class LogInViewModel @Inject constructor(
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

    private val firebaseUserData: MutableLiveData<Boolean> = MutableLiveData()
    private val errorMessageData: MutableLiveData<String> = MutableLiveData()

    /**
     * Validating data entered for log in
     * @param email entered by the user
     * @param password entered by the user
     */
    fun login(email: String, password: String) {
        userRepository.login(email, password).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    firebaseUserData.value = result.data!!
                    WorkHelper.setPeriodicallySendingLogs(mApplication)
                }

                is Resource.Error -> {
                    errorMessageData.value = mApplication.getString(R.string.LOG_IN_FAILED) + result.message!!
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Getter function for checking the status of validation
     * @return MutableLiveData in the form of a boolean to confirm status
     */
    fun getFirebaseUserData(): MutableLiveData<Boolean> {
        return firebaseUserData
    }

    /**
     * Getter function for the error message
     * @return MutableLiveData containing the error message
     */
    fun getErrorMessageData(): MutableLiveData<String> {
        return errorMessageData
    }
}
