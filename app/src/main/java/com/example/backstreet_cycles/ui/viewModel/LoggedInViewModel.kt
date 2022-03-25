package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LoggedInViewModel @Inject constructor(
    getDockUseCase: GetDockUseCase,
    getMapboxUseCase: GetMapboxUseCase, locationRepository: LocationRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(getDockUseCase, getMapboxUseCase, locationRepository, applicationContext) {

    private val userRepository = UserRepositoryImpl(mApplication, Firebase.firestore, FirebaseAuth.getInstance())
    private val loggedOutMutableLiveData: MutableLiveData<Boolean> = userRepository.getLoggedOutMutableLiveData()
    private val userDetailsMutableLiveData: MutableLiveData<Users> = userRepository.getUserDetailsMutableLiveData()

    fun logOut() {
        userRepository.logout()
    }

    fun getUserDetails() {
        return userRepository.getUserDetails()
    }

    fun getLoggedOutMutableLiveData(): MutableLiveData<Boolean> {
        return loggedOutMutableLiveData
    }

    fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
        return userDetailsMutableLiveData
    }
}