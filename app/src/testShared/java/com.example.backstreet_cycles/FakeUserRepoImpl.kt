package java.com.example.backstreet_cycles

import android.app.Application
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import javax.inject.Inject

class FakeUserRepoImpl @Inject constructor():UserRepositoryImpl()  {

    val context = Application()

//    override fun login(email: String, password: String): FirebaseUser? {
//        getFirebaseAuth().signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful){
//                    if (getFirebaseAuth().currentUser?.isEmailVerified == true) {
//                        getMutableLiveData().postValue(getFirebaseAuth().currentUser)
////                        WorkHelper.setPeriodicallySendingLogs(context)
//                    }
//                    else{
//                        ToastMessageHelper.createToastMessage(context,context.getString(R.string.LOG_IN_FAILED) + " Please verify your email address")
//                    }
//                }
//                else {
//                    ToastMessageHelper.createToastMessage(context,context.getString(R.string.LOG_IN_FAILED) +" "+ task.exception!!.localizedMessage)
//                }
//            }
//        return getFirebaseAuth().currentUser
//    }
//
//    override fun logout() {
//        getFirebaseAuth().signOut()
//        getLoggedOutMutableLiveData().postValue(true)
//    }

//    override fun getUserDetails(){
//        super.getUserDetails()
//    }

//     fun getCurrentUser() : FirebaseUser?{
//        return getFirebaseAuth().currentUser
//    }

}