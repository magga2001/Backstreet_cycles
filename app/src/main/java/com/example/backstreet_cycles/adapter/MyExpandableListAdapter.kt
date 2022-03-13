package com.example.backstreet_cycles.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.backstreet_cycles.R


class MyExpandableListAdapter(
    private val context: Context, private val groupList: List<String>,
    private val stops: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return stops.size
    }

    override fun getChildrenCount(i: Int): Int {
        return stops[groupList[i]]!!.size
    }

    override fun getGroup(i: Int): Any {
        return groupList[i]
    }

    override fun getChild(i: Int, i1: Int): Any {
        return stops[groupList[i]]!![i1]
    }

    override fun getGroupId(i: Int): Long {
        return i.toLong()
    }

    override fun getChildId(i: Int, i1: Int): Long {
        return i1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(i: Int, b: Boolean, view: View?, viewGroup: ViewGroup?): View? {
        var view = view
        val mobileName = getGroup(i).toString()
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.group_item, null)
        }
        val item = view!!.findViewById<TextView>(R.id.stops)
        item.setTypeface(null, Typeface.BOLD)
        item.text = mobileName
        return view
    }

    override fun getChildView(i: Int, i1: Int, b: Boolean, view: View?, viewGroup: ViewGroup?): View? {
        var view = view
        val model = getChild(i, i1).toString()
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.child_item, null)
        }
        val item = view!!.findViewById<TextView>(R.id.name)
        val delete: ImageView = view.findViewById(R.id.delete)
        item.text = model
        return view
    }

    override fun isChildSelectable(i: Int, i1: Int): Boolean {
        return true
    }
}