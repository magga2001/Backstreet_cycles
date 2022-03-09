package com.example.backstreet_cycles.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LoggedInViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_change_email_or_password.*
import kotlinx.android.synthetic.main.activity_change_email_or_password.et_email
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_main.*

class ChangeEmailOrPasswordActivity : AppCompatActivity() {

    private lateinit var loggedInViewModel: LoggedInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email_or_password)

        loggedInViewModel = ViewModelProviders.of(this).get(LoggedInViewModel::class.java)
        loggedInViewModel.getMutableLiveData().observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                et_email.setText(firebaseUser.email)
            }
        }

        buttonSave.setOnClickListener {
            when {
                TextUtils.isEmpty(et_current_password.text.toString().trim() { it <= ' ' }) -> {
                    et_current_password.error = "In order for use to change your email or password you need to" +
                            " enter your old password"
                }
                else -> {
                    val currentPassword = et_current_password.text.toString().trim { it <= ' ' }
                    val newPassword = et_new_password.text.toString()
                    loggedInViewModel.getUserDetails()
                    loggedInViewModel.updateEmailAndPassword(currentPassword,newPassword)
                }
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@ChangeEmailOrPasswordActivity, EditUserProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}