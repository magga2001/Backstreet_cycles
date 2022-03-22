package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoggedInViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository = UserRepository(application, Firebase.firestore, FirebaseAuth.getInstance())
    private val mutableLiveData: MutableLiveData<FirebaseUser> = userRepository.getMutableLiveData()
    private val loggedOutMutableLiveData: MutableLiveData<Boolean> = userRepository.getLoggedOutMutableLiveData()
    private val updatedProfileMutableLiveData: MutableLiveData<Boolean> = userRepository.getUpdatedProfileMutableLiveData()
    private val userDetailsMutableLiveData: MutableLiveData<Users> = userRepository.getUserDetailsMutableLiveData()

    fun logOut() {
        userRepository.logout()
    }

    fun updateUserDetails(firstName: String, lastName: String) {
        userRepository.updateUserDetails(firstName, lastName)
    }

    fun updateEmailAndPassword(password: String, newPassword: String) {
        userRepository.updateEmailAndPassword(password, newPassword)
    }

    fun getUserDetails() {
        return userRepository.getUserDetails()
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