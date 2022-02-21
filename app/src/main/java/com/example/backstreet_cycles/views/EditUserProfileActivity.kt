package com.example.backstreet_cycles.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.backstreet_cycles.DTO.UserDto
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.viewModel.LoggedInViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_edit_user_profile.*
import kotlinx.android.synthetic.main.activity_edit_user_profile.et_firstName
import kotlinx.android.synthetic.main.activity_edit_user_profile.et_lastName
import kotlinx.android.synthetic.main.activity_sign_up.*

class EditUserProfileActivity : AppCompatActivity() {


    private lateinit var logedInViewModel: LoggedInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_profile)

        logedInViewModel = ViewModelProviders.of(this).get(LoggedInViewModel::class.java)
        logedInViewModel.getUpdatedProfileMutableLiveData().observe(this, Observer<Boolean> { updated ->
            if (updated) {
                Toast.makeText(this@EditUserProfileActivity, "Proflie updated successfuly", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EditUserProfileActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })

        logedInViewModel.getUserDetailsMutableLiveData().observe(this, Observer<UserDto> { details ->
            if (details != null) {
                et_firstName.setText(details.firstName)
                et_lastName.setText(details.lastName)
            }
        })



        buttonUpdateProfile.setOnClickListener {
            when {
                TextUtils.isEmpty(et_firstName.text.toString().trim() { it <= ' ' }) -> {
                    et_firstName.error = "Please enter your first name"
                }
                TextUtils.isEmpty(et_lastName.text.toString().trim() { it <= ' ' }) -> {
                    et_lastName.error = "Please enter your last name"
                }
                else -> {
                    val firstName: String = et_firstName.text.toString().trim { it <= ' ' }
                    val lastName: String = et_lastName.text.toString().trim { it <= ' ' }
                    logedInViewModel.updateUserDetails(firstName, lastName)
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