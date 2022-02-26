package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.UserDto
import com.example.backstreet_cycles.model.AppRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job

class LoggedInViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository: AppRepository
    private val mutableLiveData: MutableLiveData<FirebaseUser>
    private val loggedOutMutableiveData: MutableLiveData<Boolean>
    private val updatedProfileMutableLiveData: MutableLiveData<Boolean>
    private val userDetailsMutableLiveData: MutableLiveData<UserDto>


    init {
//        mutableLiveData = appRepository.getMutableLiveData()
//        loggedOutMutableiveData = appRepository.getLoggedOutMutableLiveData()
//        updatedProfileMutableLiveData = appRepository.getUpdatedProfileMutableLiveData()
//        userDetailsMutableLiveData = appRepository.getUserDetailsMutableLiveData()
        mutableLiveData = MutableLiveData()
        loggedOutMutableiveData = MutableLiveData()
        updatedProfileMutableLiveData = MutableLiveData()
        userDetailsMutableLiveData = MutableLiveData()
        appRepository = AppRepository(application, Firebase.firestore, FirebaseAuth.getInstance(), mutableLiveData)
    }

    fun logOut() {
        appRepository.logout()
    }

    fun updateUserDetails(firstName: String, lastName: String) {
        appRepository.updateUserDetails(firstName, lastName)
    }

    fun updateEmailAndPassword(email: String, password: String, newPassword: String) {
        appRepository.updateEmailAndPassword(email, password, newPassword)
    }

    fun getUserDetails() {
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