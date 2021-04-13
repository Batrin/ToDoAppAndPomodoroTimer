package com.example.collegeproject

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_timer.*
import kotlin.concurrent.timer


var isRunning: Boolean = false
var seconds: Long = 0
private var milliSeconds: Long = 0

class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        var intervalCount: Int = 0
        val arguments = intent.extras
        if(arguments != null) {
            val taskTime: String = arguments.getInt("intervalTimeValue").toString()
            intervalCount = arguments.getString("intervalCount")!!.toInt()
            timerView.setText(taskTime)
            milliSeconds = taskTime.toLong() * 60000
        }

        startButton.setOnClickListener { startTimer(intervalCount) }
    }

    private fun startTimer(intervalCounter: Int) {
        intervalCounterView.text = "Интервалы" + intervalCounter
        var counter = intervalCounter
        val timer = object :  CountDownTimer(milliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                seconds %= 60
                timerView.text = String.format("%02d", minutes) + ":" + String.format("%02d", seconds)


            }
            override fun onFinish() {
                while(counter > 0){
                    counter--
                    this.start()
                }
            }
        }.start()

    }
}