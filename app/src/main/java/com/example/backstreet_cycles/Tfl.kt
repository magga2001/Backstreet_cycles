package com.example.backstreet_cycles

import android.util.Log
import com.example.backstreet_cycles.data.Dock
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class Tfl {
    companion object
    {
        val docks = mutableListOf<Dock>()
        private val db = Firebase.firestore

        suspend fun readDock(id : String):Dock?
        {
            var dock: Dock? = null
            val docRef = db.collection("docks").document(id)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        Log.d("Success,Data Found!", "--")
                        dock = document.toObject(Dock::class.java)!!
                        Log.i("Fetching $id", dock.toString())
                    } else {
                        Log.d("Success,Data Not Found!", "No data")

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Fail", "get failed with ", exception)
                }.await()
            return dock
        }
    }
}