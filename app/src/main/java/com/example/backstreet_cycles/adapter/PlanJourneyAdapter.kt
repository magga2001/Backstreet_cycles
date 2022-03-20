package com.example.backstreet_cycles.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.interfaces.PlannerInterface
import com.example.backstreet_cycles.utils.PlannerHelper

class PlanJourneyAdapter(private val context: Context, private var locations: List<Locations>, private val plannerInterface: PlannerInterface): RecyclerView.Adapter<PlanJourneyAdapter.ViewHolder>() {

    private var viewHolders: MutableList<ViewHolder> = emptyList<ViewHolder>().toMutableList()
    private val allBoxesCheckedMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        internal var expandButton: Button = view.findViewById(R.id.button_expand)
        internal var checkBoxButton: CheckBox = view.findViewById(R.id.checkBoxFinishJourney)
        internal var expandableLayout: View = view.findViewById(R.id.expandableLayout)
        internal var cardView: ViewGroup = view.findViewById(R.id.cardView)
        internal var setNav1: Button = view.findViewById(R.id.setNav1)
        internal var setNav2: Button = view.findViewById(R.id.setNav2)
        internal var setNav3: Button = view.findViewById(R.id.setNav3)

        init {
            initListener()
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }

        private fun initListener()
        {
            expandButton.setOnClickListener {
                if (expandableLayout.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.VISIBLE
                } else {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun expandableButtonVisibility() {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_plan_journey, parent, false)
        val viewHolder = ViewHolder(view)
        addViewHolder(viewHolder)
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val location = locations[position]

        holder.checkBoxButton.setOnClickListener {
            enableExpandButton(holder)
        }

        holder.expandButton.text = "From: ${shortenName(location.name).first()} " +
                "To: ${shortenName(locations[position+1].name).first()}"

        holder.setNav1.setOnClickListener{

            val journeyPoints = PlannerHelper.calcRoutePlanner(locations[position], locations[position+1],1)

            plannerInterface.onSelectedJourney(location,"walking", mutableListOf(
                journeyPoints["startingPoint"]!!,
                journeyPoints["pickUpPoint"]!!
            ))
        }

        holder.setNav2.setOnClickListener {

            val journeyPoints = PlannerHelper.calcRoutePlanner(locations[position], locations[position+1],1)

            plannerInterface.onSelectedJourney(location,"cycling", mutableListOf(
                journeyPoints["pickUpPoint"]!!,
                journeyPoints["dropOffPoint"]!!
            ))
        }

        holder.setNav3.setOnClickListener {

            val journeyPoints = PlannerHelper.calcRoutePlanner(locations[position], locations[position+1],1)

            plannerInterface.onSelectedJourney(location,"walking", mutableListOf(
                journeyPoints["dropOffPoint"]!!,
                journeyPoints["destination"]!!
            ))
        }
    }

    override fun getItemCount(): Int {
        return locations.size - 1
    }

    fun addViewHolder(viewHolder: ViewHolder) {
        viewHolders.add(viewHolder)
    }

    fun getAllBoxesCheckedMutableLiveData(): LiveData<Boolean> {
        return allBoxesCheckedMutableLiveData
    }

    private fun enableExpandButton(holder: ViewHolder){
        holder.expandButton.isEnabled = !holder.checkBoxButton.isChecked
        if (viewHolders.all { it.checkBoxButton.isChecked }) {
            allBoxesCheckedMutableLiveData.postValue(true)
        } else {
            allBoxesCheckedMutableLiveData.postValue(false)
        }
    }

    private fun shortenName(name: String): List<String> {
        val delimiter = ","
        return name.split(delimiter)
    }

    fun updateList(locations: List<Locations>)
    {
        this.locations = locations

        notifyDataSetChanged()
    }
}