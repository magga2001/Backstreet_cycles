package com.example.backstreet_cycles

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class FakeUserRepoImpl : UserRepository{

    private var currentUser: Users? = null

    override fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    override fun updateUserDetails(firstName: String, lastName: String): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    override fun updatePassword(password: String, newPassword: String): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    override fun getUserDetails(): Flow<Resource<Users>> {
        TODO("Not yet implemented")
    }

    override fun login(email: String, password: String): Flow<Resource<FirebaseUser?>> {
        TODO("Not yet implemented")
    }

    override fun emailVerification(
        firstName: String,
        lastName: String,
        email: String
    ): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    override fun resetPassword(email: String): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }

    override fun logOut() {
        currentUser = null
    }

    fun getCurrentUser(): Users?
    {
        return currentUser
    }

}