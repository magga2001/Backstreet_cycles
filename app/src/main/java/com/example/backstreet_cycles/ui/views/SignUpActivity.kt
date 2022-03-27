package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
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
                    TextUtils.isEmpty(sign_up_edit_user_details_firstName.text.toString().trim { it <= ' ' }) -> {
                        sign_up_edit_user_details_firstName.error = getString(R.string.enter_first_name)
                    }
                    TextUtils.isEmpty(sign_up_edit_user_details_lastName.text.toString().trim { it <= ' ' }) -> {
                        sign_up_edit_user_details_lastName.error = getString(R.string.enter_last_name)
                    }

                    TextUtils.isEmpty(sign_up_edit_user_details_email.text.toString().trim { it <= ' ' }) -> {
                        sign_up_edit_user_details_email.error = getString(R.string.enter_email)
                    }

                    TextUtils.isEmpty(sign_up_edit_user_details_password.text.toString().trim { it <= ' ' }) -> {
                        sign_up_edit_user_details_password.error = getString(R.string.enter_password)
                    }

                    TextUtils.isEmpty(sign_up_edit_user_details_confirm_password.text.toString().trim { it <= ' ' }) -> {
                        sign_up_edit_user_details_confirm_password.error = getString(R.string.enter_confirmed_password)
                    }

                    (sign_up_edit_user_details_confirm_password.text.toString().trim()) != (sign_up_edit_user_details_password.text.toString().trim { it <= ' ' }) -> {
                        sign_up_edit_user_details_confirm_password.error = getString(R.string.password_not_match)
                    }

                    else -> {
                        val firstName: String = sign_up_edit_user_details_firstName.text.toString().trim { it <= ' ' }
                        val lastName: String = sign_up_edit_user_details_lastName.text.toString().trim { it <= ' ' }
                        val email: String = sign_up_edit_user_details_email.text.toString().trim { it <= ' ' }
                        val password: String = sign_up_edit_user_details_password.text.toString().trim { it <= ' '}

                        signUpViewModel.register(firstName, lastName, email, password)
                        val intent = Intent(this, LogInActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                    }
                }
            }
        }
    }

