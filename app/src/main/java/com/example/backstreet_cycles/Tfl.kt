package com.example.backstreet_cycles

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.dto.Dock
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class Tfl {
    companion object
    {
        //The purpose of this list is to iterate through whole list on any query
        val docks = mutableListOf<Dock>()
        private val mutableLiveData: MutableLiveData<Dock> = MutableLiveData()
        private val db = Firebase.firestore

        suspend fun readDock(name : String):Dock?
        {
            var dock: Dock? = null
            val docRef = db.collection("docks").whereEqualTo("name", name)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("Success,Data Found!", "--")

                        for(doc in document)
                        {
                            dock = doc.toObject(Dock::class.java)
                        }

                        Log.i("Fetching $name", dock.toString())
//                        mutableLiveData.postValue(dock)
                    } else {
                        Log.d("Success,Data Not Found!", "No data")

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Fail", "get failed with ", exception)
                }.await()
            return dock
        }

        fun updateDock(dock : Dock)
        {

            Log.i("Dock_station", dock.toString())
            //Reassign value to hashmap dock
            val dockStation = hashMapOf(
                "id" to dock.id,
                "name" to dock.name,
                "lat" to dock.lat,
                "lon" to dock.lon,
                "nbBikes" to dock.nbBikes,
                "nbSpaces" to dock.nbSpaces,
                "nbDocks" to dock.nbDocks,
            )

            // Add a new document with a generated ID
            db.collection("docks").document(dock.id)
                .set(dockStation)
                .addOnSuccessListener { Log.d("Success", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("Fail", "Error writing document", e) }
        }
    }
}