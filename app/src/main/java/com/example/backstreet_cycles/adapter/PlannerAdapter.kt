package com.example.backstreet_cycles.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.interfaces.PlannerInterface
import com.example.backstreet_cycles.viewModel.JourneyViewModel
import org.w3c.dom.Text

class PlannerAdapter(private val context: Context,
                     private val location: List<Locations>,
                     private val plannerInterface: PlannerInterface): RecyclerView.Adapter<PlannerAdapter.ViewHolder>() {



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        internal var stopLabel: TextView = view.findViewById(R.id.stop_journey_label)
        internal var stop: TextView = view.findViewById(R.id.stop_journey_title)
        internal var pickUpDock: TextView = view.findViewById(R.id.pick_up_dock)
        internal var dropOffDock: TextView = view.findViewById(R.id.drop_off_dock)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {

            val loc = location[adapterPosition]

            plannerInterface.onSelectedStop(loc)

            Log.i("Clicked", loc.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_journey_planner, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val location = location[position]

        holder.stopLabel.text = "Stop ${position + 1}"
        holder.stop.text = location.name
        holder.pickUpDock.text = "TBD"
        holder.dropOffDock.text = "TBD"

    }

    override fun getItemCount(): Int {
        return location.size
    }
}