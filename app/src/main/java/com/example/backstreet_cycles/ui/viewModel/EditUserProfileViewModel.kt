package com.example.backstreet_cycles.ui.viewModel;

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
 * View model for Edit User Profile Activity responsible for profile update
 */
@HiltViewModel
class EditUserProfileViewModel @Inject constructor(
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

    private val updatedProfile: MutableLiveData<String> = MutableLiveData()

    /**
     * Updating the user's first and last name in the database
     * @param firstName records the updated first name
     * @param lastName records the updated last name
     */
    fun updateUserDetails(firstName: String, lastName: String) {
        userRepository.updateUserDetails(firstName, lastName).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    updatedProfile.value = result.data!!
                }

                is Resource.Error -> {
                    updatedProfile.value = result.message!!
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * Getter function for the updated profile of the user
     * @return MutableLiveData containing the updated profile
     */
    fun getUpdatedProfile(): MutableLiveData<String> {
        return updatedProfile
    }
}
