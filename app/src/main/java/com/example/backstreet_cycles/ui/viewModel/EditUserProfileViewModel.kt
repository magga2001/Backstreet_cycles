package com.example.backstreet_cycles.ui.viewModel;

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.backstreet_cycles.data.repository.UserRepository
import com.example.backstreet_cycles.domain.model.dto.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class EditUserProfileViewModel @Inject constructor(
        @ApplicationContext applicationContext: Context
): ViewModel() {
        private val mApplication = Contexts.getApplication(applicationContext)
        private val userRepository: UserRepository = UserRepository(mApplication, Firebase.firestore, FirebaseAuth.getInstance())
        private val updatedProfileMutableLiveData: MutableLiveData<Boolean> = userRepository.getUpdatedProfileMutableLiveData()
        private val userDetailsMutableLiveData: MutableLiveData<Users> = userRepository.getUserDetailsMutableLiveData()

        fun updateUserDetails(firstName: String, lastName: String) {
                userRepository.updateUserDetails(firstName, lastName)
        }

        fun getUpdatedProfileMutableLiveData(): MutableLiveData<Boolean> {
                return updatedProfileMutableLiveData
        }

        fun getUserDetails() {
                return userRepository.getUserDetails()
        }

        fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
                return userDetailsMutableLiveData
        }
}
