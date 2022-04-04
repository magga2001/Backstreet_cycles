package com.example.backstreet_cycles.data.repository

import android.app.Activity
import app.cash.turbine.test
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.common.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor


@RunWith(MockitoJUnitRunner::class)
class UserRepositoryImplTest{

    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @Mock
    lateinit var mockFirestore: FirebaseFirestore
    @Mock
    lateinit var  mockFirebaseAuth: FirebaseAuth
//    @Mock
//    lateinit var mockEmailAuthProvider: EmailAuthProvider
//    @Mock
//    lateinit var authCredential: AuthCredential
    @Mock
    private lateinit var taskAuth: Task<AuthResult>
    @Mock
    private lateinit var successAuthTask: Task<AuthResult>
    @Mock
    private lateinit var successVoidTask: Task<Void>
//    @Mock
//    private lateinit var collectionReference: CollectionReference
    @Mock
    private lateinit var task: Task<DocumentReference>
    @Mock
    private lateinit var firebaseUser: FirebaseUser
//    @Mock
//    private lateinit var query: Query
//    @Mock
//    private lateinit var taskQuery: Task<QuerySnapshot>
//    @Mock
//    private lateinit var document: QueryDocumentSnapshot
//    @Mock
//    private lateinit var espressoIdlingResource: EspressoIdlingResource
//    @Mock
//    private lateinit var exception: Exception

    @Before
    fun setUp(){

        MockitoAnnotations.initMocks(this)

        mockFirestore = mock(FirebaseFirestore::class.java)
        mockFirebaseAuth = mock(FirebaseAuth::class.java)

        taskAuth = object : Task<AuthResult>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {
                return this
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<AuthResult> {
                return this
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<AuthResult> {
                return this
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {
                return this
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in AuthResult>
            ): Task<AuthResult> {
                return this
            }

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in AuthResult>
            ): Task<AuthResult> {
                return this
            }

            override fun getException(): Exception? {
                return Exception("Exception")
            }

            override fun getResult(): AuthResult {
                return this.result
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {
                return this.result
            }

            override fun isCanceled(): Boolean {
                return true
            }

            override fun isComplete(): Boolean {
                return true
            }

            override fun isSuccessful(): Boolean {
                return true
            }

        }

        task = object : Task<DocumentReference>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<DocumentReference> {
                return this;
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<DocumentReference> {
                return this
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<DocumentReference> {
                return this
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in DocumentReference>): Task<DocumentReference> {
                return this
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in DocumentReference>
            ): Task<DocumentReference> {
                return this
            }

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in DocumentReference>
            ): Task<DocumentReference> {
                return this
            }

            override fun getException(): Exception? {
                return Exception("Exception called")
            }

            override fun getResult(): DocumentReference {
                return this.result
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): DocumentReference {
                return this.result
            }

            override fun isCanceled(): Boolean {
                return true
            }

            override fun isComplete(): Boolean {
                return true
            }

            override fun isSuccessful(): Boolean {
                return true
            }

        }

        userRepositoryImpl = UserRepositoryImpl(mockFirebaseAuth, mockFirestore)
//        userRepositoryImpl.setFirebaseAuth(mockFirebaseAuth)
//        userRepositoryImpl.setFirebaseFirestore(mockFirestore)
    }

    @Test
    fun test_register_account_success()= runBlocking {

        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)
        Mockito.`when`(successAuthTask.isSuccessful).thenReturn(true)


        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        userRepositoryImpl.register("example1", "example2", "example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).createUserWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Success)
            cancel()
        }
    }

    //Error due to string
    @Test
    fun test_register_when_fail() = runBlocking {
        Mockito.`when`(successAuthTask.isSuccessful).thenReturn(false)
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        userRepositoryImpl.register("example1", "example2", "example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).createUserWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_login_user_success() = runBlocking{
        Mockito.`when`(successAuthTask.isSuccessful).thenReturn(true)
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        Mockito.`when`(firebaseUser.isEmailVerified ).thenReturn(true)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        userRepositoryImpl.login("example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Success)
            cancel()
        }
    }

    @Test
    fun test_login_user_fail_with_unsuccesful_login() = runBlocking{
        Mockito.`when`(successAuthTask.isSuccessful).thenReturn(false)
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        userRepositoryImpl.login("example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_login_user_fail_with_unverified_email() = runBlocking{
        Mockito.`when`(successAuthTask.isSuccessful).thenReturn(true)
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(null)

        userRepositoryImpl.login("example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_login_user_fail_with_fail_server() = runBlocking{
        Mockito.`when`(successAuthTask.isSuccessful).thenReturn(true)
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(null)

        userRepositoryImpl.login("example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_reset_password_success() = runBlocking{
        Mockito.`when`(successVoidTask.isSuccessful).thenReturn(true)
        Mockito.`when`(mockFirebaseAuth.sendPasswordResetEmail(any())).thenReturn(successVoidTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<Void>
            listener.onComplete(successVoidTask)
            successVoidTask
        }.`when`(successVoidTask).addOnCompleteListener(any<OnCompleteListener<Void>>())

        userRepositoryImpl.resetPassword("example@gmail.com").test {
            Mockito.verify(mockFirebaseAuth).sendPasswordResetEmail(any())
            assert(awaitItem() is Resource.Success)
            cancel()
        }
    }

    @Test
    fun test_reset_password_fail() = runBlocking{
        Mockito.`when`(successVoidTask.isSuccessful).thenReturn(false)
        Mockito.`when`(mockFirebaseAuth.sendPasswordResetEmail(any())).thenReturn(successVoidTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<Void>
            listener.onComplete(successVoidTask)
            successVoidTask
        }.`when`(successVoidTask).addOnCompleteListener(any<OnCompleteListener<Void>>())


        userRepositoryImpl.resetPassword("example@gmail.com").test {
            Mockito.verify(mockFirebaseAuth).sendPasswordResetEmail(any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }


//    //Fail atm
//    @Test
//    fun test_email_verification_successful() = runBlocking{
//
//        Mockito.`when`(successVoidTask.isSuccessful).thenReturn(true)
//        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
//        Mockito.`when`(mockFirebaseAuth.currentUser!!.sendEmailVerification()).thenReturn(successVoidTask)
//        Mockito.`when`(taskQuery.isSuccessful).thenReturn(false)
//        doNothing().`when`(mockFirebaseAuth).signOut()
//
//        doAnswer{
//            val listener = it.arguments[0] as OnCompleteListener<Void>
//            listener.onComplete(successVoidTask)
//            successVoidTask
//        }.`when`(successVoidTask).addOnCompleteListener(any<OnCompleteListener<Void>>())
//
//        Mockito.`when`(mockFirestore.collection(any())).thenReturn(collectionReference)
//        Mockito.`when`(collectionReference.add(any())).thenReturn(task);
//        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
//        Mockito.`when`(firebaseUser.email).thenReturn("test")
//        val queryMock = mock(Query::class.java)
//        Mockito.`when`(collectionReference.whereEqualTo(anyString(), any())).thenReturn(queryMock)
//        Mockito.`when`(queryMock.get()).thenReturn(taskQuery)
//
//        doAnswer{
//            val listener = it.arguments[0] as OnCompleteListener<QuerySnapshot>
//            listener.onComplete(taskQuery)
//            taskQuery
//        }.`when`(taskQuery).addOnCompleteListener(any<OnCompleteListener<QuerySnapshot>>())
//
//
//        userRepositoryImpl.emailVerification("example1", "example2", "example@gmail.com").test {
//            Mockito.verify(mockFirebaseAuth.currentUser)?.sendEmailVerification()
//            assert(awaitItem() is Resource.Success)
//            cancel()
//        }
//    }

//    //Fail atm
//    @Test
//    fun test_email_verification_fail() = runBlocking{
//    }

    @Test
    fun test_sign_out(){
        doNothing().`when`(mockFirebaseAuth).signOut()
        userRepositoryImpl.logOut()
        Mockito.verify(mockFirebaseAuth).signOut()
    }

//    //TODO
//    @Test
//    fun test_update_password_success() = runBlocking{
////
////        mockStatic(EmailAuthProvider::class.java).use { theMock ->
////            theMock.`when`<Any>(EmailAuthProvider::getCredential(any(), any())).thenReturn("Rafael")
////            assertThat(Buddy.name()).isEqualTo("Rafael")
////        }
//
////        val mockEmailAuthProvider = mockStatic(EmailAuthProvider::class.java)
//
//        Mockito.`when`(successVoidTask.isSuccessful).thenReturn(true)
//        Mockito.`when`(mockFirebaseAuth.sendPasswordResetEmail(any())).thenReturn(successVoidTask)
//        Mockito.`when`(EmailAuthProvider.getCredential("example@gmail.com", "123456")).thenReturn(authCredential)
//        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
//        Mockito.`when`(firebaseUser.reauthenticate(authCredential)).thenReturn(successVoidTask)
//        Mockito.`when`(firebaseUser.updatePassword(any())).thenReturn(successVoidTask)
//
//        doAnswer{
//            val listener = it.arguments[0] as OnCompleteListener<Void>
//            listener.onComplete(successVoidTask)
//            successVoidTask
//        }.`when`(successVoidTask).addOnCompleteListener(any<OnCompleteListener<Void>>())
//
//        userRepositoryImpl.updatePassword("123456","1234567").test {
//            Mockito.verify(firebaseUser).updatePassword("1234567")
//            assert(awaitItem() is Resource.Success)
//            cancel()
//        }
//    }

//    //TODO
//    @Test
//    fun test_update_password_fail(){
//
//    }


//    //TODO
//    @Test
//    fun test_get_user_detail_success() =  runBlocking{
//
//        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
//        Mockito.`when`(firebaseUser.email).thenReturn("example@gmail.com")
//
//        Mockito.`when`(mockFirestore.collection(any())).thenReturn(collectionReference)
//        Mockito.`when`(mockFirestore.collection(any()).whereEqualTo(anyString(), any())).thenReturn(query)
//        Mockito.`when`(query.get()).thenReturn(taskQuery)
//        Mockito.`when`(taskQuery.isSuccessful).thenReturn(true)
//        doNothing().`when`(espressoIdlingResource).increment()
////        doNothing().`when`(document).toObject(Users::class.java)
////        Mockito.`when`(taskQuery.result).thenReturn()
//
//        doAnswer{
//            val listener = it.arguments[0] as OnCompleteListener<QuerySnapshot>
//            listener.onComplete(taskQuery)
//            taskQuery
//        }.`when`(taskQuery).addOnCompleteListener(any<OnCompleteListener<QuerySnapshot>>())
//
//
//        userRepositoryImpl.getUserDetails().test{
//            Mockito.verify(mockFirestore)
//                .collection("users")
//                .whereEqualTo("email", "example@gmail.com")
//                .get()
//            assert(awaitItem() is Resource.Success)
//            cancel()
//        }
//    }

//    //TODO
//    @Test
//    fun test_get_user_detail_fail() =  runBlocking {
//
//    }
}