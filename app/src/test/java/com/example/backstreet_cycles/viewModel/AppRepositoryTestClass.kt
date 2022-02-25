package com.example.backstreet_cycles.viewModel

import com.example.backstreet_cycles.model.AppRepository
import com.example.backstreet_cycles.model.DatabaseInteractor
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AppRepositoryTestClass {

    private lateinit var appRepository: AppRepository
    lateinit var databaseInteractor: DatabaseInteractor
     lateinit var mockFirestore: FirebaseFirestore

    @Before
    fun setUp() {
//        appRepository = Mockito.mock(AppRepository::class.java)
        mockFirestore = Mockito.mock(FirebaseFirestore::class.java)
        databaseInteractor = DatabaseInteractor(mockFirestore)
    }

    @Test
    fun `test create user account`() {
        val user = databaseInteractor.createUser("Tish", "One", "tish@example.com")
        databaseInteractor.addUser(user)
        Mockito.`when`(databaseInteractor.retrieveUser(user)[0].email).thenReturn("tish@example.com")
    }
}