package com.example.backstreet_cycles

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToDeleteCallBack : ItemTouchHelper.Callback() {



    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val position = viewHolder.absoluteAdapterPosition
        val dragFlags = 0
        val swipeFlag = disableFlags(position)


        return makeMovementFlags(dragFlags, swipeFlag)
    }

    private fun disableFlags(position: Int): Int {
        if (position == 0) return 0
        else return ItemTouchHelper.LEFT
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

}