package com.example.backstreet_cycles

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_journey.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt


class Journey : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)

        val distanceText = findViewById<TextView>(R.id.distanceText)
        // King's college strand campus to Guy''s hospital.
        val distance: Float = calculateDistance(51.51156652601671, -0.11599699999999999, 51.50323772692396, -0.08685256399299227)
        distanceText.setText(distance.toString() + " miles")

        goButton.setOnClickListener {
            val intent = Intent(this, Journey_on_map::class.java)
            startActivity(intent)
        }
    }

    /**
     * This function calculates the distance between the starting point and
     * the end point of the journey in miles.
     */
    private fun calculateDistance(latA: Double, lngA: Double, latB: Double, lngB: Double): Float {
        val origin = Location("locationA")
        origin.setLatitude(latA)
        origin.setLongitude(lngA)

        val destination = Location("locationA")
        destination.setLatitude(latB)
        destination.setLongitude(lngB)

        // Need to think of a solution to round it.
        return origin.distanceTo(destination) / 1000
    }

}











