package com.example.backstreet_cycles.ui.viewModels
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
//import org.junit.runner.RunWith
//import org.junit.runners.JUnit4
//import java.com.example.backstreet_cycles.FakeUserRepoImpl
//
//
//@RunWith(JUnit4::class)
//class LogInViewModelTest(app: Application){
//
//    private var logInViewModel: LogInViewModel
//
//    private lateinit var tflRepository: TflRepository
//
//    private lateinit var mapboxRepository: MapboxRepository
//
//    private lateinit var cyclistRepository: CyclistRepository
//
//    private val fakeUserRepoImpl = FakeUserRepoImpl()
//
//    private val email = "backstreet.cycles.test.user@gmail.com"
//    private val password = "123456"
//
//    init{
//        logInViewModel = LogInViewModel(tflRepository,
//            mapboxRepository,
//            cyclistRepository,
//            fakeUserRepoImpl,
//            app
//            )
//    }
//
//    fun getUserDetails(): FirebaseUser? {
//        return fakeUserRepoImpl.getCurrentUser()
//    }
//
////    @Test
////    fun fetch_test_profile_from_database(){
////        fakeUserRepoImpl.login(email,password)
////        val currentUser = fakeUserRepoImpl.getCurrentUser()
////        assertNotNull(currentUser)
////        assertEquals(currentUser?.email, email)
////    }
//}