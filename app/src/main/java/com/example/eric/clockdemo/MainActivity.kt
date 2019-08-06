package com.example.eric.clockdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var remainTimes = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener {
            btn.isEnabled = false
            clock_view.start()
        }

    }
}
