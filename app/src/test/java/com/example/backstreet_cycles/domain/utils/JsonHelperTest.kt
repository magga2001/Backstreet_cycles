package com.example.backstreet_cycles.domain.utils


import org.junit.Assert.assertEquals
import org.junit.Test


class JsonHelperTest{

    @Test
    fun test_object_to_string(){
        val numOfCyclists = listOf("3","4")
        val resultString = JsonHelper.objectToString(numOfCyclists,String::class.java)
        val expectedString = "[\"3\",\"4\"]"
        assertEquals(resultString, expectedString)
    }

}