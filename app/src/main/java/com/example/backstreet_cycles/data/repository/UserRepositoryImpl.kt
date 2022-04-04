package com.example.backstreet_cycles.data.repository

import android.content.ContentValues.TAG
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

class UserRepositoryImpl(
    fireAuth: FirebaseAuth,
    fireStore: FirebaseFirestore,
): UserRepository {

    private var firebaseAuth: FirebaseAuth = fireAuth
    private var dataBase: FirebaseFirestore = fireStore

    fun setFirebaseAuth(firebaseAuth: FirebaseAuth){
        this.firebaseAuth = firebaseAuth
    }

    fun setFirebaseFirestore(dataBase: FirebaseFirestore){
        this.dataBase = dataBase
    }

    /**
     * Register user in the Firebase
     *
     * @param firstName
     * @param lastName
     * @param email
     * @param password
     */
    override fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Resource<String>> = callbackFlow {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(Resource.Success("EMAIL VERIFICATION BEING SENT TO:  $email"))

                } else {
                    trySend(Resource.Error<String>(" The email is badly formatted"))
                }
            }

        awaitClose { channel.close() }
    }

    /**
     * Verify user with email
     *
     * @param firstName
     * @param lastName
     * @param email
     */
    override fun emailVerification(
        firstName: String,
        lastName: String,
        email: String
    ): Flow<Resource<String>> = callbackFlow {

        firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                logOut()
                createUserAccount(firstName, lastName, email)
            } else {
                trySend(Resource.Error<String>(task.exception!!.localizedMessage))
            }
        }
        awaitClose { channel.close() }
    }

    /**
     * Create user account in the database
     *
     * @param firstName
     * @param lastName
     * @param email
     */
    private fun createUserAccount(
        firstName: String,
        lastName: String,
        email: String
    ) {
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

    /**
     * Update user's first name and last name
     *
     * @param firstName
     * @param lastName
     */
    override fun updateUserDetails(firstName: String, lastName: String): Flow<Resource<String>> = callbackFlow {
        try {
            EspressoIdlingResource.increment()
        dataBase
            .collection("users")
            .whereEqualTo("email", firebaseAuth.currentUser!!.email)
            .get()
            .addOnSuccessListener { result ->
                for(document in result)
                {
                    dataBase.collection("users").document(document.id)
                        .update("firstName", firstName)
                    dataBase.collection("users").document(document.id)
                        .update("lastName", lastName)
                    dataBase.collection("users").document(document.id)
                        .update("email", firebaseAuth.currentUser!!.email)

                    trySend(Resource.Success("Profile updated successfully"))
                }
            }.addOnFailureListener {
                trySend(Resource.Error<String>("Profile not updated."))
            }
        }catch(e: Exception) {
            trySend(Resource.Error<String>("No user"))
        }finally {
            EspressoIdlingResource.decrement()
        }

        awaitClose { channel.close() }
    }

    /**
     * Update user's password
     *
     * @param password
     * @param newPassword
     */
    override fun updatePassword(password: String, newPassword: String): Flow<Resource<String>> = callbackFlow {

        val credential = EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, password)

        firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (newPassword.isNotEmpty()) {
                    user!!.updatePassword(newPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            trySend(Resource.Success("Password successfully updated."))
                        } else {
                            trySend(Resource.Error<String>("Password update fail"))
                        }
                    }
                }
            } else {
                trySend(Resource.Error<String>("Password update fail"))

            }
        }
        awaitClose { channel.close() }
    }

    /**
     * Retrieve user's details from the database
     */
    override fun getUserDetails(): Flow<Resource<Users>> = callbackFlow {
        EspressoIdlingResource.increment()
        try {
            dataBase
                .collection("users")
                .whereEqualTo("email", firebaseAuth.currentUser!!.email)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val userDetails = document.toObject(Users::class.java)
                            trySend(Resource.Success(userDetails))
                        }
                    }
                }
        } catch(e: Exception) {
            trySend(Resource.Error<Users>("No user"))

        }
        finally {
            EspressoIdlingResource.decrement()
        }

        awaitClose { channel.close() }

    }

    /**
     * Log in user into the application
     *
     * @param email
     * @param password
     */
    override fun login(email: String, password: String): Flow<Resource<Boolean>> = callbackFlow {
        EspressoIdlingResource.increment()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (firebaseAuth.currentUser?.isEmailVerified == true) {

                        if(firebaseAuth.currentUser != null){
                            trySend((Resource.Success(true)))
                        }else{
                            trySend(Resource.Error<Boolean>("Couldn't reach server"))
                        }
                        EspressoIdlingResource.decrement()
                    } else {
                        trySend(Resource.Error<Boolean>("Please verify your email address"))
                        EspressoIdlingResource.decrement()
                    }
                } else {

                    trySend(Resource.Error<Boolean>(
                            " The password is invalid or the user does not have a password")
                    )
                    EspressoIdlingResource.decrement()
                }
            }

        awaitClose { channel.close() }
    }

    /**
     * Reset user's password via email with a link
     *
     * @param email
     */
    override fun resetPassword(email: String): Flow<Resource<String>> = callbackFlow  {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(Resource.Success("Password reset link sent to email."))
            } else {
                trySend(Resource.Error<String>(
                    "There is no user record corresponding to this identifier. The user may have been deleted")
                )
            }
        }

        awaitClose { channel.close() }

    }

    /**
     * Add Json journey object to the particular user's history
     *
     * @param locations - list of journey's locations
     * @param user
     */
    override fun addJourneyToJourneyHistory(locations: MutableList<Locations>, user: Users): Flow<Resource<String>> = callbackFlow  {

        dataBase
            .collection("users")
            .whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { result ->
                val gson = Gson()
                val jsonObject = gson.toJson(locations)
                    if (jsonObject.isNotEmpty()) {
                        user.journeyHistory.add(jsonObject)
                        for (document in result) {
                            try {
                                dataBase.collection("users")
                                    .document(document.id)
                                    .update("journeyHistory", user.journeyHistory)
                                trySend(Resource.Success("Added journey to the record"))
                            } catch (e: Exception) {
                                trySend(Resource.Error<String>("Fail to add journey at this moment"))
                            }
                        }
                    }
                }.addOnFailureListener {
                    trySend(Resource.Error<String>("No user"))
                }

        awaitClose { channel.close() }

    }

    /**
     * Log out user from the application
     */
    override fun logOut() {
        EspressoIdlingResource.increment()
        firebaseAuth.signOut()
        EspressoIdlingResource.decrement()
    }

}







