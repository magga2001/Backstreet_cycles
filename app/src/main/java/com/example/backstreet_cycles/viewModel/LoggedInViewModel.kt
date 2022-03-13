package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.dto.Users
import com.example.backstreet_cycles.model.AppRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoggedInViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository: AppRepository =
        AppRepository(application, Firebase.firestore, FirebaseAuth.getInstance())
    private val mutableLiveData: MutableLiveData<FirebaseUser> = appRepository.getMutableLiveData()
    private val loggedOutMutableLiveData: MutableLiveData<Boolean> = appRepository.getLoggedOutMutableLiveData()
    private val updatedProfileMutableLiveData: MutableLiveData<Boolean> = appRepository.getUpdatedProfileMutableLiveData()
    private val userDetailsMutableLiveData: MutableLiveData<Users> = appRepository.getUserDetailsMutableLiveData()

    fun logOut() {
        appRepository.logout()
    }

    fun updateUserDetails(firstName: String, lastName: String) {
        appRepository.updateUserDetails(firstName, lastName)
    }

    fun updateEmailAndPassword(password: String, newPassword: String) {
        appRepository.updateEmailAndPassword(password, newPassword)
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
        return loggedOutMutableLiveData
    }

    fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
        return userDetailsMutableLiveData
    }
}