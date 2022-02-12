package com.example.backstreet_cycles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        lifecycleScope.launch {Log.i("Retrieve one data", Tfl.readDock("BikePoints_10").toString()) }
        Log.i("Retrieve data", Tfl.docks.toString())
        val showButtonMenu = findViewById<Button>(R.id.showButtonMenu) as Button
        val popupMenu = PopupMenu(
            this, showButtonMenu
        )
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if(id==R.id.choice1){
                Toast.makeText(this, "Choice 1 clicked!", Toast.LENGTH_SHORT).show()
            }
            else if(id==R.id.choice2){
                Toast.makeText(this, "Choice 2 clicked!", Toast.LENGTH_SHORT).show()
            }
            else if(id==R.id.choice3){
                Toast.makeText(this, "Choice 3 clicked!", Toast.LENGTH_SHORT).show()
            }
            else if(id==R.id.choice4){
                Toast.makeText(this, "Choice 4 clicked!", Toast.LENGTH_SHORT).show()
            }

            false
        }
        showButtonMenu.setOnClickListener() {
            popupMenu.show()
        }
        val location = findViewById<SearchView>(R.id.searchView) as SearchView
        val goButton = findViewById<Button>(R.id.goButton) as Button

        goButton.setOnClickListener { view ->
            Snackbar.make(view, "you entered the location of ${location.query}", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
}