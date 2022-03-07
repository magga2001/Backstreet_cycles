package com.example.backstreet_cycles.model

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.UserDto
import com.example.backstreet_cycles.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AppRepository(private val application: Application,
                    private val fireStore: FirebaseFirestore,
                    private val fireBaseAuth: FirebaseAuth) {
    private val mutableLiveData: MutableLiveData<FirebaseUser>
    private val loggedOutMutableLiveData: MutableLiveData<Boolean>
    private val updatedProfileMutableLiveData: MutableLiveData<Boolean>
    private val userDetailsMutableLiveData: MutableLiveData<UserDto>
    private val firebaseAuth: FirebaseAuth
    private val db = fireStore

    init {
        mutableLiveData = MutableLiveData()
        loggedOutMutableLiveData = MutableLiveData()
        updatedProfileMutableLiveData = MutableLiveData()
        userDetailsMutableLiveData = MutableLiveData()
        firebaseAuth = fireBaseAuth
        if (firebaseAuth.currentUser != null) {
            mutableLiveData.postValue(firebaseAuth.currentUser)
            loggedOutMutableLiveData.postValue(false)
            updatedProfileMutableLiveData.postValue(false)
        }
    }


    fun register(fName: String, lName: String, email: String, password: String): FirebaseUser? {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //firebaseAuth.currentUser?.sendEmailVerification()
                    emailVerification(fName,lName,email)

                    //createUserAccount(fName, lName, email)
                    //getUserDetails()
                    //mutableLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    createToastMessage(application.getString(R.string.REGISTRATION_FAILED) + task.exception)
                }
            }
        return firebaseAuth.currentUser
    }

    fun emailVerification(fName: String, lName: String, email: String) {
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task->
            if (task.isSuccessful) {
                createToastMessage("EMAIL VERIFICATION HAS BEEN SENT TO:  $email")
                logout()
                createUserAccount(fName, lName, email)
                getUserDetails()
                mutableLiveData.postValue(firebaseAuth.currentUser)

            }
            else{
                createToastMessage(application.getString(R.string.REGISTRATION_FAILED) + task.exception)

            }

        }
    }



    fun createUserAccount(firstName: String, lastName: String, email: String) {
        val user = UserDto(firstName, lastName, email)
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    TAG,
                    "DocumentSnapshot written with ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun updateUserDetails(firstName: String, lastName: String) =
        CoroutineScope(Dispatchers.IO).launch {

            val user = db
                .collection("users")
                .whereEqualTo("email", userDetailsMutableLiveData.value!!.email)
                .get()
                .await()

            if (user.documents.isNotEmpty()) {
                for (document in user) {
                    try {
                        db.collection("users").document(document.id).update("firstName", firstName)
                        db.collection("users").document(document.id).update("lastName", lastName)
                        db.collection("users").document(document.id)
                            .update("email", firebaseAuth.currentUser!!.email)
                        updatedProfileMutableLiveData.postValue(true)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            createToastMessage(e.message)
                        }
                    }

                }
            } else {
                withContext(Dispatchers.Main) {
                    createToastMessage(application.getString(R.string.NO_PERSON_MATCH))
                }
            }
        }

    fun updateEmailAndPassword(email: String, password: String, newPassword: String) {

        val credential =
            EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, password)

        firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Log.d("value", "User re-authenticated.")
                val user = FirebaseAuth.getInstance().currentUser
                user!!.updateEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        createToastMessage("Email Changed Current Email is $email")
                        val email = userDetailsMutableLiveData.value!!.email
                        if (email != null) {
                            updateEmail(email)
                            userDetailsMutableLiveData.value!!.email =
                                firebaseAuth.currentUser!!.email
                        }
                    } else {
                        createToastMessage(application.getString(R.string.UPDATE_FAILED))
                    }
                }
                if (newPassword.isNotEmpty()) {
                    user.updatePassword(newPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            createToastMessage(application.getString(R.string.PASSWORD_CHANGED))
                        } else {
                            createToastMessage(application.getString(R.string.UPDATE_FAILED))
                        }
                    }
                }
            } else {
                createToastMessage(application.getString(R.string.UPDATE_FAILED))
            }
        }
    }

    fun updateEmail(email: String) = CoroutineScope(Dispatchers.IO).launch {

        val user = db
            .collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        if (user.documents.isNotEmpty()) {
            for (document in user) {
                try {
                    db.collection("users").document(document.id)
                        .update("email", firebaseAuth.currentUser!!.email)
                    updatedProfileMutableLiveData.postValue(true)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        createToastMessage(e.message)
                    }
                }

            }
        } else {
            withContext(Dispatchers.Main) {
                createToastMessage(application.getString(R.string.NO_PERSON_MATCH))
            }
        }

    }

    fun getUserDetails() {
        db
            .collection("users")
            .whereEqualTo("email", firebaseAuth.currentUser!!.email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val userDetails = document.toObject(UserDto::class.java)
                        userDetailsMutableLiveData.postValue(userDetails)
                    }
                }

            }
    }

    fun login(email: String, password: String) : FirebaseUser? {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if (firebaseAuth.currentUser?.isEmailVerified == true) {
                        mutableLiveData.postValue(firebaseAuth.currentUser)
                    }
                    else{
                        createToastMessage(application.getString(R.string.LOG_IN_FAILED) + "EMAIL NOT VERIFIED")
                    }
                }
                else {
                    createToastMessage(application.getString(R.string.LOG_IN_FAILED) + task.exception)
                }
            }
        return firebaseAuth.currentUser
    }

    fun logout() {
        firebaseAuth.signOut()
        loggedOutMutableLiveData.postValue(true)
    }

    private fun createToastMessage(stringMessage: String?) {
        Toast.makeText(application, stringMessage, Toast.LENGTH_LONG)
            .show()
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }

    fun getLoggedOutMutableLiveData(): MutableLiveData<Boolean> {
        return loggedOutMutableLiveData
    }

    fun getUpdatedProfileMutableLiveData(): MutableLiveData<Boolean> {
        return updatedProfileMutableLiveData
    }

    fun getUserDetailsMutableLiveData(): MutableLiveData<UserDto> {
        return userDetailsMutableLiveData
    }
}







