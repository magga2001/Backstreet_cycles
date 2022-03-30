package com.example.backstreet_cycles.ui.viewModel
//
//import android.app.Application
//import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
//import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
//import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
//import com.google.firebase.FirebaseOptions
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.ktx.app
//import com.google.firebase.ktx.initialize
//import org.junit.Assert.*
//import org.junit.Test
//import java.com.example.backstreet_cycles.FakeUserRepoImpl
//
//class LoggedInViewModelTest(app: Application) {
//
//    private var loggedInViewModel: LoggedInViewModel
//
//    private lateinit var tflRepository: TflRepository
//
//    private lateinit var mapboxRepository: MapboxRepository
//
//    private lateinit var cyclistRepository: CyclistRepository
//
//    private val fakeUserRepoImpl = FakeUserRepoImpl()
//
//    init {
//        loggedInViewModel = LoggedInViewModel(
//            tflRepository,
//            mapboxRepository,
//            cyclistRepository,
//            fakeUserRepoImpl,
//            app
//        )
//    }
//
//    fun getUserDetails(): FirebaseUser? {
//        return fakeUserRepoImpl.getCurrentUser()
//    }
//
//    @Test
//    fun check_user_is_logged_out(){
//        fakeUserRepoImpl.logout()
//        val currentUser = fakeUserRepoImpl.getCurrentUser()
//        assertNull(currentUser)
//    }
//}