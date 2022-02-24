package com.example.backstreet_cycles.model

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProviders
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.views.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


class AppRepositoryTest {

    private val signUpActivity = SignUpActivity()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loginRegiterViewModel: LogInRegisterViewModel
    private val db = Firebase.firestore
    private val EMAIL = "john@doe.com"
    private val PASSWORD = "john@doe.com"


    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        loginRegiterViewModel = ViewModelProviders.of(signUpActivity).get(LogInRegisterViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    @Test
    fun `test whether the user creates a successful account`() {
        loginRegiterViewModel.register("John", "Doe", EMAIL, PASSWORD)
        assertEquals(firebaseAuth.currentUser, true)
//        var user = db.collection("users")
//            .whereEqualTo("email", firebaseAuth.currentUser!!.email)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents){
//                    assertEquals(document,true)
//                }
//            }
//        assertEquals(user,true)
    }
}