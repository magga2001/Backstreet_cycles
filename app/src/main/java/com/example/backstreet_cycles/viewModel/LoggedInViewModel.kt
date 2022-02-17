package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.model.AppRepository
import com.google.firebase.auth.FirebaseUser

class LoggedInViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository: AppRepository
    private val mutableLiveData: MutableLiveData<FirebaseUser>
    private val loggedOutMutableiveData: MutableLiveData<Boolean>


    init {
        appRepository = AppRepository(application)
        mutableLiveData = appRepository.getMutableLiveData()
        loggedOutMutableiveData = appRepository.getLoggedOutMutableLiveData()
    }

    fun logOut() {
        appRepository.logout()
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }

    fun getLoggedOutMutableLiveData(): MutableLiveData<Boolean> {
        return loggedOutMutableiveData
    }
}