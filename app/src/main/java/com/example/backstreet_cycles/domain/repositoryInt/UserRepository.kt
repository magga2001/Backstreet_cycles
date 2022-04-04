package com.example.backstreet_cycles.domain.repositoryInt

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun register(firstName: String, lastName: String, email: String, password: String): Flow<Resource<String>>

    fun updateUserDetails(firstName: String, lastName: String): Flow<Resource<String>>

    fun updatePassword(password: String, newPassword: String): Flow<Resource<String>>

    fun getUserDetails(): Flow<Resource<Users>>

    fun login(email: String, password: String): Flow<Resource<Boolean>>

    fun emailVerification(firstName: String, lastName: String, email: String): Flow<Resource<String>>

    fun resetPassword(email: String): Flow<Resource<String>>

    fun addJourneyToJourneyHistory(locations: MutableList<Locations>, user: Users): Flow<Resource<String>>

    fun logOut()
}