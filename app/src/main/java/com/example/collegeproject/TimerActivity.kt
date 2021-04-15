package com.example.collegeproject

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_timer.*
import kotlin.concurrent.timer


var isRunning: Boolean = false
var seconds: Long = 0
private var milliSeconds: Long = 0
private var countOfInterval: Int = 0

class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        val arguments = intent.extras
        if(arguments != null) {
            val taskTime: String = arguments.getInt("intervalTimeValue").toString()
            countOfInterval = arguments.getString("intervalCount")!!.toInt()
            timerView.setText(taskTime)
            milliSeconds = taskTime.toLong() * 60000
            intervalCounterView.text = "Интервалы : " + countOfInterval
        }

        startButton.setOnClickListener { startWorkTimer() }
    }
    private fun startWorkTimer() {
        intervalCounterView.text = "Интервалы : " + countOfInterval
        if(countOfInterval == 0){
            whatTimeNowView.text = "Задача выполнена"
        }
        else if(countOfInterval > 0){
            startTimer(countOfInterval)
        }
    }
    private fun startRestTimer(intervalCounter: Int){
        val timer = object :  CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                seconds %= 60
                timerView.text = String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
            }
            override fun onFinish() {
                countOfInterval--
                startWorkTimer()
            }
        }.start()
    }
    private fun startTimer(intervalCounter: Int) {
        whatTimeNowView.text = "Время работать!"
        var counter = intervalCounter
        val timer = object :  CountDownTimer(milliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                seconds %= 60
                timerView.text = String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
            }
            override fun onFinish() {
                startRestTimer(counter)
                whatTimeNowView.text = "Время отдохнуть!"
            }
        }.start()
    }
}