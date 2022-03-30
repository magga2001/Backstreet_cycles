package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.utils.SnackBarHelper
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    /**
     * Initialise the contents within the display of the Sign Up
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        signUpViewModel.getMessage().observe(this){
//            runBlocking {
////                delay(1000)
//                SnackBarHelper.displaySnackBar(signUpActivity, it)
////                val intent = Intent(this, LogInActivity::class.java)
////                startActivity(intent)
////                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//            }

            SnackBarHelper.displaySnackBar(signUpActivity, it)

        }

        initListener()
    }

    /**
     * Checks the whether the user's input for Sign Up is correct, if yes, it passes
     * input into the SignUpViewModel and starts the Log In activity. Otherwise, the error messages
     * are displayed
     */
    private fun initListener() {
        buttonSignUp.setOnClickListener {
            when {
                TextUtils.isEmpty(
                    sign_up_edit_user_details_firstName.text.toString().trim { it <= ' ' }) -> {
                    sign_up_edit_user_details_firstName.error = getString(R.string.enter_first_name)
                }
                TextUtils.isEmpty(
                    sign_up_edit_user_details_lastName.text.toString().trim { it <= ' ' }) -> {
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

                (sign_up_confirmPassword.text.toString()
                    .trim()) != (sign_up_password.text.toString().trim { it <= ' ' }) -> {
                    sign_up_confirmPassword.error = getString(R.string.password_not_match)
                }

                else -> {
                    val firstName: String =
                        sign_up_edit_user_details_firstName.text.toString().trim { it <= ' ' }
                    val lastName: String =
                        sign_up_edit_user_details_lastName.text.toString().trim { it <= ' ' }
                    val email: String = sign_up_change_email.text.toString().trim { it <= ' ' }
                    val password: String = sign_up_password.text.toString().trim { it <= ' ' }

                    lifecycleScope.launch {
                        signUpViewModel.register(firstName, lastName, email, password)
                    }
                }
            }
        }
    }
}

