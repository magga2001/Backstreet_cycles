package com.example.backstreet_cycles.domain.utils

//
//import org.junit.Assert.assertEquals
//import org.junit.Test
//
//
//class JsonHelperTest{
////
////    @Test
////    fun test_string_to_object(){
////
////        val testList: List<Locations> = listOf(
////            Locations("Tower of London",51.5081, -0.0759 ),
////            Locations("St Paul’s Cathedral",51.5138, -0.0984 ),
////            Locations("Tate Modern",51.5076, -0.0994 )
////        )
////        val resultObject = JsonHelper.stringToObject(testList.toString(), Locations::class.java)
////        assertEquals(resultObject, testList)
////    }
////
//    @Test
//    fun test_object_to_string(){
//        val numOfCyclists = listOf<String>("3","4")
//        val resultString = JsonHelper.objectToString(numOfCyclists,String::class.java)
//        val expectedString = "[\"3\",\"4\"]"
//        assertEquals(resultString, expectedString)
//    }
////
//////    @Test
//////    fun test_get_Json_from_resources(){
//////        val application = Application()
//////        val result = JsonHelper.getJsonFromResources(application, R.raw.touristattraction)
//////        assertEquals(result, R.raw.touristattraction.toString())
//////    }
//}