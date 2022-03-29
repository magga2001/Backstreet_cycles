package com.example.backstreet_cycles

import android.app.Application
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.initialize
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.com.example.backstreet_cycles.FakeUserRepoImpl

@RunWith(AndroidJUnit4ClassRunner::class)
class SignUpViewModelTest (app: Application) {

    private var signUpViewModel: SignUpViewModel

    private lateinit var tflRepository: TflRepository

    private lateinit var mapboxRepository: MapboxRepository

    private lateinit var cyclistRepository: CyclistRepository

    private val options = FirebaseOptions.Builder()
        .setProjectId("backstreetcyclestesting-61f8e")
        .setApplicationId("1:808206718442:android:c199598c548e6fca628e93")
        .setApiKey("AIzaSyDHMVdka4bwNzSXOKy65GZCQh8ONpHv058")
        .build()

    private val secondary = Firebase.app("secondary")
    private val fireStore = FirebaseFirestore.getInstance(secondary)
    private val fireBaseAuth = FirebaseAuth.getInstance(secondary)
    private val fakeUserRepoImpl = FakeUserRepoImpl()

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val firstName = "Test"
    private val lastName = "User"
    private val password = "123456"


    init {
        Firebase.initialize(app, options, "secondary")
        signUpViewModel = SignUpViewModel(
            tflRepository,
            mapboxRepository,
            cyclistRepository,
            fakeUserRepoImpl,
            app
        )
    }
//
//    @Test
//    fun check_user_is_registered(){
//        fakeUserRepoImpl.register(firstName, lastName, email, password)
//        val currentUser = fakeUserRepoImpl.getCurrentUser()
//        assertNotNull(currentUser)
//    }
}