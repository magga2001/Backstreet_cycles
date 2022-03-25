package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
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
class ChangePasswordViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context
    ): ViewModel() {

    private val mApplication = Contexts.getApplication(applicationContext)
    private val userRepository: UserRepository = UserRepository(mApplication, Firebase.firestore, FirebaseAuth.getInstance())
    private val mutableLiveData: MutableLiveData<FirebaseUser> = userRepository.getMutableLiveData()

    fun updatePassword(password: String, newPassword: String) {
        userRepository.updatePassword(password, newPassword)
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }

    fun getUserDetails() {
        return userRepository.getUserDetails()
    }

}