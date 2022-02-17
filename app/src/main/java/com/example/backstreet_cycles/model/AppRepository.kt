package com.example.backstreet_cycles.model

import android.app.Application
import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AppRepository {
    private val application: Application
    private val mutableLiveData: MutableLiveData<FirebaseUser>
    private val firebaseAuth: FirebaseAuth
    private val db = Firebase.firestore

    constructor(application: Application){
        this.application = application
        mutableLiveData = MutableLiveData()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    public fun register(fName: String, lName: String, email: String, password: String) {
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

   fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }


//    companion object {
//
//        private lateinit var auth: FirebaseAuth
//        private val db = Firebase.firestore
//        private lateinit var acitvity: Activity
//
//
//        fun getActivity(): Activity {
//            return acitvity
//        }
//
//        fun signUp(email: String, password: String) {
//            auth = FirebaseAuth.getInstance()
//            var successful = false
//            auth.createUserWithEmailAndPassword(email,password)
//                .addOnSuccessListener {
//                    successful = true
//                }.addOnFailureListener {
//                    successful = false
//                }
//
//        }
//
//        private fun successful(success: Boolean) {
//            if (success) {
//                acitvity = MainActivity()
//            }
//            acitvity =  LogInActivity()
//        }
//

//    }
}







