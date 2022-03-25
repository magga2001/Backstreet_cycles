package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.useCase.PermissionUseCase
import com.example.backstreet_cycles.ui.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_log_in.*

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {

    private val loginRegisterViewModel: LogInRegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


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

        initListener()

        PermissionUseCase.checkPermission(context = this, activity = this)
    }

    private fun initListener()
    {

        buttonLogin.setOnClickListener {
            when {
                TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                    et_email.error = "Please enter your email"
                }

                TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                    et_password.error = "Please enter a password"
                }

                else ->
                    loginRegisterViewModel.login(
                        et_email.text.trim().toString(),
                        et_password.text.trim().toString()
                    )
            }
        }

        buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        clickForgotPassword.setOnClickListener{
            startActivity(Intent(this@LogInActivity, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }

}