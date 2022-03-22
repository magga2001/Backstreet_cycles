package com.example.backstreet_cycles.ui.viewModel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.data.repository.UserRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.lang.Exception
import java.util.concurrent.Executor

class UserRepositoryTestClass {

    @Mock
    lateinit var mutableFirebaseUser: MutableLiveData<FirebaseAuth>

    lateinit var userRepository: UserRepository
    @Mock
    lateinit var mockFirestore: FirebaseFirestore
    @Mock
    lateinit var  mockFirebaseAuth: FirebaseAuth
    @Mock
    lateinit var mockApplication: Application
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

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)
//        mockApplication = mock(Application::class.java)
//        mockFirestore = mock(FirebaseFirestore::class.java)
//        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        userRepository = UserRepository(mockApplication,mockFirestore,mockFirebaseAuth)
        println(mockFirestore)

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

    }

    @Test
    fun `test create user account`() {

        `when`(successTask.isSuccessful).thenReturn(true)
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask)
        doAnswer{
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            listener.onComplete(successTask)
            successTask
        }.`when`(successTask).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<AuthResult>>())
//        val testUser = appRepository.register("example1", "example2", "example@gmail.com","12345")
//        if (testUser != null) {
//            assert(testUser.email == "example@gmail.com")
//        }
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
        Mockito.doNothing().`when`(mutableFirebaseUser).postValue(any());
        userRepository.register("example1", "example2", "example@gmail.com","12345")
        verify(mockFirebaseAuth).createUserWithEmailAndPassword(any(), any())
//        assert(verify(task.exception)!!.message == Exception("Exception called").message)
    }

    @Test
    fun `test create user when error`() {

        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(taskAuth)
        val testUser = userRepository.register("example1", "example2", "example@gmail.com","12345")
        if (testUser != null) {
            assert(testUser.email == null)
        }

//        appRepository.register("example1", "example2", "example@gmail.com","12345")
        //Mockito.verify(toastService.show)
    }
}
