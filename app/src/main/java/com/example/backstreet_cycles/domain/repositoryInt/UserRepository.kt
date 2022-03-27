package com.example.backstreet_cycles.domain.repositoryInt

import android.content.ContentValues
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.utils.ToastMessageHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface UserRepository {

    fun register(fName: String, lName: String, email: String, password: String): FirebaseUser?

    fun updateUserDetails(firstName: String, lastName: String) : Job

    fun updatePassword(password: String, newPassword: String)

    fun getUserDetails()

    fun login(email: String, password: String) : FirebaseUser?

    fun resetPassword(email: String)

    fun logout()
}