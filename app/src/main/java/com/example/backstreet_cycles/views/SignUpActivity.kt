package com.example.backstreet_cycles.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import kotlinx.android.synthetic.main.activity_log_in.et_email
import kotlinx.android.synthetic.main.activity_log_in.et_password
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var loginRegisterViewModel: LogInRegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loginRegisterViewModel = ViewModelProvider(this)[LogInRegisterViewModel::class.java]
        loginRegisterViewModel.getMutableLiveData().observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                val intent = Intent(this@SignUpActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        buttonSignUp.setOnClickListener {
                when {
                    TextUtils.isEmpty(et_firstName.text.toString().trim { it <= ' ' }) -> {
                        et_firstName.error = getString(R.string.enter_first_name)
                    }
                    TextUtils.isEmpty(et_lastName.text.toString().trim { it <= ' ' }) -> {
                        et_lastName.error = getString(R.string.enter_last_name)
                    }

                    TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                        et_email.error = getString(R.string.enter_email)
                    }

                    TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                        et_password.error = getString(R.string.enter_password)
                    }

                    TextUtils.isEmpty(et_confirmPassword.text.toString().trim { it <= ' ' }) -> {
                        et_password.error = getString(R.string.enter_confirmed_password)
                    }

                    (et_confirmPassword.text.toString().trim()) != (et_password.text.toString().trim { it <= ' ' }) -> {
                        et_confirmPassword.error = getString(R.string.password_not_match)
                    }

                    else -> {
                        val firstName: String = et_firstName.text.toString().trim { it <= ' ' }
                        val lastName: String = et_lastName.text.toString().trim { it <= ' ' }
                        val email: String = et_email.text.toString().trim { it <= ' ' }
                        val password: String = et_password.text.toString().trim { it <= ' '}

                        loginRegisterViewModel.register(firstName, lastName, email, password)
                    }
                }
            }
        }
    override fun onBackPressed() {
        val intent = Intent(this@SignUpActivity, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    }

