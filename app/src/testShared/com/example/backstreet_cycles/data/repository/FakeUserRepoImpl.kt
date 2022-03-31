package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserRepoImpl : UserRepository{

    private val PASSWORD_MINIMUM_LENGTH = 6

    private var users: HashMap<String, HashMap<Users, String>> = hashMapOf()
    private var currentUser: Users? = null
    private var isVerified = false

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
                emit(Resource.Success("Registration Successful"))
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
        }
    }

    override fun updatePassword(password: String, newPassword: String): Flow<Resource<String>> = flow{

        if(currentUser != null) {

            val email = currentUser?.email

            if(users.containsKey(email))
            {
                val user = users[email]

                if(user?.get(currentUser!!) == password && newPassword.length < PASSWORD_MINIMUM_LENGTH){
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

            if(user?.get(existedUser) == password){
                currentUser = existedUser
                //Probably doesn't work
                emit(Resource.Success(true))
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
        emit(Resource.Success("Email sent"))
    }

    override fun resetPassword(email: String): Flow<Resource<String>> = flow{
        if(users.containsKey(email))
        {
            emit(Resource.Success("Reset password sent"))
        }else{
            emit(Resource.Error("No user"))
        }
    }

    override fun logOut() {
        currentUser = null
    }

    fun getCurrentUser(): Users?
    {
        return currentUser
    }

    fun verifyEmail()
    {
        isVerified = true
    }

    fun getUsersDb(): HashMap<String, HashMap<Users, String>>
    {
        return users
    }

}