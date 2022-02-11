package com.example.backstreet_cycles

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.backstreet_cycles.data.Dock
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashScreenActivity: AppCompatActivity() {

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loadDock()
        //unseed()
    }

    private fun loadActivity()
    {
        Handler(Looper.myLooper()!!).postDelayed({
            Log.d("Success loading", "Loading Main Activity")
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },3000)
    }

    private fun checkLoading() {

        Log.i("COUNT_DOWN", "RUN")
        object: CountDownTimer(3000,1000)
        {
            override fun onTick(p0: Long) {
                Log.i("COUNT_DOWN", "OnTickRUn")
            }

            override fun onFinish() {
                Log.i("COUNT_DOWN", "OnFinishRUn")
                if(Api.isLoaded)
                {
                    for(dock in Api.docks)
                    {
                        seedDock(dock)
                    }

                    readDock()
                    //THEN GO TO MAIN ACTIVITY
                    if(Tfl.docks.isEmpty())
                    {
                        checkLoading()
                    }else
                    {
                        loadActivity()
                    }
                }
                else
                {
                    checkLoading()
                }

            }
        }.start()
    }

    private fun seedDock(dock : Dock)
    {

        Log.i("Dock_station", dock.toString())
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

        // Add a new document with a generated ID
        db.collection("docks").document(dock.id)
            .set(dockStation)
            .addOnSuccessListener { Log.d("Success", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("Fail", "Error writing document", e) }
    }

    private fun loadDock()
    {
        val docRef = db.collection("docks")
        docRef.get()
            .addOnSuccessListener { document ->
                Log.i("Document", document.toString())
                if (document != null && !document.isEmpty) {
                    Log.d("Success,Data Found!", "--")
                    readDock()
                    loadActivity()
                } else {
                    Log.d("Success,Data Not Found!", "Adding data")
                    Api.getDock()
                    checkLoading()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Fail", "get failed with ", exception)
            }
    }

    private fun readDock()
    {
        val docRef = db.collection("docks")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Success,Data Found!", "--")
                    for(doc in document)
                    {
                        Tfl.docks.add(doc.toObject(Dock::class.java))
                        Log.i("Local data", "Success")
                        Log.i("Local data docks #", Tfl.docks.size.toString())
                    }
                } else {
                    Log.d("Success,Data Not Found!", "No data")

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Fail", "get failed with ", exception)
            }
    }

    //Unseed data from Firebase
    private fun unseed()
    {
        //Unseed dock

        val docRef = db.collection("docks")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Success,Data Found!", "Deleting")
                    for(doc in document)
                    {
                        db.collection("docks").document(doc.id)
                            .delete()
                            .addOnSuccessListener { Log.d("DELETE", "DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Log.w("DELETE", "Error deleting document", e) }
                    }
                } else {
                    Log.d("Success,Data Not Found!", "No data")

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Fail", "get failed with ", exception)
            }

        loadActivity()
    }
}