package com.example.backstreet_cycles.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LoggedInViewModel
import kotlinx.android.synthetic.main.activity_edit_user_profile.*
import kotlinx.android.synthetic.main.activity_edit_user_profile.et_firstName
import kotlinx.android.synthetic.main.activity_edit_user_profile.et_lastName

class EditUserProfileActivity : AppCompatActivity() {


    private lateinit var loggedInViewModel: LoggedInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_profile)

        loggedInViewModel = ViewModelProviders.of(this).get(LoggedInViewModel::class.java)
        loggedInViewModel.getUpdatedProfileMutableLiveData().observe(this) { updated ->
            if (updated) {
                Toast.makeText(
                    this@EditUserProfileActivity,
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@EditUserProfileActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        loggedInViewModel.getUserDetails()
        loggedInViewModel.getUserDetailsMutableLiveData().observe(this) { details ->
            if (details != null) {
                et_firstName.setText(details.firstName)
                et_lastName.setText(details.lastName)
            }
        }




        buttonUpdateProfile.setOnClickListener {
            when {
                TextUtils.isEmpty(et_firstName.text.toString().trim { it <= ' ' }) -> {
                    et_firstName.error = "Please enter your first name"
                }
                TextUtils.isEmpty(et_lastName.text.toString().trim { it <= ' ' }) -> {
                    et_lastName.error = "Please enter your last name"
                }
                else -> {
                    val firstName: String = et_firstName.text.toString().trim { it <= ' ' }
                    val lastName: String = et_lastName.text.toString().trim { it <= ' ' }
                    loggedInViewModel.updateUserDetails(firstName, lastName)
                }
            }
        }


    }

    override fun onBackPressed() {
        val intent = Intent(this@EditUserProfileActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}