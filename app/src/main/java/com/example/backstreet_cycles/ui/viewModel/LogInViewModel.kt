package com.example.backstreet_cycles.ui.viewModel;

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
class LogInViewModel @Inject constructor(
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

    private val firebaseUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val errorMessage: MutableLiveData<String> = MutableLiveData()

    suspend fun login(email: String, password: String) {

        userRepository.login(email, password).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    firebaseUser.postValue(result.data!!)
                    WorkHelper.setPeriodicallySendingLogs(mApplication)
                }

                is Resource.Error -> {
                    errorMessage.postValue(
                        mApplication.getString(R.string.LOG_IN_FAILED) + ". " + result.message!!
                    )
                }
                is Resource.Loading -> {

                }
            }
        }.launchIn(viewModelScope)
    }

    fun getFirebaseUser(): MutableLiveData<FirebaseUser> {
        return firebaseUser
    }

    fun getErrorMessage(): MutableLiveData<String> {
        return errorMessage
    }
}
