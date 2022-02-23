package com.example.backstreet_cycles.model

import android.R.attr.password
import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.UserDto
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AppRepository(private val application: Application) {
    private val mutableLiveData: MutableLiveData<FirebaseUser>
    private val loggedOutMutableLiveData: MutableLiveData<Boolean>
    private val updatedProfileMutableLiveData: MutableLiveData<Boolean>
    private val userDetailsMutableLiveData: MutableLiveData<UserDto>
    private val firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    init {
        mutableLiveData = MutableLiveData()
        loggedOutMutableLiveData = MutableLiveData()
        updatedProfileMutableLiveData = MutableLiveData()
        userDetailsMutableLiveData = MutableLiveData()
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            mutableLiveData.postValue(firebaseAuth.currentUser)
            loggedOutMutableLiveData.postValue(false)
            updatedProfileMutableLiveData.postValue(false)
        }
    }

    fun register(fName: String, lName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    createUserAccount(fName, lName, email)
                    getUserDetails()
                    mutableLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(application, "Registration failed "+ task.exception, Toast.LENGTH_SHORT)
                        .show()
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

    fun updateUserDetails(firstName: String, lastName: String) = CoroutineScope(Dispatchers.IO).launch {

        val user = db
            .collection("users")
            .whereEqualTo("email", userDetailsMutableLiveData.value!!.email)
            .get()
            .await()

        if(user.documents.isNotEmpty()){
            for (document in user) {
                try {
                    db.collection("users").document(document.id).update("firstName", firstName)
                    db.collection("users").document(document.id).update("lastName", lastName)
                    db.collection("users").document(document.id).update("email", firebaseAuth.currentUser!!.email)
                    updatedProfileMutableLiveData.postValue(true)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(application, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }else {
            withContext(Dispatchers.Main) {
                Toast.makeText(application, "No persons matched the query.", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    fun updateEmailAndPassword(email: String, password: String, newPassword: String) {

            val credential = EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!,password)

            firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Log.d("value", "User re-authenticated.")
                    val user = FirebaseAuth.getInstance().currentUser
                    user!!.updateEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                application,
                                "Email Changed Current Email is $email",
                                Toast.LENGTH_SHORT
                            ).show()
                            val email = userDetailsMutableLiveData.value!!.email
                            if (email != null) {
                                updateEmail(email)
                                userDetailsMutableLiveData.value!!.email = firebaseAuth.currentUser!!.email
                            }
                        } else {
                            Toast.makeText(
                                application,
                                "Update Failed " + task.exception,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                    if (newPassword.isNotEmpty()) {
                        user.updatePassword(newPassword).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    application,
                                    "Password Changed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    application,
                                    "Update Failed " + task.exception,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
                }else {
                    Toast.makeText(
                        application,
                        "Update Failed " + task.exception,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }

    fun updateEmail(email: String) = CoroutineScope(Dispatchers.IO).launch {

        val user = db
            .collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        if(user.documents.isNotEmpty()){
            for (document in user) {
                try {
                    db.collection("users").document(document.id).update("email", firebaseAuth.currentUser!!.email)
                    updatedProfileMutableLiveData.postValue(true)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(application, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }else {
            withContext(Dispatchers.Main) {
                Toast.makeText(application, "No persons matched the query.", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    fun getUserDetails() {
        db
            .collection("users")
            .whereEqualTo("email", firebaseAuth.currentUser!!.email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    for (document in task.result) {
                        val userDetails = document.toObject(UserDto::class.java)
                        userDetailsMutableLiveData.postValue(userDetails)
                    }
                }

            }
    }

    fun login(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    mutableLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(application, "Log in failed "+ task.exception, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    fun logout(){
        firebaseAuth.signOut()
        loggedOutMutableLiveData.postValue(true)
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







