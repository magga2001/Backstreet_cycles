package com.example.backstreet_cycles.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.et_email
import kotlinx.android.synthetic.main.activity_log_in.et_password
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginRegiterViewModel: LogInRegisterViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        loginRegiterViewModel = ViewModelProviders.of(this).get(LogInRegisterViewModel::class.java)
        loginRegiterViewModel.getMutableLiveData().observe(this, Observer<FirebaseUser> {
            fun onChanged(firebaseUser: FirebaseUser){
                if (firebaseUser != null) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                }
            }


        })


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

                        loginRegiterViewModel.register(firstName, lastName, email, password)

                    }
                }


        }


//        buttonSignUp.setOnClickListener {
//            when {
//                TextUtils.isEmpty(et_firstName.text.toString().trim() { it <= ' ' }) -> {
//                    et_firstName.error = "Please enter your first name"
//                }
//                TextUtils.isEmpty(et_lastName.text.toString().trim() { it <= ' ' }) -> {
//                    et_lastName.error = "Please enter your last name"
//                }
//
//                TextUtils.isEmpty(et_email.text.toString().trim() { it <= ' ' }) -> {
//                    et_email.error = "Please enter your email"
//                }
//
//                TextUtils.isEmpty(et_password.text.toString().trim() { it <= ' ' }) -> {
//                    et_password.error = "Please enter a password"
//                }
//
//
//                TextUtils.isEmpty(et_confirmPassword.text.toString().trim() { it <= ' ' }) -> {
//                    et_password.error = "Please confirm your password"
//                }
//
//                (et_confirmPassword.text.toString().trim()) != (et_password.text.toString().trim(){ it <= ' ' }) -> {
//                    et_confirmPassword.error = "Passwords do not match"
//                }
//
//                else -> {
//                    val firstName: String = et_firstName.text.toString().trim { it <= ' ' }
//                    val lastName: String = et_lastName.text.toString().trim { it <= ' ' }
//                    val email: String = et_email.text.toString().trim { it <= ' ' }
//                    val password: String = et_password.text.toString().trim { it <= ' '}
//
//                    UserLogic.signUp(email, password)
//                    if (UserLogic.getActivity() == this@SignUpActivity){
//                        Toast.makeText(
//                            this@SignUpActivity,
//                            "Invalid details",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        UserLogic.createUserAccount(firstName, lastName, email, password)
//                        Toast.makeText(
//                            this,
//                            "Account successfully created!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    val intent = Intent(this@SignUpActivity, UserLogic.getActivity()::class.java)
//                    intent.flags =
//                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    finish()
//
//                    }
//                }
//            }
        }
    }

