package com.example.collegeproject

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_timer.*
import kotlin.concurrent.timer


var isRunning: Boolean = false
var seconds: Long = 0
private var milliSeconds: Long = 0
private var countOfInterval: Int = 0
private lateinit var auth: FirebaseAuth
private lateinit var user: FirebaseUser
private lateinit var reference: DatabaseReference
private lateinit var idTask: String
class TimerActivity : AppCompatActivity() {

    companion object{
        const val NOTIFICTION_ID = 101
        const val CHANNEL_ID = "channelID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        val arguments = intent.extras
        reference = FirebaseDatabase.getInstance().reference.child(user.uid).child("tasks")
        if(arguments != null) {
            val taskTime: String = arguments.getInt("intervalTimeValue").toString()
            countOfInterval = arguments.getString("intervalCount")!!.toInt()
            idTask = arguments.getString("idTask").toString()
            timerView.text = String.format("%s",taskTime) + ":00"
            milliSeconds = taskTime.toLong() * 60000
            intervalCounterView.text = "Интервалы : " + countOfInterval
        }

        startButton.setOnClickListener { startWorkTimer() }
        goBackToTaskButton.setOnClickListener { goToTask() }

        /*  Работа с уведомлениями  */
    }

    private fun goToTask() {
        finish()
    }

    private fun startWorkTimer() {
        intervalCounterView.text = "Интервалы : " + countOfInterval
        if(countOfInterval == 0){
            whatTimeNowView.text = "Задача выполнена"
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            reference.child(idTask).child("done").setValue(true)
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