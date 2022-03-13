package com.example.backstreet_cycles.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import kotlinx.android.synthetic.main.activity_log_in.*


class LogInActivity : AppCompatActivity() {

    private lateinit var loginRegisterViewModel: LogInRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        loginRegisterViewModel = ViewModelProviders.of(this)[LogInRegisterViewModel::class.java]
        loginRegisterViewModel.getMutableLiveData()
            .observe(this) { firebaseUser ->
                if (firebaseUser != null) {
                    val intent = Intent(this@LogInActivity, HomePageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

        buttonLogin.setOnClickListener {
            when {
                TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                    et_email.error = "Please enter your email"
                }

                TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                    et_password.error = "Please enter a password"
                }

                else -> loginRegisterViewModel.login(
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




}