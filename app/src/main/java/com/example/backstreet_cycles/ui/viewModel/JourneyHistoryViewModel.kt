package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.backstreet_cycles.data.repository.UserRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.reflect.Type
import javax.inject.Inject

class JourneyHistoryViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context
): ViewModel()  {


    private val mApplication = Contexts.getApplication(applicationContext)
    private val userRepository = UserRepository(mApplication, Firebase.firestore, FirebaseAuth.getInstance())
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var showAlert: MutableLiveData<Boolean> = MutableLiveData(false)


    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

    fun getShowAlertMutableLiveData(): MutableLiveData<Boolean> {
        return showAlert
    }

    fun getJourneyHistory(userDetails: Users):MutableList<List<Locations>> {
        val listLocations = emptyList<List<Locations>>().toMutableList()
        for (journey in userDetails.journeyHistory){
            val serializedObject: String = journey
            listLocations.add(convertJSON(serializedObject))
        }
        return listLocations
    }

    private fun convertJSON(serializedObject: String): List<Locations> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Locations?>?>() {}.type
        return gson.fromJson(serializedObject, type)
    }
}