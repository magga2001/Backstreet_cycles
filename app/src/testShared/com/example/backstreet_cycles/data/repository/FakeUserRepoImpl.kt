package com.example.backstreet_cycles.data.repository

import android.util.Log
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.utils.JsonHelper
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeUserRepoImpl @Inject constructor() : UserRepository{

    private val PASSWORD_MINIMUM_LENGTH = 6

    private var users: HashMap<String, HashMap<Users, String>> = hashMapOf()
    private var verifiedUser: HashMap<String, Boolean> = hashMapOf()
    private var currentUser: Users? = null
    private val validEmail: String = "@example.com"

    override fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Resource<String>> = flow{

        if(password.length < PASSWORD_MINIMUM_LENGTH){
            emit(Resource.Error("Password is too short"))
        }else{
            if(!users.containsKey(email)){
                val user = Users(firstName,lastName, email)

                users[email] = hashMapOf(
                    user to password
                )

                verifiedUser[email] = false

                emit(Resource.Success("Email verification sent"))
            }else{
                emit(Resource.Error("Email already existed"))
            }
        }
    }

    override fun updateUserDetails(firstName: String, lastName: String): Flow<Resource<String>> = flow {

        if(currentUser != null) {
            val email = currentUser?.email

            if(users.containsKey(email)){
                currentUser?.firstName = firstName
                currentUser?.lastName = lastName
                emit(Resource.Success("Success"))
            }else{
                emit(Resource.Error("No user"))
            }
        }else{
            emit(Resource.Error("No user"))
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
                    emit(Resource.Success("Password updated Successfully"))
                }else{
                    emit(Resource.Error("Fail to update password"))
                }
            }
        }
    }

    override fun getUserDetails(): Flow<Resource<Users>> = flow{

        if(currentUser != null){

            val email = currentUser?.email

            if(users.containsKey(email)){
                emit(Resource.Success(currentUser!!))
            }else{
                emit(Resource.Error("No user"))
            }
        }
    }

    override fun login(email: String, password: String): Flow<Resource<Boolean>> = flow {

        if(users.containsKey(email))
        {
            val user = users[email]
            val existedUser = user?.keys?.first()

            if(verifiedUser[email] == true && user?.get(existedUser) == password){
                currentUser = existedUser
                emit(Resource.Success(true))
            }else{
                emit(Resource.Error("Please verify your email"))
            }

        }else{
            emit(Resource.Error("No user"))
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
            emit(Resource.Success("Reset password sent"))
        }else{
            emit(Resource.Error("No user"))
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
        if(users.containsKey(email)){
            verifiedUser[email] = true
        }
    }

    fun addMockUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ){
        val user = Users(firstName,lastName, email)

        users[email] = hashMapOf(
            user to password
        )
        verifiedUser[email] = true
        currentUser = user

    }
}