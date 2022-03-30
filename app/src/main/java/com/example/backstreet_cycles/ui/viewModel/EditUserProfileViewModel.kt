package com.example.backstreet_cycles.ui.viewModel;

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class EditUserProfileViewModel @Inject constructor(
        tflRepository: TflRepository,
        mapboxRepository: MapboxRepository,
        cyclistRepository: CyclistRepository,
        userRepository: UserRepository,
        @ApplicationContext applicationContext: Context
) : BaseViewModel(tflRepository, mapboxRepository, cyclistRepository, userRepository, applicationContext){

        private val updatedProfileMutableLiveData: MutableLiveData<Boolean> = userRepository.getUpdatedProfileMutableLiveData()
        private val userDetailsMutableLiveData: MutableLiveData<Users> = userRepository.getUserDetailsMutableLiveData()

        fun updateUserDetails(firstName: String, lastName: String) {
                EspressoIdlingResource.increment()
                userRepository.updateUserDetails(firstName, lastName)
                EspressoIdlingResource.decrement()
        }

        fun getUpdatedProfileMutableLiveData(): MutableLiveData<Boolean> {
                return updatedProfileMutableLiveData
        }

        fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
                return userDetailsMutableLiveData
        }
}
