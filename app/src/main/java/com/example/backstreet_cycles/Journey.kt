package com.example.backstreet_cycles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_journey.*


class Journey : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)
        
        goButton.setOnClickListener {
            val intent = Intent(this, Journey_on_map::class.java)
            startActivity(intent)
        }
    }

}