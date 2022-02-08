package com.example.backstreet_cycles

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent

import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.backstreet_cycles.databinding.ActivityLogInBinding

import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        buttonSignUp.setOnClickListener {
            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
        }

    //        findViewById<Button>(R.id.button_register).setOnClickListener {
//            startActivity(Intent(this@LogInActivity, SignUpActivity::class.java))
//        }


    }
}