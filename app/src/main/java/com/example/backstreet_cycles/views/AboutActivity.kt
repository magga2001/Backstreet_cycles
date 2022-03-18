package com.example.backstreet_cycles.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.backstreet_cycles.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}