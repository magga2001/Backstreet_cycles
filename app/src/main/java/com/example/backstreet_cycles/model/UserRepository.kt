package com.example.backstreet_cycles.model

import android.app.Application
import android.content.ContentValues.TAG
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.DTO.Users
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber


class
UserRepository(private val application: Application,
                     fireStore: FirebaseFirestore,
                     fireBaseAuth: FirebaseAuth) {
    private val mutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val loggedOutMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val updatedProfileMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val userDetailsMutableLiveData: MutableLiveData<Users> = MutableLiveData()
    private val firebaseAuth: FirebaseAuth = fireBaseAuth
    private val dataBase = fireStore

    init {
        if (firebaseAuth.currentUser != null) {
            mutableLiveData.postValue(firebaseAuth.currentUser)
            loggedOutMutableLiveData.postValue(false)
            updatedProfileMutableLiveData.postValue(false)
        }
    }

    fun register(fName: String, lName: String, email: String, password: String): FirebaseUser? {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    emailVerification(fName,lName,email)

                } else {
                    createToastMessage(application.getString(R.string.REGISTRATION_FAILED) + task.exception)
                }
            }
        return firebaseAuth.currentUser
    }

    private fun emailVerification(fName: String, lName: String, email: String) {
        createToastMessage("EMAIL VERIFICATION BEING SENT TO:  $email")
        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task->
            if (task.isSuccessful) {
                logout()

                createToastMessage("PLEASE VERIFY YOUR EMAIL:  $email")
                //this needs to be relocated
                createUserAccount(fName, lName, email)
                mutableLiveData.postValue(firebaseAuth.currentUser)
            }
            else{
                createToastMessage(application.getString(R.string.REGISTRATION_FAILED) + task.exception)

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

//    suspend fun getUser() : QuerySnapshot{
//        val user = dataBase
//            .collection("users")
//            .whereEqualTo("email", userDetailsMutableLiveData.value!!.email)
//            .get()
//            .await()
//        return user
//    }

    fun updateUserDetails(firstName: String, lastName: String) =
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
                            createToastMessage(e.message)
                        }
                    }

                }
            } else {
                withContext(Dispatchers.Main) {
                    createToastMessage(application.getString(R.string.NO_PERSON_MATCH))
                }
            }
        }

    fun updateEmailAndPassword(password: String, newPassword: String) {

        val credential =
            EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, password)


        firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Timber.tag("value").w("User re-authenticated.")
                val user = FirebaseAuth.getInstance().currentUser
                if (newPassword.isNotEmpty()) {
                    user!!.updatePassword(newPassword).addOnCompleteListener { t ->
                        if (t.isSuccessful) {
                            createToastMessage(application.getString(R.string.PASSWORD_CHANGED))
                        } else {
                            createToastMessage(application.getString(R.string.UPDATE_FAILED))
                        }
                    }
                }
            } else {
                createToastMessage(application.getString(R.string.UPDATE_FAILED))
            }
        }
    }

    fun getUserDetails() {
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
    }

    fun login(email: String, password: String) : FirebaseUser? {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if (firebaseAuth.currentUser?.isEmailVerified == true) {
                        mutableLiveData.postValue(firebaseAuth.currentUser)
                    }
                    else{
                        createToastMessage(application.getString(R.string.LOG_IN_FAILED) + " Please verify your email address")
                    }
                }
                else {
                    createToastMessage(application.getString(R.string.LOG_IN_FAILED) + task.exception)
                }
            }
        return firebaseAuth.currentUser
    }

    fun logout() {
        firebaseAuth.signOut()
        loggedOutMutableLiveData.postValue(true)
    }

    private fun createToastMessage(stringMessage: String?) {
        Toast.makeText(application, stringMessage, Toast.LENGTH_LONG)
            .show()
    }

    fun getMutableLiveData(): MutableLiveData<FirebaseUser> {
        return mutableLiveData
    }

    fun getLoggedOutMutableLiveData(): MutableLiveData<Boolean> {
        return loggedOutMutableLiveData
    }

    fun getUpdatedProfileMutableLiveData(): MutableLiveData<Boolean> {
        return updatedProfileMutableLiveData
    }

    fun getUserDetailsMutableLiveData(): MutableLiveData<Users> {
        return userDetailsMutableLiveData
    }
}







