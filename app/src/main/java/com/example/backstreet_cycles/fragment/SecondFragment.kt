package com.example.backstreet_cycles.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.MainActivity
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.adapter.RecyclerAdapter




class SecondFragment : Fragment() {

    lateinit var recycle: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //locationComponent = mapboxMap?.locationComponent

    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        val activity: Activity? = activity
//        var dockList: MutableList<Dock>? = null
//        if (activity is MainActivity) {
//           val myactivity: MainActivity? = activity as MainActivity?
//           dockList = myactivity?.getClosestDocks(10,0.1)
//        }
//        //getClosestDocks(10,1.0)
//        super.onViewCreated(view, savedInstanceState)
//        recycle = view.findViewById(R.id.arrive_to_recyclerView)
//        val adapter = RecyclerAdapter(dockList)
//        recycle.layoutManager = LinearLayoutManager(activity)
//        recycle.adapter = adapter
    }



}