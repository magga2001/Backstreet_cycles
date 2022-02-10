package com.example.backstreet_cycles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class MainActivity : AppCompatActivity() {

    var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkLoading()

        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
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
                if(Tfl.isLoaded)
                {
                    Log.i("Dock_station", Tfl.docks.toString())
                    //THEN GO TO MAIN ACTIVITY
                }
                else
                {
                    checkLoading()
                }

            }
        }.start()
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