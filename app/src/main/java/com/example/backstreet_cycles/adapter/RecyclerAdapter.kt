package com.example.backstreet_cycles.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.R

class RecyclerAdapter(private var docks: MutableList<Dock>?) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal val dockName: TextView = itemView.findViewById(R.id.dock_name)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Log.i("asdsad", dockName.text.toString())

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_docks,parent,false )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dockName.text = docks!!.get(position).name
    }

    override fun getItemCount(): Int {
        Log.i("docks size:", docks!!.size.toString())
        return 10
//        return docks!!.size
    }
}