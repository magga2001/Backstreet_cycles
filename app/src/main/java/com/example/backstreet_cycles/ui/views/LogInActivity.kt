package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.useCase.PermissionHelper
import com.example.backstreet_cycles.ui.viewModel.LogInRegisterViewModel
import kotlinx.android.synthetic.main.activity_log_in.*


class LogInActivity : AppCompatActivity() {

    private lateinit var loginRegisterViewModel: LogInRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        loginRegisterViewModel = ViewModelProvider(this)[LogInRegisterViewModel::class.java]
        loginRegisterViewModel.getMutableLiveData()
            .observe(this) { firebaseUser ->
                if (firebaseUser != null) {
                    val intent = Intent(this@LogInActivity, HomePageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                }
            }

        log_in_button.setOnClickListener {
            when {
                TextUtils.isEmpty(log_in_email.text.toString().trim { it <= ' ' }) -> {
                    log_in_email.error = "Please enter your email"
                }

                TextUtils.isEmpty(log_in_password.text.toString().trim { it <= ' ' }) -> {
                    log_in_password.error = "Please enter a password"
                }

                else ->
                    loginRegisterViewModel.login(
                    log_in_email.text.trim().toString(),
                    log_in_password.text.trim().toString()
                )
            }
        }

        log_in_buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        log_in_clickForgotPassword.setOnClickListener{
            startActivity(Intent(this@LogInActivity, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        PermissionHelper.checkPermission(context = this, activity = this)
    }


    override fun onBackPressed() {

    }




}