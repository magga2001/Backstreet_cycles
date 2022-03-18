package com.example.backstreet_cycles.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.adapter.PlanJourneyAdapter
import com.example.backstreet_cycles.model.MapRepository
import kotlinx.android.synthetic.main.activity_faq.*
import kotlinx.android.synthetic.main.bottom_sheet_journey.*

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