package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.useCase.PermissionUseCase
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_log_in.*

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {

    private val logInViewModel: LogInViewModel by viewModels()

    /**
     * Initialise the contents within the display of the Log In activity
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        /**
         * Checks whether user is not null and starts HomePage activity
         **/
        logInViewModel.getMutableLiveData()
            .observe(this) { firebaseUser ->
                if (firebaseUser != null) {
                    val intent = Intent(this@LogInActivity, HomePageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            }

        initListener()

        PermissionUseCase.checkPermission(context = this, activity = this)
    }

    /**
     * Checks the whether the user's input for Log In is correct, if yes, it passes
     * input into the LogInViewModel. Otherwise, the error messages are displayed
     */

    private fun initListener() {

        log_in_button.setOnClickListener {
            when {
                TextUtils.isEmpty(log_in_email.text.toString().trim { it <= ' ' }) -> {
                    log_in_email.error = "Please enter your email"
                }

                TextUtils.isEmpty(log_in_password.text.toString().trim { it <= ' ' }) -> {
                    log_in_password.error = "Please enter a password"
                }

                else ->
                    logInViewModel.login(
                        log_in_email.text.trim().toString(),
                        log_in_password.text.trim().toString()
                    )
            }
        }

        /**
         * Implements Create Account Button behaviour
         **/

        log_in_buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /**
         * Implements Forgot Password Button behaviour
         **/

        log_in_clickForgotPassword.setOnClickListener {
            startActivity(Intent(this@LogInActivity, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

}