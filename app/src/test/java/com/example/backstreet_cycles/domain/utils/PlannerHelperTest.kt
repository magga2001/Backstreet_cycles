package com.example.backstreet_cycles.domain.utils

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PlannerHelperTest {


    @Test
    fun test_shorten_name(){
        val longName = "aaaaaaddddddd"

        val shortName = PlannerHelper.shortenName(longName)

        assertEquals(shortName, "a")




    }


}