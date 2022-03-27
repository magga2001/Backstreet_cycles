package com.example.backstreet_cycles.ui.viewModel;

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class EditUserProfileViewModel @Inject constructor(
        getDockUseCase: GetDockUseCase,
        cyclistRepository: CyclistRepository,
        userRepository: UserRepository,
        @ApplicationContext applicationContext: Context
) : BaseViewModel(getDockUseCase, cyclistRepository, userRepository, applicationContext){

        private val updatedProfileMutableLiveData: MutableLiveData<Boolean> = userRepository.getUpdatedProfileMutableLiveData()
        private val userDetailsMutableLiveData: MutableLiveData<Users> = userRepository.getUserDetailsMutableLiveData()

        fun updateUserDetails(firstName: String, lastName: String) {
                userRepository.updateUserDetails(firstName, lastName)
        }

        fun getUpdatedProfileMutableLiveData(): MutableLiveData<Boolean> {
                return updatedProfileMutableLiveData
        }

        fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
                return userDetailsMutableLiveData
        }
}
