package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.backstreet_cycles.data.repository.UserRepository
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
class SignUpViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context
): ViewModel(){
    private val mApplication = Contexts.getApplication(applicationContext)
    private val userRepository = UserRepository(mApplication, Firebase.firestore, FirebaseAuth.getInstance())

    fun register(firstName:String, lastName:String, email:String, password:String): FirebaseUser?{
        return userRepository.register(firstName,lastName,email,password)
    }
}