package com.example.backstreet_cycles.domain.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.useCase.PlannerUseCase
import com.example.backstreet_cycles.domain.utils.JourneyState
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.interfaces.Planner
import com.example.backstreet_cycles.ui.views.JourneyActivity
import java.security.AccessController.getContext


class PlanJourneyAdapter(private val context: Context, private var locations: List<Locations>, private val planner: Planner): RecyclerView.Adapter<PlanJourneyAdapter.ViewHolder>() {

    private var viewHolders: MutableList<ViewHolder> = emptyList<ViewHolder>().toMutableList()
    private val allBoxesCheckedMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val collapseBottomSheet: MutableLiveData<Boolean> = MutableLiveData()
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener
    {
        internal var expandButton: Button = view.findViewById(R.id.planJourney_button_expand)
        internal var checkBoxButton: CheckBox = view.findViewById(R.id.checkBoxFinishJourney)
        internal var expandableLayout: View = view.findViewById(R.id.planJourney_expandableLayout)
        internal var cardView: ViewGroup = view.findViewById(R.id.locationDataCardView)
        internal var setNav1: Button = view.findViewById(R.id.setNav1)
        internal var setNav2: Button = view.findViewById(R.id.setNav2)
        internal var setNav3: Button = view.findViewById(R.id.setNav3)
        internal var tvFrom: TextView= view.findViewById(R.id.planJourney_from)
        internal var tvTo:TextView = view.findViewById(R.id.planJourney_to)

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
                    expandButton.text = "v"

                } else {
                    TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                    expandableLayout.visibility = View.GONE
                    expandButton.text = ">"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.journey_expandable_card, parent, false)
        val viewHolder = ViewHolder(view)
        addViewHolder(viewHolder)
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val location = locations[position]
        var  buttonList: MutableList<Button> = mutableListOf()

        buttonList.add(holder.setNav1)
        buttonList.add(holder.setNav2)
        buttonList.add(holder.setNav3)

        holder.checkBoxButton.setOnClickListener {
            enableExpandButton(holder)
        }

        holder.tvFrom.text = "From: ${PlannerHelper.shortenName(location.name).first()} "

        holder.tvTo.text =   "To: ${PlannerHelper.shortenName(locations[position+1].name).first()}"

        holder.setNav1.setOnClickListener{

            val journeyPoints = PlannerUseCase.calcRoutePlanner(locations[position], locations[position+1],1)

            planner.onSelectedJourney(location,MapboxConstants.WALKING, mutableListOf(
                journeyPoints["startingPoint"]!!,
                journeyPoints["pickUpPoint"]!!
            ), JourneyState.START_WALKING)
            collapseBottomSheet.postValue(true)
            disableOrEnableNavigationButtons(holder, holder.setNav1)
        }

        holder.setNav2.setOnClickListener {

            val journeyPoints = PlannerUseCase.calcRoutePlanner(locations[position], locations[position+1],1)

            planner.onSelectedJourney(location,MapboxConstants.CYCLING, mutableListOf(
                journeyPoints["pickUpPoint"]!!,
                journeyPoints["dropOffPoint"]!!
            ), JourneyState.BIKING)
            collapseBottomSheet.postValue(true)
            disableOrEnableNavigationButtons(holder, holder.setNav2)
        }

        holder.setNav3.setOnClickListener {

            val journeyPoints = PlannerUseCase.calcRoutePlanner(locations[position], locations[position+1],1)

            planner.onSelectedJourney(location,MapboxConstants.WALKING, mutableListOf(
                journeyPoints["dropOffPoint"]!!,
                journeyPoints["destination"]!!
            ), JourneyState.END_WALKING)
            collapseBottomSheet.postValue(true)
            disableOrEnableNavigationButtons(holder, holder.setNav3)
        }
    }

    private fun disableOrEnableNavigationButtons(holder: ViewHolder, buttonToDisable: Button){
        holder.setNav1.isEnabled=true
        holder.setNav2.isEnabled=true
        holder.setNav3.isEnabled=true
        if(buttonToDisable.id == holder.setNav1.id){
            holder.setNav1.isEnabled=false
        }
        else if(buttonToDisable.id == holder.setNav2.id){
            holder.setNav2.isEnabled=false
        }
        else{
            holder.setNav3.isEnabled=false
        }
        (context as JourneyActivity).findViewById<Button>(R.id.start_navigation).isEnabled=true

    }


    override fun getItemCount(): Int {
        return locations.size - 1
    }

    private fun addViewHolder(viewHolder: ViewHolder) {
        viewHolders.add(viewHolder)
    }

    fun getAllBoxesCheckedMutableLiveData(): LiveData<Boolean> {
        return allBoxesCheckedMutableLiveData
    }

    fun getCollapseBottomSheet(): LiveData<Boolean> {
        return collapseBottomSheet
    }

    private fun enableExpandButton(holder: ViewHolder){
        holder.expandButton.isEnabled = !holder.checkBoxButton.isChecked
        checkCurrentCheckBox(holder)
        checkAllBoxes()
    }

    private fun checkCurrentCheckBox(holder:ViewHolder){
        if(holder.checkBoxButton.isEnabled){
            holder.expandableLayout.visibility = View.GONE
            holder.expandButton.text = ">"
        }
    }

    private fun checkAllBoxes(){
        if (viewHolders.all { it.checkBoxButton.isChecked }) {
            allBoxesCheckedMutableLiveData.postValue(true)

        } else {
            allBoxesCheckedMutableLiveData.postValue(false)
        }
    }
}