package com.example.backstreet_cycles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.backstreet_cycles.databinding.ActivityLogInBinding
import com.example.backstreet_cycles.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_log_in.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        sign_up_button.setOnClickListener {
            when {
                TextUtils.isEmpty(et_email.text.toString().trim() { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please enter your email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_password.text.toString().trim() { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please enter your password",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    val email: String = et_email.text.toString().trim { it <= ' ' }
                    val password: String = et_password.text.toString().trim { it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(
                            { task ->
                                if (task.isSuccessful) {
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    Toast.makeText(
                                        this@SignUpActivity,
                                        "Account successfully created!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent =
                                        Intent(this@SignUpActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        }
                    }
                }
    }
}