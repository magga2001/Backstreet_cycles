package java.com.example.backstreet_cycles

import android.app.Application
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.initialize
import javax.inject.Inject

class FakeUserRepoImpl @Inject constructor(
    application: Application,
    var fireStore: FirebaseFirestore,
    var fireBaseAuth: FirebaseAuth
) : UserRepositoryImpl(application, fireStore, fireBaseAuth) {

    init {
        val options = FirebaseOptions.Builder()
            .setProjectId("backstreetcyclestesting-61f8e")
            .setApplicationId("1:808206718442:android:c199598c548e6fca628e93")
            .setApiKey("AIzaSyDHMVdka4bwNzSXOKy65GZCQh8ONpHv058")
            .build()
        val secondary = Firebase.app("secondary")
        fireStore = FirebaseFirestore.getInstance(secondary)
        fireBaseAuth = FirebaseAuth.getInstance(secondary)
    }

    override fun login(email: String, password: String): FirebaseUser? {
        return super.login(email, password)
    }

//    val mutableLiveData = MutableLiveData<FirebaseUser>()
//    val appRepository = AppRepository(application, FirebaseFirestore.getInstance(secondary), FirebaseAuth.getInstance(secondary), mutableLiveData)
//    appRepository.register("One","Coconut","more@example.com","123456")

    override fun getUserDetails() {
        super.getUserDetails()
    }

    fun getCurrentUser(): FirebaseUser? {
        return fireBaseAuth.currentUser
    }

}