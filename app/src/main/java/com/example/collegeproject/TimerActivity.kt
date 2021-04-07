package com.example.collegeproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        val arguments = intent.extras
        if(arguments != null){
            val taskTime: String? = arguments.getString("taskTime")
            timerView.setText(taskTime)
        }
    }
}