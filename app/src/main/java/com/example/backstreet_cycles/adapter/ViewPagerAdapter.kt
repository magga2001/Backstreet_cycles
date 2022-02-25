package com.example.backstreet_cycles.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.Lifecycle
import com.example.backstreet_cycles.testFragments.Test1Fragment
import com.example.backstreet_cycles.testFragments.Test2Fragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2 //Because we have two fragments
    }

    override fun createFragment(position: Int): Fragment {
       return if (position==0) Test1Fragment()
        else Test2Fragment()
        //return DocksListFragment()
    }

}