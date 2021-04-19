package com.example.collegeproject

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
private lateinit var idTask: String

private lateinit var auth: FirebaseAuth
private lateinit var user: FirebaseUser
private lateinit var reference: DatabaseReference

private lateinit var soundPlayer: MediaPlayer
private lateinit var vibrator: Vibrator
var canVibrate: Boolean = false
val milli = 1000L

class TimerActivity : AppCompatActivity() {

    companion object{
        const val NOTIFICTION_ID = 101
        const val CHANNEL_ID = "channelID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        soundPlayer = MediaPlayer.create(this, R.raw.sound)
        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        canVibrate = vibrator.hasVibrator()

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        reference = FirebaseDatabase.getInstance().reference.child(user.uid).child("tasks")

        /*  Извлечение информации из интента */
        val arguments = intent.extras
        if(arguments != null) {
            val taskTime: String = arguments.getInt("intervalTimeValue").toString()
            countOfInterval = arguments.getString("intervalCount")!!.toInt()
            idTask = arguments.getString("idTask").toString()
            timerView.text = String.format("%s",taskTime) + ":00"
            milliSeconds = taskTime.toLong() * 60000
            intervalCounterView.text = "Интервалы : " + countOfInterval
        }

        /*  Установка обработчиков кликов на кнопки активности таймера */
        startButton.setOnClickListener { startWorkTimer() }
        goBackToTaskButton.setOnClickListener { goToTask() }
    }

    /*  функция для перехода к списку задач */
    private fun goToTask() {
        finish()
    }

    /* Функция включения звука */
    fun playSound(sound: MediaPlayer){
        sound.start()
    }

    /*  Функция включения вибрации на 1 секунду */
    fun vibrate(){
        if (canVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                milli,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                )
            } else {
                vibrator.vibrate(milli)
            }
        }
    }

    /* Функция для начала таймера */
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

    /*  Функция для начала таймера отдыха */
    private fun startRestTimer(intervalCounter: Int){
        val timer = object :  CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                seconds %= 60
                timerView.text = String.format("%02d", minutes) + ":" + String.format("%02d", seconds)
            }
            override fun onFinish() {
                playSound(soundPlayer)
                vibrate()
                countOfInterval--
                startWorkTimer()
            }
        }.start()
    }

    /*  Функция для начала таймера работы */
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
                vibrate()
                playSound(soundPlayer)
                startRestTimer(counter)
                whatTimeNowView.text = "Время отдохнуть!"
            }
        }.start()
    }
}