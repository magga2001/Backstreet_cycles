package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.data.remote.TflApi
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
class TflRepositoryImplTest{

    private lateinit var tflRepositoryImpl: TflRepositoryImpl

    @Mock
    private lateinit var tflApi: TflApi

    @Before
    fun setUp(){
        val adapterFactory = KotlinJsonAdapterFactory()
        val moshi = Moshi.Builder().add(adapterFactory).build()
        val moshiFactory = MoshiConverterFactory.create(moshi)

        tflApi = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(moshiFactory)
            .build()
            .create(TflApi::class.java)
        tflRepositoryImpl = TflRepositoryImpl(tflApi)
    }

    @Test
    fun `test if you can set the current Docks`(){
        val docks = mutableListOf(
            Dock("1","Test_1",0.9),
            Dock("2","Test_2",0.9)
        )
        tflRepositoryImpl.setCurrentDocks(docks)
        assert(tflRepositoryImpl.getCurrentDocks().isNotEmpty())
    }

    @Test
    fun `test if you can get the current Docks`(){
        assert(tflRepositoryImpl.getCurrentDocks().isEmpty())
    }



}