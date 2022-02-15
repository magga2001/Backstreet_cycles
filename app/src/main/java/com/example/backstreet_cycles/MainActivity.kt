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


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class MainActivity : AppCompatActivity() {

    var mapView: MapView? = null

    var listOfDocks= Tfl.docks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(
            Style.MAPBOX_STREETS,
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    addAnnotationToMap()
                }
            }
        )

        lifecycleScope.launch { Log.i("Retrieve one data",Tfl.readDock("BikePoints_10").toString()) }
        //Log.i("Retrieve data", Tfl.docks.toString())
        Log.i("abc", listOfDocks[0].toString())
        val showButtonMenu = findViewById<Button>(R.id.showButtonMenu) as Button
        val popupMenu = PopupMenu(
            this, showButtonMenu
        )
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == R.id.choice1) {
                Toast.makeText(this, "Choice 1 clicked!", Toast.LENGTH_SHORT).show()
            } else if (id == R.id.choice2) {
                Toast.makeText(this, "Choice 2 clicked!", Toast.LENGTH_SHORT).show()
            } else if (id == R.id.choice3) {
                Toast.makeText(this, "Choice 3 clicked!", Toast.LENGTH_SHORT).show()
            } else if (id == R.id.choice4) {
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
            Snackbar.make(
                view,
                "you entered the location of ${location.query}",
                Snackbar.LENGTH_LONG
            )
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


    private fun addAnnotationToMap() {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            this@MainActivity,
            R.drawable.red_marker
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(Point.fromLngLat(listOfDocks[0].lon, listOfDocks[0].lat))
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }

    }
}