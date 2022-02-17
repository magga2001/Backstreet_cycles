package com.example.backstreet_cycles.views

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.viewModel.LoggedInViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var loggedInViewModel: LoggedInViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loggedInViewModel = ViewModelProviders.of(this).get(LoggedInViewModel::class.java)
        loggedInViewModel.getMutableLiveData().observe(this, Observer<FirebaseUser> { firebaseUser ->
            if (firebaseUser != null) {
                textView.text = "Hello: " + firebaseUser.email
            }
        })

        loggedInViewModel.getLoggedOutMutableLiveData().observe(this, Observer<Boolean> { loggedOut ->
            if (loggedOut) {
                startActivity(Intent(this@MainActivity,LogInActivity::class.java))
                finish()
            }
        })

        buttonLogOut.setOnClickListener {
            loggedInViewModel.logOut()
        }

    }
}