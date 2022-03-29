package com.example.backstreet_cycles.data.repository

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.utils.ToastMessageHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber


open class UserRepositoryImpl() : UserRepository

{
    private val mutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val loggedOutMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val updatedProfileMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val userDetailsMutableLiveData: MutableLiveData<Users> = MutableLiveData()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dataBase = FirebaseFirestore.getInstance()
    private val application = Application()

    init {
        if (firebaseAuth.currentUser != null) {
            mutableLiveData.postValue(firebaseAuth.currentUser)
            loggedOutMutableLiveData.postValue(false)
            updatedProfileMutableLiveData.postValue(false)
        }
    }

    override fun register(fName: String, lName: String, email: String, password: String): FirebaseUser? {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mutableLiveData.postValue(firebaseAuth.currentUser)
                    emailVerification(fName,lName,email)

                } else {
                    ToastMessageHelper.createToastMessage(application,application.getString(R.string.REGISTRATION_FAILED) + task.exception!!.localizedMessage)
                }
            }
        return firebaseAuth.currentUser
    }

    private fun emailVerification(fName: String, lName: String, email: String) {
        ToastMessageHelper.createToastMessage(application,"EMAIL VERIFICATION BEING SENT TO:  $email")
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task->
            if (task.isSuccessful) {
                logout()
                ToastMessageHelper.createToastMessage(application,"PLEASE VERIFY YOUR EMAIL:  $email")
                //this needs to be relocated
                createUserAccount(fName, lName, email)
            }
            else{
                ToastMessageHelper.createToastMessage(application,application.getString(R.string.REGISTRATION_FAILED) + task.exception!!.localizedMessage)
            }

        }
    }

    private fun createUserAccount(firstName: String, lastName: String, email: String) {
        val user = Users(firstName, lastName, email)
        dataBase.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Timber.tag(TAG).d("DocumentSnapshot written with ID: ${documentReference.id}")

            }
            .addOnFailureListener { error ->
                Timber.tag(TAG).w("Error adding document")
                Timber.tag(TAG).e(error)

            }
    }

    override fun updateUserDetails(firstName: String, lastName: String): Job =
        CoroutineScope(Dispatchers.IO).launch {

            val user =  dataBase
            .collection("users")
            .whereEqualTo("email", userDetailsMutableLiveData.value!!.email)
            .get()
            .await()

            if (user.documents.isNotEmpty()) {
                for (document in user) {
                    try {
                        dataBase.collection("users").document(document.id).update("firstName", firstName)
                        dataBase.collection("users").document(document.id).update("lastName", lastName)
                        dataBase.collection("users").document(document.id)
                            .update("email", firebaseAuth.currentUser!!.email)
                        updatedProfileMutableLiveData.postValue(true)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            ToastMessageHelper.createToastMessage(application,e.message)
                        }
                    }

                }
            } else {
                withContext(Dispatchers.Main) {
//                    ToastMessageHelper.createToastMessage(application,application.getString(R.string.NO_PERSON_MATCH))
                }
            }
        }

    override fun updatePassword(password: String, newPassword: String) {

        val credential =
            EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, password)


        firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Timber.tag("value").w("User re-authenticated.")
                val user = FirebaseAuth.getInstance().currentUser
                if (newPassword.isNotEmpty()) {
                    user!!.updatePassword(newPassword).addOnCompleteListener { t ->
                        if (t.isSuccessful) {
                            ToastMessageHelper.createToastMessage(application,application.getString(R.string.PASSWORD_CHANGED))
                        } else {
                            ToastMessageHelper.createToastMessage(application,application.getString(R.string.UPDATE_FAILED))
                        }
                    }
                }
            } else {
//                ToastMessageHelper.createToastMessage(application,application.getString(R.string.UPDATE_FAILED))

            }
        }
    }

    override fun getUserDetails() {
        try {
            dataBase
                .collection("users")
                .whereEqualTo("email", firebaseAuth.currentUser!!.email)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val userDetails = document.toObject(Users::class.java)
                            userDetailsMutableLiveData.postValue(userDetails)
                        }
                    }
                }
        } catch(e: Exception) {
//            ToastMessageHelper.createToastMessage(application,e.localizedMessage)
            Log.i("Hello", "getUserDetails")
        }
    }

    override fun login(email: String, password: String) : FirebaseUser? {
        EspressoIdlingResource.increment()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if (firebaseAuth.currentUser?.isEmailVerified == true) {
                        mutableLiveData.postValue(firebaseAuth.currentUser)
                        EspressoIdlingResource.decrement()
                    }
                    else{
                        ToastMessageHelper.createToastMessage(application,application.getString(R.string.LOG_IN_FAILED) + " Please verify your email address")
                        EspressoIdlingResource.decrement()
                    }
                }
                else {
                    ToastMessageHelper.createToastMessage(application,application.getString(R.string.LOG_IN_FAILED) +" "+ task.exception!!.localizedMessage)
                    EspressoIdlingResource.decrement()
                }
            }
        return firebaseAuth.currentUser
    }

    override fun resetPassword(email: String) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                    task -> if (task.isSuccessful){
                ToastMessageHelper.createToastMessage(application,"Email reset email sent")
            }
            else{
                ToastMessageHelper.createToastMessage(application,task.exception!!.message.toString())
            }
        }
    }

    override fun logout() {
        EspressoIdlingResource.increment()
        firebaseAuth.signOut()
        loggedOutMutableLiveData.postValue(true)
        EspressoIdlingResource.decrement()
    }

    override fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }

    override fun getLoggedOutMutableLiveData(): MutableLiveData<Boolean> {
        return loggedOutMutableLiveData
    }

    override fun getUpdatedProfileMutableLiveData(): MutableLiveData<Boolean> {
        return updatedProfileMutableLiveData
    }

    override fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
        return userDetailsMutableLiveData
    }

    override fun getFirebaseAuthUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

}







