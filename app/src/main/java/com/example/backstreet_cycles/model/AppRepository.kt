package com.example.backstreet_cycles.model

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Looper
import android.text.BoringLayout
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.UserDto
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception


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
                    ContentValues.TAG,
                    "DocumentSnapshot written with ID: ${documentReference.id}"
                )
            }

            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun updateUserDetails(firstName: String, lastName: String) = CoroutineScope(Dispatchers.IO).launch {

        val user = db
            .collection("users")
            .whereEqualTo("email", firebaseAuth.currentUser!!.email)
            .get()
            .await()

        if(user.documents.isNotEmpty()){
            for (document in user) {
                try {
                    db.collection("users").document(document.id).update("firstName", firstName)
                    db.collection("users").document(document.id).update("lastName", lastName)
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
                        var userDetails = document.toObject(UserDto::class.java)
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







