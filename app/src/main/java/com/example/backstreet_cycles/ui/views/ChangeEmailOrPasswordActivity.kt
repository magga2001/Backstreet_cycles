package com.example.backstreet_cycles.ui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_change_email_or_password.*


@AndroidEntryPoint
class ChangeEmailOrPasswordActivity : AppCompatActivity() {

    private val loggedInViewModel: LoggedInViewModel by viewModels()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email_or_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loggedInViewModel.getMutableLiveData().observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                et_email.text = firebaseUser.email
            }
        }

        buttonSave.setOnClickListener {
            when {
                TextUtils.isEmpty(et_current_password.text.toString().trim { it <= ' ' }) -> {
                    et_current_password.error = "In order for use to change your email or password you need to" +
                            " enter your old password"
                }
                else -> {
                    val currentPassword = et_current_password.text.toString().trim { it <= ' ' }
                    val newPassword = et_new_password.text.toString()
                    loggedInViewModel.getUserDetails()
                    loggedInViewModel.updateEmailAndPassword(currentPassword,newPassword)
                }
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@ChangeEmailOrPasswordActivity, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onClickHome()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onClickHome() {
        super.onBackPressed()
    }



}