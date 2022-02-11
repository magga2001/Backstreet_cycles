package com.example.backstreet_cycles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_log_in.*


class LogInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        auth = FirebaseAuth.getInstance()


        buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
        }

        buttonLogin.setOnClickListener {
            logIn(
                et_email.text.trim().toString(),
                et_password.text.trim().toString()
            )
        }

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