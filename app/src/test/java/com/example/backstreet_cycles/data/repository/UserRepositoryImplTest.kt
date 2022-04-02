package com.example.backstreet_cycles.data.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.mockito.Mock


class UserRepositoryImplTest{

    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var check: String
    private lateinit var user: FirebaseUser

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore
    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth
    @Mock
    private lateinit var task: OnCompleteListener<FirebaseAuth>


    @Before
    fun setUp(){
        userRepositoryImpl = UserRepositoryImpl()
        userRepositoryImpl.setFirebaseAuth(mockFirebaseAuth)
        userRepositoryImpl.setFirebaseFirestore(mockFirestore)
    }

//        @Test
//        fun `test if the user can register successfully`() {
//            Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password))
//                .thenReturn(NotNull())
//        }
//        = runBlocking{
//            userRepositoryImpl.register("John","Doe","johndoe@example.com","123456")
//                .onEach{ Log.i("Flow..", it.toString()) }.test {
//                        assert(awaitItem() is Resource.Success)
//                        awaitComplete()
//                        }
//        }
}