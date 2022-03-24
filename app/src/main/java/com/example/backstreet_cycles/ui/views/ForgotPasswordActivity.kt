package com.example.backstreet_cycles.ui.views

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        forgot_password_SendPasswordReset_button.setOnClickListener{
            val email:String=forgot_password_email.text.toString().trim{it<=' ' }
            if (email.isEmpty()){
                forgot_password_email.error = "Please enter your email"
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                    task -> if (task.isSuccessful){
                        Toast.makeText(this@ForgotPasswordActivity, "Email reset email sent", Toast.LENGTH_LONG).show()
                    finish()
                    }
                    else{
                    Toast.makeText(this@ForgotPasswordActivity, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()

                }
                }
            }

        }

    }






}