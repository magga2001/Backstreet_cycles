package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.utils.SnackbarHelper
import com.example.backstreet_cycles.ui.viewModel.EditUserProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_edit_user_profile.*

@AndroidEntryPoint
class EditUserProfileActivity : AppCompatActivity() {

    private val editUserProfileViewModel: EditUserProfileViewModel by viewModels()

    /**
     * Initialise the contents within the display of the EditProfilePage
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Indication that the profile has been updated
        editUserProfileViewModel.getUpdatedProfileMutableLiveData().observe(this) { updated ->
            if (updated) {
                SnackbarHelper.displaySnackbar(editUserProfileActivity,"Profile Updated Successfully")
                val intent = Intent(this@EditUserProfileActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        editUserProfileViewModel.getUserDetails()

        // Display the first and last name of the user if they exist
        editUserProfileViewModel.getUserDetailsMutableLiveData().observe(this) { details ->
            if (details != null) {
                edit_user_details_firstName.setText(details.firstName)
                edit_user_details_lastName.setText(details.lastName)
            }
        }

        initListener()
    }

    // Display helpful text in the text fields if they aren't already containing information
    private fun initListener()
    {
        edit_user_details_SaveButton.setOnClickListener {
            when {
                TextUtils.isEmpty(
                    edit_user_details_firstName.text.toString().trim { it <= ' ' }) -> {
                    edit_user_details_firstName.error = getString(R.string.enter_first_name)
                }
                TextUtils.isEmpty(
                    edit_user_details_lastName.text.toString().trim { it <= ' ' }) -> {
                    edit_user_details_lastName.error = getString(R.string.enter_last_name)
                }
                else -> {
                    val firstName: String =
                        edit_user_details_firstName.text.toString().trim { it <= ' ' }
                    val lastName: String =
                        edit_user_details_lastName.text.toString().trim { it <= ' ' }
                    editUserProfileViewModel.updateUserDetails(firstName, lastName)
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
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }
}