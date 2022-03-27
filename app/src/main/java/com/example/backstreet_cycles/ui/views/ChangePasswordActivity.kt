package com.example.backstreet_cycles.ui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_change_password.*


@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        changePasswordViewModel.getMutableLiveData().observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                change_password_email.text = firebaseUser.email
            }
        }

        change_password_SaveButton.setOnClickListener {
            when {
                TextUtils.isEmpty(change_password_currentPassword.text.toString().trim { it <= ' ' }) -> {
                    change_password_currentPassword.error = "In order for use to change your email or password you need to" +
                            " enter your old password"
                }
                else -> {
                    val currentPassword = change_password_currentPassword.text.toString().trim { it <= ' ' }
                    val newPassword = change_password_NewPassword.text.toString()
                    changePasswordViewModel.getUserDetails()
                    changePasswordViewModel.updatePassword(currentPassword,newPassword)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }



}