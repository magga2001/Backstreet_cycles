package com.example.backstreet_cycles.model

import android.app.Application
import android.content.ContentValues
import android.text.BoringLayout
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.UserDto
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AppRepository(private val application: Application) {
    private val mutableLiveData: MutableLiveData<FirebaseUser>
    private val loggedOutMutableLiveData: MutableLiveData<Boolean>
    private val firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    init {
        mutableLiveData = MutableLiveData()
        loggedOutMutableLiveData = MutableLiveData()
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            mutableLiveData.postValue(firebaseAuth.currentUser)
            loggedOutMutableLiveData.postValue(false)
        }
    }

    fun register(fName: String, lName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    createUserAccount(fName, lName, email, password)
                    mutableLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(application, "Registration failed "+ task.exception, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    fun createUserAccount(firstName: String, lastName: String, email: String, password: String) {
        val user = UserDto(firstName, lastName, email, password)
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
}







