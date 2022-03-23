package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LogInRegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val mutableLiveData: MutableLiveData<FirebaseUser>

    init {
        userRepository = UserRepository(application, Firebase.firestore, FirebaseAuth.getInstance())
        mutableLiveData = userRepository.getMutableLiveData()
    }

    fun register(firstName:String, lastName:String, email:String, password:String): FirebaseUser?{
        return userRepository.register(firstName,lastName,email,password)
    }


    fun  login(email: String, password: String){
        userRepository.login(email,password)
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }

}