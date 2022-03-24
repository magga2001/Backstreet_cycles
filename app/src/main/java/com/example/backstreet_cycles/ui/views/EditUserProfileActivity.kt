package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.utils.SnackbarHelper
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
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
                SnackbarHelper.displaySnackbar(editUserProfileActivity,"Profile Updated Successfully")
                val intent = Intent(this@EditUserProfileActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        loggedInViewModel.getUserDetails()
        loggedInViewModel.getUserDetailsMutableLiveData().observe(this) { details ->
            if (details != null) {
                edit_user_details_firstName.setText(details.firstName)
                edit_user_details_lastName.setText(details.lastName)
            }
        }

        edit_user_details_SaveButton.setOnClickListener {
            when {
                TextUtils.isEmpty(edit_user_details_firstName.text.toString().trim { it <= ' ' }) -> {
                    edit_user_details_firstName.error = getString(R.string.enter_first_name)
                }
                TextUtils.isEmpty(edit_user_details_lastName.text.toString().trim { it <= ' ' }) -> {
                    edit_user_details_lastName.error = getString(R.string.enter_last_name)
                }
                else -> {
                    val firstName: String = edit_user_details_firstName.text.toString().trim { it <= ' ' }
                    val lastName: String = edit_user_details_lastName.text.toString().trim { it <= ' ' }
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
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

}