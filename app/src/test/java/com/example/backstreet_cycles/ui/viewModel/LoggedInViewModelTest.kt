package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.initialize
import org.junit.Assert.*
import java.com.example.backstreet_cycles.FakeUserRepoImpl

class LoggedInViewModelTest(app: Application) {

    private var loggedInViewModel: LoggedInViewModel

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
    private val fakeUserRepoImpl = FakeUserRepoImpl(app, fireStore, fireBaseAuth)

    init {
        loggedInViewModel = LoggedInViewModel(
            tflRepository,
            mapboxRepository,
            cyclistRepository,
            fakeUserRepoImpl,
            app
        )
        Firebase.initialize(app, options, "secondary")
    }

    fun logOut() {
        fakeUserRepoImpl.logout()
    }

}