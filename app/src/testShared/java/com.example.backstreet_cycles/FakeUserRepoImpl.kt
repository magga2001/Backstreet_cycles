package java.com.example.backstreet_cycles

import android.app.Application
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.example.backstreet_cycles.domain.utils.ToastMessageHelper
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.initialize
import javax.inject.Inject

class FakeUserRepoImpl @Inject constructor():UserRepositoryImpl()  {

    override fun login(email: String, password: String): FirebaseUser? {
        getFirebaseAuth().signInWithEmailAndPassword(email, password)
        return getFirebaseAuth().currentUser
    }

    override fun logout() {
        getFirebaseAuth().signOut()
    }

    override fun getUserDetails(){
        super.getUserDetails()
    }

     fun getCurrentUser() : FirebaseUser?{
        return getFirebaseAuth().currentUser
    }

}