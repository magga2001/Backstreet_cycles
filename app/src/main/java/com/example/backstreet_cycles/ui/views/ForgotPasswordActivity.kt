package com.example.backstreet_cycles.ui.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_forgot_password.*

@AndroidEntryPoint
class ForgotPasswordActivity  : AppCompatActivity() {

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initListener()

    }

    private fun initListener()
    {
        button_send_password_reset.setOnClickListener{
            val email = et_email_forgot_password.text.toString().trim{it<=' ' }
            if (email.isEmpty()){
                et_email_forgot_password.error = "Please enter your email"
            }
            else{
                forgotPasswordViewModel.resetPassword(email)
            }

        }
    }






}