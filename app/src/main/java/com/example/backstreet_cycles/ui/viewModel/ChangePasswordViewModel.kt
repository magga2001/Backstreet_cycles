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

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
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

    private val updatePassword: MutableLiveData<String> = MutableLiveData()

    /**
     * Updating the user's password in the database
     * @param password records current password
     * @param newPassword records the updated password
     */
    fun updatePassword(password: String, newPassword: String) {
        userRepository.updatePassword(password, newPassword).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    updatePassword.value = result.data!!
                }

                is Resource.Error -> {
                    updatePassword.value = result.message!!
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
    fun getUpdateDetail(): MutableLiveData<String> {
        return updatePassword
    }
}