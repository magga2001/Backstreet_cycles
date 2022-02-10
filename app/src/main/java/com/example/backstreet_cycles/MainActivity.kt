package com.example.backstreet_cycles

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLoading()
    }

    private fun checkLoading() {

        Log.i("COUNT_DOWN", "RUN")
       object: CountDownTimer(3000,1000)
        {
            override fun onTick(p0: Long) {
                Log.i("COUNT_DOWN", "OnTickRUn")
            }

            override fun onFinish() {
                Log.i("COUNT_DOWN", "OnFinishRUn")
               if(Tfl.isLoaded)
               {
                   Log.i("Dock_station", Tfl.docks.toString())
                   //THEN GO TO MAIN ACTIVITY
               }
                else
               {
                   checkLoading()
               }

            }
        }.start()
    }
}