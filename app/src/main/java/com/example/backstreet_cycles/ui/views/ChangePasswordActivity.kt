package com.example.backstreet_cycles.ui.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.utils.SnackBarHelper
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.coroutines.launch

/**
 * This activity launches Change Password page that is responsible for enabling user the password change
 */
@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    /**
     * Initialise the contents within the display of the ChangePasswordPage
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        changePasswordViewModel.getUserDetails()

        // Display email of the logged in user
        changePasswordViewModel.getUserInfo().observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                change_password_email.text = firebaseUser.email
            }
        }

        changePasswordViewModel.getUpdateDetail().observe(this){
            SnackBarHelper.displaySnackBar(changePasswordActivity, it)
        }

        // Update the password in the database
        change_password_SaveButton.setOnClickListener {
            when {
                TextUtils.isEmpty(
                    change_password_currentPassword.text.toString().trim { it <= ' ' }) -> {
                    change_password_currentPassword.error =
                        getString(R.string.to_change_password)
                }
                else -> {
                    val currentPassword =
                        change_password_currentPassword.text.toString().trim { it <= ' ' }
                    val newPassword = change_password_NewPassword.text.toString()
                    lifecycleScope.launch {
                        changePasswordViewModel.updatePassword(currentPassword, newPassword)
                    }
                }
            }
        }
    }

    /**
     * Return to the HomePage if the back button is clicked
     * @param item from the nav_header
     * @return true if clicked
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Terminate the ChangePassword display when back button is clicked
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}