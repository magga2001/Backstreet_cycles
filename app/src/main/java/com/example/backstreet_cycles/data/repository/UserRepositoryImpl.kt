package com.example.backstreet_cycles.data.repository

import android.content.ContentValues.TAG
import android.util.Log
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


class UserRepositoryImpl : UserRepository {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var dataBase: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun setFirebaseAuth(firebaseAuth: FirebaseAuth){
        this.firebaseAuth = firebaseAuth
    }

    fun setFirebaseFirestore(dataBase: FirebaseFirestore){
        this.dataBase = dataBase
    }

    override fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(Resource.Success("EMAIL VERIFICATION BEING SENT TO:  $email"))
                } else {
                    trySend(Resource.Error<String>(task.exception!!.localizedMessage))
                }
            }

        awaitClose { channel.close() }
    }

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
                trySend(Resource.Success("Profile not updated."))
            }
        }catch(e: Exception) {
            trySend(Resource.Error<String>("No user"))
        }finally {
            EspressoIdlingResource.decrement()
        }

        awaitClose { channel.close() }
    }

    override fun updatePassword(password: String, newPassword: String): Flow<Resource<String>> = callbackFlow {

        val credential =
            EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, password)

        firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Timber.tag("value").w("User re-authenticated.")
                val user = FirebaseAuth.getInstance().currentUser
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
                            EspressoIdlingResource.decrement()
                        }
                    }
                }
        } catch(e: Exception) {
            trySend(Resource.Error<Users>("No user"))
                        EspressoIdlingResource.decrement()

        }
//        finally {
//            EspressoIdlingResource.decrement()
//        }

        awaitClose { channel.close() }

    }

    override fun login(email: String, password: String): Flow<Resource<Boolean>> = callbackFlow {
        EspressoIdlingResource.increment()
        trySend(Resource.Loading())
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

                    trySend(Resource.Error<Boolean>(task.exception!!.localizedMessage))
                    EspressoIdlingResource.decrement()
                }
            }

        awaitClose { channel.close() }
    }

    override fun resetPassword(email: String): Flow<Resource<String>> = callbackFlow  {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(Resource.Success("Password reset link sent to email."))
            } else {
                trySend(Resource.Error<String>(task.exception!!.message.toString()))
            }
        }

        awaitClose { channel.close() }

    }

    override fun addJourneyToJourneyHistory(locations: MutableList<Locations>, user: Users): Flow<Resource<String>> = callbackFlow  {

        dataBase
            .collection("users")
            .whereEqualTo("email", firebaseAuth.currentUser!!.email)
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
                                Log.i("running", "history")
                                trySend(Resource.Success("Added journey to the record"))
                            } catch (e: Exception) {
                                trySend(Resource.Error("Fail to add journey at this moment"))
                            }
                        }
                    }
                }.addOnFailureListener {
                    trySend(Resource.Error<String>("No user"))
                }

        awaitClose { channel.close() }

    }

    override fun logOut() {
        EspressoIdlingResource.increment()
        firebaseAuth.signOut()
        EspressoIdlingResource.decrement()
    }
}







