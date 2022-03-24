package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.backstreet_cycles.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LogInRegisterViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context
): ViewModel() {

    private val mApplication = Contexts.getApplication(applicationContext)
    private val mContext = applicationContext
    private val userRepository: UserRepository
    private val mutableLiveData: MutableLiveData<FirebaseUser>

    init {
        userRepository = UserRepository(mApplication, Firebase.firestore, FirebaseAuth.getInstance())
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