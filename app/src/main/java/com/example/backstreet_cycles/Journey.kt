package com.example.backstreet_cycles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class Journey : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)

        var goButton = findViewById<Button>(R.id.goButton)
        goButton.setOnClickListener {
            val intent = Intent(this, Journey_on_map::class.java)
            startActivity(intent)
        }
    }
}