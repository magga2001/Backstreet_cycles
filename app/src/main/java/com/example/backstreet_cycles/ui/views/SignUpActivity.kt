package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.LogInRegisterViewModel
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
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
            }
        }

        buttonSignUp.setOnClickListener {
                when {
                    TextUtils.isEmpty(sign_up_edit_user_details_firstName.text.toString().trim { it <= ' ' }) -> {
                        sign_up_edit_user_details_firstName.error = getString(R.string.enter_first_name)
                    }
                    TextUtils.isEmpty(sign_up_edit_user_details_lastName.text.toString().trim { it <= ' ' }) -> {
                        sign_up_edit_user_details_lastName.error = getString(R.string.enter_last_name)
                    }

                    TextUtils.isEmpty(sign_up_change_email.text.toString().trim { it <= ' ' }) -> {
                        sign_up_change_email.error = getString(R.string.enter_email)
                    }

                    TextUtils.isEmpty(sign_up_password.text.toString().trim { it <= ' ' }) -> {
                        sign_up_password.error = getString(R.string.enter_password)
                    }

                    TextUtils.isEmpty(sign_up_confirmPassword.text.toString().trim { it <= ' ' }) -> {
                        sign_up_confirmPassword.error = getString(R.string.enter_confirmed_password)
                    }

                    (sign_up_confirmPassword.text.toString().trim()) != (sign_up_password.text.toString().trim { it <= ' ' }) -> {
                        sign_up_confirmPassword.error = getString(R.string.password_not_match)
                    }

                    else -> {
                        val firstName: String = sign_up_edit_user_details_firstName.text.toString().trim { it <= ' ' }
                        val lastName: String = sign_up_edit_user_details_lastName.text.toString().trim { it <= ' ' }
                        val email: String = sign_up_change_email.text.toString().trim { it <= ' ' }
                        val password: String = sign_up_password.text.toString().trim { it <= ' '}

                        loginRegisterViewModel.register(firstName, lastName, email, password)
                        val intent = Intent(this, LogInActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

