package com.example.backstreet_cycles

import kotlin.collections.ArrayList
import kotlin.collections.HashMap


internal object ExpandableListData {
    val data: HashMap<String, List<String>>
        get() {
            val expandableListDetails = HashMap<String, List<String>>()

            val stop1: MutableList<String> = ArrayList()
            stop1.add("Walk")
            stop1.add("Cycle")
            stop1.add("Walk")

            val stop2: MutableList<String> = ArrayList()
            stop2.add("Walk")
            stop2.add("Cycle")
            stop2.add("Walk")

            val stop3: MutableList<String> = ArrayList()
            stop3.add("Walk")
            stop3.add("Cycle")
            stop3.add("Walk")

            expandableListDetails["Harrods"] = stop1
            expandableListDetails["Wembley Stadium"] = stop2
            expandableListDetails["Tower Bridge"] = stop3
            return expandableListDetails
        }
}