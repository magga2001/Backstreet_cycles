package com.example.backstreet_cycles.presentation.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.backstreet_cycles.R

class FAQActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }
}