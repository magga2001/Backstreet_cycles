package com.example.backstreet_cycles.ui.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_forgot_password.*

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    /**
     * Initialise the contents within the display of the ForgotPasswordPage
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initListener()
    }

    /**
     * Send link to reset password if an email was provided
     */
    private fun initListener() {
        forgot_password_SendPasswordReset_button.setOnClickListener {
            forgot_password_SendPasswordReset_button.setOnClickListener {
                val email= forgot_password_email.text.toString().trim { it <= ' ' }
                if (email.isEmpty()) {
                    forgot_password_email.error = "Please enter your email"
                } else {
                    forgotPasswordViewModel.resetPassword(email)
                }
            }
        }
    }
}