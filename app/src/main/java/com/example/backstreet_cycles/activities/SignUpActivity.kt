package com.example.backstreet_cycles.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.logicClasses.UserLogic
import com.example.backstreet_cycles.logicClasses.UserLogic.Companion.createUserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {



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


                TextUtils.isEmpty(et_confirmPassword.text.toString().trim() { it <= ' ' }) -> {
                    et_password.error = "Please confirm your password"
                }

                (et_confirmPassword.text.toString().trim()) != (et_password.text.toString().trim(){ it <= ' ' }) -> {
                    et_confirmPassword.error = "Passwords do not match"
                }

                else -> {
                    val firstName: String = et_firstName.text.toString().trim { it <= ' ' }
                    val lastName: String = et_lastName.text.toString().trim { it <= ' ' }
                    val email: String = et_email.text.toString().trim { it <= ' ' }
                    val password: String = et_password.text.toString().trim { it <= ' '}

                    val successful = UserLogic.signUp(firstName, lastName, email, password)
                    if (successful.isSuccessful){

                        Toast.makeText(
                            this,
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
                            "Invalid details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    }
                }
            }
        }
    }

