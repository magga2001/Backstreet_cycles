package com.example.backstreet_cycles.logicClasses


import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.example.backstreet_cycles.DTO.UserDto
import com.example.backstreet_cycles.activities.SignUpActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserLogic {



    companion object {

        private lateinit var auth: FirebaseAuth
        private val db = Firebase.firestore




        fun signUp(firstName: String, lastName: String, email: String, password: String): Task<AuthResult> {
            auth = FirebaseAuth.getInstance()
//            var successful: Boolean
            var successful = auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        createUserAccount(firstName,lastName,email,password)
//                        successful = task.isSuccessful
                    }else {
//                        successful = false
                    }
                }
            return successful
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
    }
}







