package com.example.backstreet_cycles.model

import android.app.Application
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import timber.log.Timber
import kotlin.concurrent.thread

class TflRepository(private val application: Application) {

    companion object
    {
        val docks = mutableListOf<Dock>()
    }

    private val docksFetch = mutableListOf<Dock>()
    private var isLoaded:Boolean=false
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

    private fun getDock()
    {
        isLoaded =false
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(application)
        val url = application.getString(R.string.tfl_url)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->

                val json = JSONArray(response)
                fetchDocks(json)
            },
            {
                Toast.makeText(application,it.message.toString(), Toast.LENGTH_LONG).show()

                isLoaded =true
            })


        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun fetchDocks(json: JSONArray)
    {
        for(i in 0 until json.length())
        {
            val id = json.getJSONObject(i).getString("id")
            val name = json.getJSONObject(i).getString("commonName")
            val lat = json.getJSONObject(i).getDouble("lat")
            val lon = json.getJSONObject(i).getDouble("lon")
            val nbBikes = checkValidity(json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(6).getString("value"))
            val nbSpaces = checkValidity(json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(7).getString("value"))
            val nbDocks = checkValidity(json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(8).getString("value"))

            //Check if dock is broken
            if(validDock(nbBikes,nbSpaces,nbDocks))
            {
                val dock = Dock(id,name,lat,lon,nbBikes,nbSpaces,nbDocks)
                docksFetch.add(dock)
            }
        }

        isLoaded =true
    }

    private fun validDock(nbBikes: Int, nbSpaces: Int, nbDocks: Int): Boolean
    {
        return (nbDocks - (nbBikes + nbSpaces) == 0)
    }

    private fun checkValidity(value : String): Int
    {
        return try {
            value.toInt()
        } catch (e: Exception) {
            // handler
            0
        }
    }

    private fun checkLoading() {

        object: CountDownTimer(500,250)
        {
            override fun onTick(p0: Long) {
                //Empty
            }

            override fun onFinish() {
                if(isLoaded)
                {
                    thread(start = true) {
                        for(dock in docksFetch)
                        {
                            runBlocking {addDock(dock)}
                        }

                        runBlocking{readDocks()}
                    }
                }
                else
                {
                    checkLoading()
                }

            }
        }.start()
    }

    suspend fun addDock(dock : Dock)
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

        // Add a new document with a generated ID
        db.collection("dockstation").document(dock.id)
            .set(dockStation)
            .addOnSuccessListener { Timber.d("Success, DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Timber.d("Fail -- Error writing document $e")}
            .await()
    }

    fun loadDock()
    {
        val docRef = db.collection("dockstation")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && !document.isEmpty) {
                    Timber.d("Success Data Found --")
                    runBlocking {readDocks()}
                } else {
                    Timber.d("Fail -- Data Not Found")
                    getDock()
                    checkLoading()
                }
            }
            .addOnFailureListener { exception ->
                Timber.d("Fail, get failed with $exception")
            }
    }

    private suspend fun readDocks()
    {
        val docRef = db.collection("dockstation")
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

        //loadActivity()
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

    fun getDockMutableLiveData(): MutableLiveData<Dock> {
        return dockMutableLiveData
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean> {
        return isReadyMutableLiveData
    }

    /**
     * UnSeed data from Firebase (Debugging purpose)
     */
    private fun unSeed()
    {
        //UnSeed dock
        val docRef = db.collection("docks")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Timber.d("Success,Data Found! -- Deleting")
                    for(doc in document)
                    {
                        db.collection("docks").document(doc.id)
                            .delete()
                            .addOnSuccessListener { Timber.d("DocumentSnapshot successfully deleted!") }
                            .addOnFailureListener { e -> Timber.d("Error deleting document $e") }
                    }
                } else {
                    Timber.d("Success,Data Not Found! -- No data")

                }
            }
            .addOnFailureListener { exception ->
                Timber.d("Fail get failed with $exception")
            }
    }
}