package com.example.collegeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import java.lang.Thread.sleep

private lateinit var auth: FirebaseAuth


@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        var user: FirebaseUser? = auth.currentUser

        val SPLASH_TIME_OUT:Long = 1500
        Handler().postDelayed({
            if(user == null){
                val intentToAuth = Intent(this, AuthActivity:: class.java)
                startActivity(intentToAuth)
                finish()
            }
            else{
                val intentToMain = Intent(this, MainActivity:: class.java)
                startActivity(intentToMain)
                finish()
            }

            finish()
        }, SPLASH_TIME_OUT)


    }
}