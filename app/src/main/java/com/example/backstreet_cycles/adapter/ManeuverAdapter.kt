package com.example.backstreet_cycles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.dto.Maneuver

class ManeuverAdapter(private val context: Context, private val maneuvers: List<Maneuver>): RecyclerView.Adapter<ManeuverAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        internal var instruction: TextView = view.findViewById(R.id.instruction)
        internal var distance: TextView = view.findViewById(R.id.distance)

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_instructions, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val maneuver = maneuvers[position]

        holder.instruction.text = maneuver.instruction
        holder.distance.text = context.getString(R.string.distance_unit, maneuver.distance.toString())
    }

    override fun getItemCount(): Int {
        return maneuvers.size
    }
}