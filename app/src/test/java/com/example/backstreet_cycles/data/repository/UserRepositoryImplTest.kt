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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Executor


@RunWith(MockitoJUnitRunner::class)
class UserRepositoryImplTest{

    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @Mock
    lateinit var mockFirestore: FirebaseFirestore
    @Mock
    lateinit var  mockFirebaseAuth: FirebaseAuth
    @Mock
    private lateinit var taskAuth: Task<AuthResult>
    @Mock
    private lateinit var successAuthTask: Task<AuthResult>
    @Mock
    private lateinit var successVoidTask: Task<Void>
    @Mock
    private lateinit var task: Task<DocumentReference>
    @Mock
    private lateinit var firebaseUser: FirebaseUser

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

            override fun getException(): Exception {
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
                return this
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

            override fun getException(): Exception {
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
    }

    @Test
    fun test_register_account_success()= runBlocking {

        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)
        `when`(successAuthTask.isSuccessful).thenReturn(true)


        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        userRepositoryImpl.register("example1", "example2", "example@gmail.com","123456").test {
            verify(mockFirebaseAuth).createUserWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Success)
            cancel()
        }
    }

    @Test
    fun test_register_when_fail() = runBlocking {
        `when`(successAuthTask.isSuccessful).thenReturn(false)
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        userRepositoryImpl.register("example1", "example2", "example@gmail.com","123456").test {
            verify(mockFirebaseAuth).createUserWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_login_user_success() = runBlocking{
        `when`(successAuthTask.isSuccessful).thenReturn(true)
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)
        `when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.isEmailVerified ).thenReturn(true)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        userRepositoryImpl.login("example@gmail.com","123456").test {
            verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Success)
            cancel()
        }
    }

    @Test
    fun test_login_user_fail_with_unsuccesful_login() = runBlocking{
        `when`(successAuthTask.isSuccessful).thenReturn(false)
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        userRepositoryImpl.login("example@gmail.com","123456").test {
            verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_login_user_fail_with_unverified_email() = runBlocking{
        `when`(successAuthTask.isSuccessful).thenReturn(true)
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        `when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)

        userRepositoryImpl.login("example@gmail.com","123456").test {
            verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_login_user_fail_with_fail_server() = runBlocking{
        `when`(successAuthTask.isSuccessful).thenReturn(true)
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(any(), any())).thenReturn(successAuthTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successAuthTask)
            successAuthTask
        }.`when`(successAuthTask).addOnCompleteListener(any<OnCompleteListener<AuthResult>>())

        `when`(mockFirebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)

        userRepositoryImpl.login("example@gmail.com","123456").test {
            verify(mockFirebaseAuth).signInWithEmailAndPassword(any(), any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_reset_password_success() = runBlocking{
        `when`(successVoidTask.isSuccessful).thenReturn(true)
        `when`(mockFirebaseAuth.sendPasswordResetEmail(any())).thenReturn(successVoidTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<Void>
            listener.onComplete(successVoidTask)
            successVoidTask
        }.`when`(successVoidTask).addOnCompleteListener(any<OnCompleteListener<Void>>())

        userRepositoryImpl.resetPassword("example@gmail.com").test {
            verify(mockFirebaseAuth).sendPasswordResetEmail(any())
            assert(awaitItem() is Resource.Success)
            cancel()
        }
    }

    @Test
    fun test_reset_password_fail() = runBlocking{
        `when`(successVoidTask.isSuccessful).thenReturn(false)
        `when`(mockFirebaseAuth.sendPasswordResetEmail(any())).thenReturn(successVoidTask)

        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<Void>
            listener.onComplete(successVoidTask)
            successVoidTask
        }.`when`(successVoidTask).addOnCompleteListener(any<OnCompleteListener<Void>>())


        userRepositoryImpl.resetPassword("example@gmail.com").test {
            verify(mockFirebaseAuth).sendPasswordResetEmail(any())
            assert(awaitItem() is Resource.Error)
            cancel()
        }
    }

    @Test
    fun test_sign_out(){
        doNothing().`when`(mockFirebaseAuth).signOut()
        userRepositoryImpl.logOut()
        verify(mockFirebaseAuth).signOut()
    }

}