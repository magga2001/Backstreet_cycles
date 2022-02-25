package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.model.AppRepository
import com.google.firebase.auth.FirebaseUser

class LogInRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository: AppRepository
    private val mutableLiveData: MutableLiveData<FirebaseUser>



    init {
        appRepository = AppRepository(application)
        mutableLiveData = appRepository.getMutableLiveData()
    }

    fun register(firstName:String, lastName:String, email:String, password:String){
        appRepository.register(firstName,lastName,email,password)
    }


    fun  login(email: String, password: String){
        appRepository.login(email,password)
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }


}