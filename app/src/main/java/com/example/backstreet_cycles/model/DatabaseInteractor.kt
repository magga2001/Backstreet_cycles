package com.example.backstreet_cycles.model

import com.example.backstreet_cycles.DTO.UserDto
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DatabaseInteractor(firestore: FirebaseFirestore) {
    val user: UserDto
    val db: FirebaseFirestore = firestore
//    private val userData: DocumentReference

    init {
        user = UserDto()
    }

    fun createUser(firstName: String, lastName: String, email: String): UserDto {
        user.firstName = firstName
        user.lastName = lastName
        user.email = email
        return user
    }

    fun addUser(user: UserDto) {
        db.collection("users")
            .add(user)
    }

    fun retrieveUser(user: UserDto): MutableList<UserDto> {
        return db.collection("users")
            .whereEqualTo("email", user.email)
            .get()
            .result
            .toObjects(UserDto::class.java)
    }

//    fun deleteUser(user: UserDto) {
//        val usr = db.collection("users")
//            .whereEqualTo("email",user.email)
//            .get()
//
//        db.collection("users")
//            .document(usr.)
//    }




}