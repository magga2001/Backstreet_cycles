package com.example.backstreet_cycles.model

import com.example.backstreet_cycles.DTO.UserDto
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseInteractor(firestore: FirebaseFirestore) {
    private val user: UserDto = UserDto()
    private val db = firestore
//    private val userData: DocumentReference

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