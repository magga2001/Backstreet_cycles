package com.example.backstreet_cycles.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.dto.Dock
import com.example.backstreet_cycles.R

class DockAdapter(private var docks: List<Dock>?) : RecyclerView.Adapter<DockAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal val dockName: TextView = itemView.findViewById(R.id.dock_name)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Log.i("Dock Clicked: ", dockName.text.toString())

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_docks,parent,false )
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dockName.text = docks!![position].name
    }

    override fun getItemCount(): Int {
        return 10
        //return docks!!.size
    }

    fun updateList(docks: List<Dock>)
    {
        this.docks = docks

        notifyDataSetChanged()
    }
}