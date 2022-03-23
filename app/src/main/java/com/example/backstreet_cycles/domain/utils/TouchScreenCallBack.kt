package com.example.backstreet_cycles.domain.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class TouchScreenCallBack : ItemTouchHelper.Callback() {



    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val position = viewHolder.absoluteAdapterPosition
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlag = disableFlags(position)


        return makeMovementFlags(dragFlags, swipeFlag)
    }

    private fun disableFlags(position: Int): Int {
        return if (position == 0) 0
        else ItemTouchHelper.LEFT
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

}