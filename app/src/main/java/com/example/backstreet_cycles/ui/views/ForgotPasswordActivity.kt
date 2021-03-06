package com.example.backstreet_cycles.ui.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.utils.SnackBarHelper
import com.example.backstreet_cycles.ui.viewModel.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.coroutines.launch

/**
 * This activity launches Forgot Password page which enables user to retrieve access to their account
 * by sending a verification link to their email
 */
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

        forgotPasswordViewModel.getResetPassword().observe(this){
            SnackBarHelper.displaySnackBar(forgotPasswordActivity, it)
        }

        initListener()
    }

    /**
     * Send link to reset password if an email was provided
     */
    private fun initListener() {
        forgot_password_SendPasswordReset_button.setOnClickListener {
            val email = forgot_password_email.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                forgot_password_email.error = getString(R.string.enter_email_forgot_password)
            } else {
                lifecycleScope.launch {
                    forgotPasswordViewModel.resetPassword(email)
                }
            }
        }
    }
}