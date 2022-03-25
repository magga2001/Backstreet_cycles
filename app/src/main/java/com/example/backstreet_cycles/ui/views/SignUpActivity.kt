package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_log_in.et_email
import kotlinx.android.synthetic.main.activity_log_in.et_password
import kotlinx.android.synthetic.main.activity_sign_up.*

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private val signUpViewModel: SignUpViewModel  by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initListener()
    }

        private fun initListener()
        {
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

                        signUpViewModel.register(firstName, lastName, email, password)
                        val intent = Intent(this, LogInActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                    }
                }
            }
        }
    }

