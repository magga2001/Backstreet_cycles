package com.example.backstreet_cycles.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LoggedInViewModel
import kotlinx.android.synthetic.main.activity_edit_user_profile.*

class EditUserProfileActivity : AppCompatActivity() {


    private lateinit var loggedInViewModel: LoggedInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loggedInViewModel = ViewModelProvider(this)[LoggedInViewModel::class.java]
        loggedInViewModel.getUpdatedProfileMutableLiveData().observe(this) { updated ->
            if (updated) {
                Toast.makeText(
                    this@EditUserProfileActivity,
                    getString(R.string.profile_updated),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@EditUserProfileActivity, HomePageActivity::class.java)
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
                    et_firstName.error = getString(R.string.enter_first_name)
                }
                TextUtils.isEmpty(et_lastName.text.toString().trim { it <= ' ' }) -> {
                    et_lastName.error = getString(R.string.enter_last_name)
                }
                else -> {
                    val firstName: String = et_firstName.text.toString().trim { it <= ' ' }
                    val lastName: String = et_lastName.text.toString().trim { it <= ' ' }
                    loggedInViewModel.updateUserDetails(firstName, lastName)
                }
            }
        }

//        buttonChangeEmailOrPassword.setOnClickListener {
//            val intent = Intent(this@EditUserProfileActivity, ChangeEmailOrPasswordActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            finish()
//        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@EditUserProfileActivity, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}