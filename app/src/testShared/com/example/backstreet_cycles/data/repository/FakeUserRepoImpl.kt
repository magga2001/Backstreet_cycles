package com.example.backstreet_cycles.data.repository

import android.util.Log
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named

class FakeUserRepoImpl @Inject constructor(
    @Named("firstName") firstName: String,
    @Named("lastName") lastName: String,
    @Named("email") email: String,
    @Named("password") password: String,
    locations: MutableList<Locations>
) : UserRepository{

    constructor() : this("", "", "", "", mutableListOf())

    private val PASSWORD_MINIMUM_LENGTH = 6
    private var users: HashMap<String, HashMap<Users, String>> = hashMapOf()
    private var verifiedUser: HashMap<String, Boolean> = hashMapOf()
    private var currentUser: Users? = null
    private val validEmail: String = "@example.com"

    init {
        if(firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && locations.isNotEmpty()){
            EspressoIdlingResource.increment()
            addMockUser(firstName, lastName, email, password, locations)
            EspressoIdlingResource.decrement()
        }
    }

    override fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Resource<String>> = channelFlow{
        EspressoIdlingResource.increment()
        if(password.length < PASSWORD_MINIMUM_LENGTH){
            send(Resource.Error("Password is too short"))
            EspressoIdlingResource.decrement()
        }else{
            if(!users.containsKey(email)){
                val user = Users(firstName,lastName, email)

                users[email] = hashMapOf(
                    user to password
                )

                verifiedUser[email] = false

                send(Resource.Success("Email verification sent"))
                EspressoIdlingResource.decrement()
            }else{
                send(Resource.Error("Email already existed"))
                EspressoIdlingResource.decrement()
            }
        }
    }

    override fun updateUserDetails(firstName: String, lastName: String): Flow<Resource<String>> = flow {
        EspressoIdlingResource.increment()
        if(currentUser != null) {
            val email = currentUser?.email

            if(users.containsKey(email)){
                currentUser?.firstName = firstName
                currentUser?.lastName = lastName
                emit(Resource.Success("Success"))
                EspressoIdlingResource.decrement()
            }else{
                emit(Resource.Error("No User"))
                EspressoIdlingResource.decrement()
            }
        }else{
            emit(Resource.Error("No User"))
            EspressoIdlingResource.decrement()
        }
    }

    override fun updatePassword(password: String, newPassword: String): Flow<Resource<String>> = flow{

        if(currentUser != null) {

            val email = currentUser?.email

            if(users.containsKey(email))
            {
                val user = users[email]

                if(user?.get(currentUser!!) == password && newPassword.length > PASSWORD_MINIMUM_LENGTH){
                    user[currentUser!!] = newPassword
                    emit(Resource.Success("Password successfully updated."))
                }else{
                    emit(Resource.Error("Password update fail"))
                }
            }
        }
    }

    override fun getUserDetails(): Flow<Resource<Users>> = flow{

        EspressoIdlingResource.increment()
        if(currentUser != null){

            val email = currentUser?.email

            if(users.containsKey(email)){
                Log.i("usersss", currentUser!!.email.toString())
                emit(Resource.Success(currentUser!!))
                EspressoIdlingResource.decrement()
            }else{
                emit(Resource.Error("No User"))
            }
        }
    }

    override fun login(email: String, password: String): Flow<Resource<Boolean>> = channelFlow {

        if(users.containsKey(email))
        {
            val user = users[email]
            val existedUser = user?.keys?.first()

            if(verifiedUser[email] == true && user?.get(existedUser) == password){
                currentUser = existedUser
                send(Resource.Success(true))
            }else{
                send(Resource.Error("Please verify your email"))
            }

        }else{
            send(Resource.Error("No User"))
        }
    }

    override fun emailVerification(
        firstName: String,
        lastName: String,
        email: String
    ): Flow<Resource<String>> = flow {
        if(!email.contains(validEmail)){
            users.remove(email)
            emit(Resource.Error("Invalid email, cannot send email"))
        }
    }

    override fun resetPassword(email: String): Flow<Resource<String>> = flow{
        if(users.containsKey(email))
        {
            emit(Resource.Success("Password reset link sent to email."))
        }else{
            emit(Resource.Error("There is no user record corresponding to this identifier. The user may have been deleted."))
        }
    }

    override fun addJourneyToJourneyHistory(
        locations: MutableList<Locations>,
        user: Users
    ): Flow<Resource<String>> = flow {

        if(currentUser != null){

            val email = user.email

            if(users.containsKey(email)){
                val user = users[email]
                val existedUser = user?.keys?.first()

                val gson = Gson()
                val jsonObject = gson.toJson(locations)

                existedUser!!.journeyHistory = mutableListOf(jsonObject)

                emit(Resource.Success("Record added"))
            }
        }
    }

    override fun logOut() {
        currentUser = null
    }

    fun getCurrentUser(): Users?
    {
        return currentUser
    }

    fun verifyEmail(email: String)
    {
        EspressoIdlingResource.increment()
        if(users.containsKey(email)){
            verifiedUser[email] = true
        }
        EspressoIdlingResource.decrement()
    }

    fun addMockUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        locations: MutableList<Locations>
    ){
        EspressoIdlingResource.increment()
        val user = Users(firstName,lastName, email)

        users[email] = hashMapOf(
            user to password
        )
        verifiedUser[email] = true
        currentUser = user

        if(currentUser != null){

            val email = user.email

            if(users.containsKey(email)){
                val user = users[email]
                val existedUser = user?.keys?.first()

                val gson = Gson()
                val jsonObject = gson.toJson(locations)

                existedUser!!.journeyHistory = mutableListOf(jsonObject)
            }
        }

        EspressoIdlingResource.decrement()

    }
}