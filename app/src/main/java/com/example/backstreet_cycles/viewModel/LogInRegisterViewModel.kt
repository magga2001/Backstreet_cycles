package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.model.AppRepository
import com.google.firebase.auth.FirebaseUser

class LogInRegisterViewModel : AndroidViewModel {

    private val appRepository: AppRepository
    private val mutableLiveData: MutableLiveData<FirebaseUser>


    constructor(application: Application) : super(application) {

        appRepository = AppRepository(application)
        mutableLiveData = appRepository.getMutableLiveData()
    }

    public fun register(firstName:String, lastName:String, email:String, password:String){
        appRepository.register(firstName,lastName,email,password)
    }

    public fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }
}