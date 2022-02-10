package com.example.backstreet_cycles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_log_in.buttonLogin as buttonLogin
import kotlinx.android.synthetic.main.activity_log_in.et_password as et_password
import kotlinx.android.synthetic.main.activity_sign_up.et_email as et_email

class LogInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var customToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        auth = FirebaseAuth.getInstance()


        buttonSignUp.setOnClickListener {
            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
        }

        buttonLogin.setOnClickListener {
            logIn(
                et_email.text.trim().toString(),
                et_password.text.trim().toString()
            )
        }


//            findViewById<Button>(R.id.button_register).setOnClickListener {
//            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
//        }

    }

    fun logIn(email:String,password:String ){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task->
                if (task.isSuccessful){
                    val intent=Intent(this,MainActivity::class.java);
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"Error"+task.exception, Toast.LENGTH_LONG).show()
                }
            }
    }


}