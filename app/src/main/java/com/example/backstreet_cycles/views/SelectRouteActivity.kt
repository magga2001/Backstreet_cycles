package com.example.backstreet_cycles.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.adapter.StopAdapter
import com.example.backstreet_cycles.dto.Stop

class SelectRouteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val dataList = ArrayList<Stop>()
        dataList.add(Stop("First"))
        dataList.add(Stop("Second"))


        val stopAdapter = StopAdapter(dataList)

        recyclerView.adapter = stopAdapter
    }
}


