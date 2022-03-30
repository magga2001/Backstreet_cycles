package com.example.backstreet_cycles.domain.repositoryInt

import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.domain.model.dto.Users
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job

interface UserRepository {

    fun register(fName: String, lName: String, email: String, password: String): FirebaseUser?

    fun updateUserDetails(firstName: String, lastName: String) : Job

    fun updatePassword(password: String, newPassword: String)

    fun getUserDetails()

    fun login(email: String, password: String) : FirebaseUser?

    fun resetPassword(email: String)

    fun logout()

    fun getMutableLiveData(): MutableLiveData<FirebaseUser>

    fun getLoggedOutMutableLiveData(): MutableLiveData<Boolean>

    fun getUpdatedProfileMutableLiveData(): MutableLiveData<Boolean>

    fun getUserDetailsMutableLiveData(): MutableLiveData<Users>

    fun getFirebaseAuthUser(): FirebaseUser?
}