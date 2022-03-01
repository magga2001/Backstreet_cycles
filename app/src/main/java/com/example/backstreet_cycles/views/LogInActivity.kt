package com.example.backstreet_cycles.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.backstreet_cycles.DTO.UserDto
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.AppRepository
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_log_in.et_email
import kotlinx.android.synthetic.main.activity_log_in.et_password


class LogInActivity : AppCompatActivity() {

    private lateinit var loginRegiterViewModel: LogInRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        intialiseSecondDB()

        loginRegiterViewModel = ViewModelProviders.of(this).get(LogInRegisterViewModel::class.java)
        loginRegiterViewModel.getMutableLiveData()
            .observe(this, Observer<FirebaseUser> { firebaseUser ->
                if (firebaseUser != null) {
                    val intent = Intent(this@LogInActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            })


        buttonLogin.setOnClickListener {
            when {
                TextUtils.isEmpty(et_email.text.toString().trim() { it <= ' ' }) -> {
                    et_email.error = "Please enter your email"
                }

                TextUtils.isEmpty(et_password.text.toString().trim() { it <= ' ' }) -> {
                    et_password.error = "Please enter a password"
                }

                else -> loginRegiterViewModel.login(
                    et_email.text.trim().toString(),
                    et_password.text.trim().toString()
                )
            }
        }

        buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
        }


    }

    override fun onBackPressed() {

    }

    fun intialiseSecondDB() {

        val options = FirebaseOptions.Builder()
                .setProjectId("backstreetcyclestesting-61f8e")
                .setApplicationId("1:808206718442:android:c199598c548e6fca628e93")
                .setApiKey("AIzaSyDHMVdka4bwNzSXOKy65GZCQh8ONpHv058")
                .build()
        Firebase.initialize(application, options, "secondary")
        val secondary = Firebase.app("secondary")
        val mutableLiveData = MutableLiveData<FirebaseUser>()
        val appRepository = AppRepository(application, FirebaseFirestore.getInstance(secondary), FirebaseAuth.getInstance(secondary), mutableLiveData)
        appRepository.register("One","Coconut","more@example.com","123456")

    }


}