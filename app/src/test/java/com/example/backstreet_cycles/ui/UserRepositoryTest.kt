//package com.example.backstreet_cycles.ui
//
//import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.firestore.FirebaseFirestore
//import org.junit.Before
//
//
//class UserRepositoryTest {
//
//    private lateinit var userRepositoryImpl: UserRepositoryImpl
//    private lateinit var firstName: String
//    private lateinit var lastName: String
//    private lateinit var email: String
//    private lateinit var password: String
//    private lateinit var check: String
//    private lateinit var user: FirebaseUser
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var firebaseAuth: FirebaseAuth
//
//    @Before
//    fun setUp(){
//        firstName = "Tihomir"
//        lastName = "Stefanov"
//        email = "stefanovtih02@gmail.com"
//        password = "123456"
//        check = ""
//
////        val app = FirebaseApp.initializeApp()
//        firebaseAuth = FirebaseAuth.getInstance()
//        firestore = FirebaseFirestore.getInstance()
//        userRepositoryImpl = UserRepositoryImpl(firestore,firebaseAuth)
//        userRepositoryImpl.register(firstName,lastName, email, password)
//    }

//    @Test
//    fun `test if the user can be registered successfully with a real email`() {
//
////        user.delete()
////        val firestoreUser = firestore
////            .collection("users")
////            .whereEqualTo("email",email)
////            .get()
////            .await()
////
////        firestore.collection("user")
////            .document(firestoreUser.documents[0].id)
////            .delete()
////            .await()
//
//
//        userRepositoryImpl.register(firstName,lastName, email, password).onEach { result ->
//            when (result) {
//                is Resource.Success -> {
//                    print("True")
//                }
//
//                is Resource.Error -> {
//                    print("False")
//                }
//                is Resource.Loading -> {
//
//                }
//            }
//        }.launchIn(MainScope())
//            print(check)
//        assert(check == "Test")
//    }

//    @After
//    suspend fun tearDown(){
//        user.delete()
//        val firestoreUser = firestore
//            .collection("users")
//            .whereEqualTo("email",email)
//            .get()
//            .await()
//
//        firestore.collection("user").document(firestoreUser.documents[0].id).delete()
//    }

//}


