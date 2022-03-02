package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.UserDto
import com.example.backstreet_cycles.model.AppRepository
import com.example.backstreet_cycles.model.DatabaseInteractor
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.lang.Exception
import java.util.concurrent.Executor

class AppRepositoryTestClass {

    lateinit var appRepository: AppRepository
    lateinit var databaseInteractor: DatabaseInteractor
    lateinit var mockFirestore: FirebaseFirestore
    lateinit var  mockFirebaseAuth: FirebaseAuth
    lateinit var application: Application

    @Mock
    private lateinit var successTask: Task<AuthResult>
    @Mock
    private lateinit var failureTask: Task<AuthResult>
    @Mock
    private lateinit var collectionReference: CollectionReference
    @Mock
    private  lateinit var task: Task<DocumentReference>
    @Mock
    private  lateinit var firebaseUser: FirebaseUser
    @Mock
    private  lateinit var taskQuery: Task<QuerySnapshot>
    @Mock
    private lateinit var mutableFirebaseUser : MutableLiveData<FirebaseUser>

    @Mock
    private lateinit var exception: Exception

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        application = Mockito.mock(Application::class.java)
        mockFirestore = mock(FirebaseFirestore::class.java)
        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        appRepository = AppRepository(application,mockFirestore,mockFirebaseAuth, mutableFirebaseUser)
        println(mockFirestore)
        databaseInteractor = DatabaseInteractor(mockFirestore)
        println(databaseInteractor)
        task = object : Task<DocumentReference>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<DocumentReference> {
                return this;
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<DocumentReference> {
                TODO("Not yet implemented")
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<DocumentReference> {
                TODO("Not yet implemented")
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in DocumentReference>): Task<DocumentReference> {
              return this;
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in DocumentReference>
            ): Task<DocumentReference> {
                TODO("Not yet implemented")
            }

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in DocumentReference>
            ): Task<DocumentReference> {
                TODO("Not yet implemented")
            }

            override fun getException(): Exception? {
                return task.exception
            }

            override fun getResult(): DocumentReference {
                return task.result
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): DocumentReference {
                return task.result
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
//        firebaseAuth = Mockito.mock(FirebaseAuth.getInstance());
    }

    @Test
    fun `test create user account returns correct email`() {
//        val task: Task<AuthResult> = Mockito.mock(Task<AuthResult>::class.java)
       //  val firebaseAuth: FirebaseAuth = Mockito.mock(FirebaseAuth::class.java);
//        Mockito.`when`(successTask.isSuccessful).thenReturn(true)
//        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(successTask)
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
//        doAnswer{
//            val listener = it.arguments[0] as OnCompleteListener<QuerySnapshot>
//            listener.onComplete(taskQuery)
//            taskQuery
//        }.`when`(taskQuery).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<QuerySnapshot>>())
//        Mockito.doNothing().`when`(mutableFirebaseUser).postValue(any());
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask)
        val testUser = appRepository.register("example1", "example2", "example@gmail.com","123456")
        if (testUser != null) {
            assert(testUser.email == "example@gmail.com")
        }
        //Mockito.verify(mockFirebaseAuth).createUserWithEmailAndPassword(any(), any())
        //Mockito.verify( userDetailsMutableLiveData).postValue(userDetails)
    }

    @Test
    fun `test create user when error does not return an email`() {
//        val task: Task<AuthResult> = Mockito.mock(Task<AuthResult>::class.java)
        //  val firebaseAuth: FirebaseAuth = Mockito.mock(FirebaseAuth::class.java);
//        Mockito.`when`(successTask.isSuccessful).thenReturn(false)
//        Mockito.`when`(mockFirebaseAuth.createUserWithEmailAndPassword(any(), any())).thenReturn(successTask)
//        doAnswer{
//            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
//            listener.onComplete(successTask)
//            successTask
//        }.`when`(successTask).addOnCompleteListener(ArgumentMatchers.any<OnCompleteListener<AuthResult>>())

        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(failureTask)
        val testUser = appRepository.register("example1", "example2", "example@gmail.com","12345")
        assert(testUser == null)


//        appRepository.register("example1", "example2", "example@gmail.com","12345")
        //Mockito.verify(toastService.show)
    }

    @Test
    fun `test create two users with same email does not work`() {
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask)
        val testUser = appRepository.register("example1", "example2", "example1@gmail.com","123456")
        if (testUser != null) {
            assert(testUser.email == "example1@gmail.com")
        }
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(failureTask)
        val testUser2 = appRepository.register("example1", "example2", "example1@gmail.com","123456")
        assert(testUser2 == null)

    }

    @Test
    fun `test successful sign in with username and password`(){
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask)
        appRepository.register("example1", "example2", "example@gmail.com","123456")
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(successTask)
        val testUser=appRepository.login("example@gmail.com", "123456")
        if (testUser != null) {
            assert(testUser.email=="example@gmail.com")
        }
    }
}
