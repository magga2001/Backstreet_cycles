package com.example.backstreet_cycles.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.dto.Stop

class StopAdapter(private var stops: List<Stop>) : RecyclerView.Adapter<StopAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.activity_item_layout, p0, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return stops.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.id?.text = stops[p1].id

    }

    fun updateList(stops: List<Stop>)
    {
        this.stops = stops

        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal val id: TextView = itemView.findViewById(R.id.Stop_id)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Log.i("Stop Clicked: ", id.text.toString())

        }
    }
}