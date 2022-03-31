package com.example.backstreet_cycles.domain.repositoryInt

import android.content.ContentValues
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.utils.ToastMessageHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface UserRepository {

    fun register(firstName: String, lastName: String, email: String, password: String): Flow<Resource<String>>

    fun updateUserDetails(firstName: String, lastName: String): Flow<Resource<String>>

    fun updatePassword(password: String, newPassword: String): Flow<Resource<String>>

    fun getUserDetails(): Flow<Resource<Users>>

    fun login(email: String, password: String): Flow<Resource<Boolean>>

    fun emailVerification(firstName: String, lastName: String, email: String): Flow<Resource<String>>

    fun resetPassword(email: String): Flow<Resource<String>>

    fun logOut()
}