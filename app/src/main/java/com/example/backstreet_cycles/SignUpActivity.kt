package com.example.backstreet_cycles

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.backstreet_cycles.DTO.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)



        buttonSignUp.setOnClickListener {
            when {
                TextUtils.isEmpty(et_firstName.text.toString().trim() { it <= ' ' }) -> {
                    et_firstName.error = "Please enter your first name"
                }
                TextUtils.isEmpty(et_lastName.text.toString().trim() { it <= ' ' }) -> {
                    et_lastName.error = "Please enter your last name"
                }

                TextUtils.isEmpty(et_email.text.toString().trim() { it <= ' ' }) -> {
                    et_email.error = "Please enter your email"
                }

                TextUtils.isEmpty(et_password.text.toString().trim() { it <= ' ' }) -> {
                    et_password.error = "Please enter a password"
                }

                else -> {
                    val firstName: String = et_firstName.text.toString().trim { it <= ' ' }
                    val lastName: String = et_lastName.text.toString().trim { it <= ' ' }
                    val email: String = et_email.text.toString().trim { it <= ' ' }
                    val password: String = et_password.text.toString().trim { it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                createUserAccount(firstName,lastName,email,password)
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Account successfully created!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                }
                            }
                        }
                    }
                }
    }

    private fun createUserAccount(firstName: String, lastName: String, email: String, password: String) {
        val user = UserDto(firstName, lastName, email, password)
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}