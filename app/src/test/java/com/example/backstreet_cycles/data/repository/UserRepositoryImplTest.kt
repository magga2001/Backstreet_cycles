package com.example.backstreet_cycles.data.repository

import android.app.Activity
import app.cash.turbine.test
import com.example.backstreet_cycles.common.Resource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor


class UserRepositoryImplTest{

    private lateinit var userRepositoryImpl: UserRepositoryImpl
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var check: String
    private lateinit var user: FirebaseUser

//    @Mock
//    private lateinit var mockFirestore: FirebaseFirestore
//    @Mock
//    private lateinit var mockFirebaseAuth: FirebaseAuth
//    @Mock
//    private lateinit var task: OnCompleteListener<FirebaseAuth>

    @Mock
    lateinit var mockFirestore: FirebaseFirestore
    @Mock
    lateinit var  mockFirebaseAuth: FirebaseAuth

    @Mock
    private lateinit var taskAuth: Task<AuthResult>
    @Mock
    private lateinit var failiureTask: Task<AuthResult>

    @Mock
    private lateinit var successTask: Task<AuthResult>

    @Mock
    private lateinit var collectionReference: CollectionReference
    @Mock
    private  lateinit var task: Task<DocumentReference>
    @Mock
    private  lateinit var firebaseUser: FirebaseUser
    @Mock
    private  lateinit var taskQuery: Task<QuerySnapshot>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var authResult: Task<AuthResult>

    @Before
    fun setUp(){

//        fireStore = mockk<FirebaseFirestore>()
//        firebaseAuth = mockk<FirebaseAuth>()
//        authResult = mockk<Task<AuthResult>>(relaxed = true)

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
    fun `test create user account`()= runBlocking {

        Mockito.`when`(successTask.isSuccessful).thenReturn(true)
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(successTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successTask)
            successTask
        }.`when`(successTask).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<AuthResult>>())

        Mockito.`when`(mockFirestore.collection(any())).thenReturn(collectionReference)
        Mockito.`when`(collectionReference.add(any())).thenReturn(task);
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        Mockito.`when`(firebaseUser.email).thenReturn("test")
        val queryMock = mock(Query::class.java)
        Mockito.`when`(collectionReference.whereEqualTo(anyString(), any())).thenReturn(queryMock)
        Mockito.`when`(queryMock.get()).thenReturn(taskQuery)
        Mockito.`when`(taskQuery.isSuccessful).thenReturn(false)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<QuerySnapshot>
            listener.onComplete(taskQuery)
            taskQuery
        }.`when`(taskQuery).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<QuerySnapshot>>())

        userRepositoryImpl.register("example1", "example2", "example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).createUserWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Success)
            cancel()
        }
    }

    //Change string...
    //Need modification
    @Test
    fun `test create user when error`() = runBlocking {
        Mockito.`when`(successTask.isSuccessful).thenReturn(false)
        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(successTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successTask)
            successTask
        }.`when`(successTask).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<AuthResult>>())

        Mockito.`when`(mockFirestore.collection(any())).thenReturn(collectionReference)
        Mockito.`when`(collectionReference.add(any())).thenReturn(task);
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        Mockito.`when`(firebaseUser.email).thenReturn("test")
        val queryMock = mock(Query::class.java)
        Mockito.`when`(collectionReference.whereEqualTo(anyString(), any())).thenReturn(queryMock)
        Mockito.`when`(queryMock.get()).thenReturn(taskQuery)
        Mockito.`when`(taskQuery.isSuccessful).thenReturn(false)
        //This line...
//        Mockito.`when`(task.exception!!.localizedMessage).thenReturn("Fail to create account")

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<QuerySnapshot>
            listener.onComplete(taskQuery)
            taskQuery
        }.`when`(taskQuery).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<QuerySnapshot>>())

        userRepositoryImpl.register("example1", "example2", "example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).createUserWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_login_user_success() = runBlocking{
        Mockito.`when`(successTask.isSuccessful).thenReturn(true)
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successTask)
            successTask
        }.`when`(successTask).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<AuthResult>>())

        Mockito.`when`(mockFirestore.collection(any())).thenReturn(collectionReference)
        Mockito.`when`(collectionReference.add(any())).thenReturn(task);
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        Mockito.`when`(firebaseUser.email).thenReturn("test")
        val queryMock = mock(Query::class.java)
        Mockito.`when`(collectionReference.whereEqualTo(anyString(), any())).thenReturn(queryMock)
        Mockito.`when`(queryMock.get()).thenReturn(taskQuery)
        Mockito.`when`(taskQuery.isSuccessful).thenReturn(false)
        Mockito.`when`(mockFirebaseAuth.currentUser?.isEmailVerified ).thenReturn(true)
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<QuerySnapshot>
            listener.onComplete(taskQuery)
            taskQuery
        }.`when`(taskQuery).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<QuerySnapshot>>())

        userRepositoryImpl.login("example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Success)
            cancel()
        }
    }

    @Test
    fun test_login_user_error() = runBlocking{
        Mockito.`when`(successTask.isSuccessful).thenReturn(true)
        Mockito.`when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successTask)
            successTask
        }.`when`(successTask).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<AuthResult>>())

        Mockito.`when`(mockFirestore.collection(any())).thenReturn(collectionReference)
        Mockito.`when`(collectionReference.add(any())).thenReturn(task);
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        Mockito.`when`(firebaseUser.email).thenReturn("test")
        val queryMock = mock(Query::class.java)
        Mockito.`when`(collectionReference.whereEqualTo(anyString(), any())).thenReturn(queryMock)
        Mockito.`when`(queryMock.get()).thenReturn(taskQuery)
        Mockito.`when`(taskQuery.isSuccessful).thenReturn(false)
        Mockito.`when`(mockFirebaseAuth.currentUser?.isEmailVerified ).thenReturn(false)
        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(null)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<QuerySnapshot>
            listener.onComplete(taskQuery)
            taskQuery
        }.`when`(taskQuery).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<QuerySnapshot>>())

        userRepositoryImpl.login("example@gmail.com","123456").test {
            Mockito.verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

//    @Test
//    fun test_reset_password() = runBlocking{
//        Mockito.`when`(successTask.isSuccessful).thenReturn(true)
//        Mockito.`when`(mockFirebaseAuth.sendPasswordResetEmail(any(), any())).thenReturn(successTask)
//
//        doAnswer{
//            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
//            listener.onComplete(successTask)
//            successTask
//        }.`when`(successTask).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<AuthResult>>())
//
//        Mockito.`when`(mockFirestore.collection(any())).thenReturn(collectionReference)
//        Mockito.`when`(collectionReference.add(any())).thenReturn(task);
//        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
//        Mockito.`when`(firebaseUser.email).thenReturn("test")
//        val queryMock = mock(Query::class.java)
//        Mockito.`when`(collectionReference.whereEqualTo(anyString(), any())).thenReturn(queryMock)
//        Mockito.`when`(queryMock.get()).thenReturn(taskQuery)
//        Mockito.`when`(taskQuery.isSuccessful).thenReturn(false)
//        Mockito.`when`(mockFirebaseAuth.currentUser?.isEmailVerified ).thenReturn(true)
//        Mockito.`when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
//
//        doAnswer{
//            val listener = it.arguments[0] as OnCompleteListener<QuerySnapshot>
//            listener.onComplete(taskQuery)
//            taskQuery
//        }.`when`(taskQuery).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<QuerySnapshot>>())
//
//        userRepositoryImpl.resetPassword("example@gmail.com").test {
//            Mockito.verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
//            assert(awaitItem() is Resource.Error)
//            cancel()
//        }
//    }
}