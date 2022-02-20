package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.UserDto
import com.example.backstreet_cycles.model.AppRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job

class LoggedInViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository: AppRepository
    private val mutableLiveData: MutableLiveData<FirebaseUser>
    private val loggedOutMutableiveData: MutableLiveData<Boolean>
    private val updatedProfileMutableLiveData: MutableLiveData<Boolean>
    private val userDetailsMutableLiveData: MutableLiveData<UserDto>


    init {
        appRepository = AppRepository(application)
        mutableLiveData = appRepository.getMutableLiveData()
        loggedOutMutableiveData = appRepository.getLoggedOutMutableLiveData()
        updatedProfileMutableLiveData = appRepository.getUpdatedProfileMutableLiveData()
        userDetailsMutableLiveData = appRepository.getUserDetailsMutableLiveData()
    }

    fun logOut() {
        appRepository.logout()
    }

    fun updateUserDetails(firstName: String, lastName: String) {
        appRepository.updateUserDetails(firstName, lastName)
    }

    fun getUserDetails(): Job {
        return appRepository.getUserDetails()
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }

    fun getUpdatedProfileMutableLiveData(): MutableLiveData<Boolean> {
        return updatedProfileMutableLiveData
    }

    fun getLoggedOutMutableLiveData(): MutableLiveData<Boolean> {
        return loggedOutMutableiveData
    }

    fun getUserDetailsMutableLiveData(): MutableLiveData<UserDto> {
        return userDetailsMutableLiveData
    }
}