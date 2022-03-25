package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LogInRegisterViewModel @Inject constructor(
    getDockUseCase: GetDockUseCase,
    getMapboxUseCase: GetMapboxUseCase, locationRepository: LocationRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(getDockUseCase, getMapboxUseCase, locationRepository, applicationContext) {

    private val userRepository: UserRepositoryImpl = UserRepositoryImpl(mApplication, Firebase.firestore, FirebaseAuth.getInstance())
    private val mutableLiveData: MutableLiveData<FirebaseUser> = userRepository.getMutableLiveData()

    fun register(firstName:String, lastName:String, email:String, password:String): FirebaseUser?{
        return userRepository.register(firstName,lastName,email,password)
    }


    fun  login(email: String, password: String){
        userRepository.login(email,password)
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }

    fun resetPassword(email: String) {
        userRepository.resetPassword(email)
    }

}