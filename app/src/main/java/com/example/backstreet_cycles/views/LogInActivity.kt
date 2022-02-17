package com.example.backstreet_cycles.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
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


class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
//        auth = FirebaseAuth.getInstance()
//        val userLogic = UserLogic



        buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
        }
//
//        buttonLogin.setOnClickListener {
//            when {
//                TextUtils.isEmpty(et_email.text.toString().trim() { it <= ' ' }) -> {
//                    et_email.error = "Please enter your email"
//                }
//
//                TextUtils.isEmpty(et_password.text.toString().trim() { it <= ' ' }) -> {
//                    et_password.error = "Please enter a password"
//                }
//
//                else -> logIn(
//                    et_email.text.trim().toString(),
//                    et_password.text.trim().toString()
//                )
//            }
//
//        }
//
//    }
//
//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if(currentUser != null){
//            reload()
//        }
//    }
//
//
//    private fun reload() {
//        startActivity(Intent(this@LogInActivity,MainActivity::class.java))
//    }
//
//    fun logIn(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val intent = Intent(this, MainActivity::class.java)
//                    Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
//                    this.startActivity(intent)
//                } else {
//                    Toast.makeText(this, "Invalid details", Toast.LENGTH_LONG).show()
//                }
//            }
    }


}