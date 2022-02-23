package com.example.backstreet_cycles.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.dto.Dock
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class TflRepository(private val application: Application) {

    companion object
    {
        val docks = mutableListOf<Dock>()
    }

    private val dockMutableLiveData: MutableLiveData<Dock>
    private val docksMutableLiveData: MutableLiveData<List<Dock>>
    private val isReadyMutableLiveData: MutableLiveData<Boolean>
    private val db = Firebase.firestore

    init{
        dockMutableLiveData = MutableLiveData()
        docksMutableLiveData = MutableLiveData()
        isReadyMutableLiveData = MutableLiveData()
        isReadyMutableLiveData.value = false
    }

    fun loadDock()
    {
        val docRef = db.collection("docks")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && !document.isEmpty) {
                    Timber.d("Success Data Found --")
                    runBlocking {readDocks()}
                } else {
                    Timber.d("Fail -- Data Not Found")
                }
            }
            .addOnFailureListener { exception ->
                Timber.d("Fail, get failed with $exception")
            }
    }

    suspend fun updateDock(dock : Dock)
    {
        //Create dock
        val dockStation = hashMapOf(
            "id" to dock.id,
            "name" to dock.name,
            "lat" to dock.lat,
            "lon" to dock.lon,
            "nbBikes" to dock.nbBikes,
            "nbSpaces" to dock.nbSpaces,
            "nbDocks" to dock.nbDocks,
        )

        // Override/Add a  document with a generated ID
        db.collection("docks").document(dock.id)
            .set(dockStation)
            .addOnSuccessListener { Timber.d("Success, DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Timber.d("Fail -- Error writing document $e")}
            .await()
    }

    private suspend fun readDocks()
    {
        val docRef = db.collection("docks")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Timber.d("Success Data Found --")
                    for(doc in document)
                    {
                        docks.add(doc.toObject(Dock::class.java))
                    }
                    docksMutableLiveData.postValue(docks)
                } else {
                    Timber.d("Success,Data Not Found! -- No data")

                }
            }
            .addOnFailureListener { exception ->
                Timber.d("Fail, get failed with $exception")
            }.await()

        isReadyMutableLiveData.postValue(true)
    }

    suspend fun readADock(name : String): Dock?
    {
        var dock: Dock? = null
        val docRef = db.collection("docks").whereEqualTo("name", name)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Timber.d("Success Data Found --")

                    for (doc in document) {
                        dock = doc.toObject(Dock::class.java)
                    }
//                    dockMutableLiveData.postValue(dock!!)
                } else {
                    Timber.d("Success,Data Not Found!, No data")

                }
            }
            .addOnFailureListener { exception ->
                Timber.d("Fail, get failed with $exception")
            }.await()
        return dock
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean> {
        return isReadyMutableLiveData
    }
}