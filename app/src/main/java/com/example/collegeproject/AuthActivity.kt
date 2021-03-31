package com.example.collegeproject

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    lateinit var root: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        signUpButton.setOnClickListener {
            perfomRegister()
        }

        //Firebase authentication to create user in database

        alreadyTextView.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }
    }
    private fun perfomRegister(){
        val email = emailTextField.text.toString()
        val password = passwordTextField.text.toString()
        val name = NameTextField.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter all of the data", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    else if(it.isSuccessful){
                        emailTextField.text.clear()
                        passwordTextField.text.clear()
                        NameTextField.text.clear()
                        Toast.makeText(this, "Registration was successful!", Toast.LENGTH_SHORT)
                    }
                }
                .addOnFailureListener{

                }
    }
}