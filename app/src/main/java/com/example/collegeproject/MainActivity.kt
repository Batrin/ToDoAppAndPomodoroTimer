package com.example.collegeproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var listUserTask: ListView
    private lateinit var database: DatabaseReference
    private lateinit var listString : List<String>
    private val taskArr = mutableListOf<String>()
    private lateinit var taskArrayAdapter:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        /* Получние объекта аутентификации и получение текущего авторизованного пользователя */
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        /*       Получение ссылок на кнопки     */
        val addButton = findViewById<View>(R.id.addButton)
        val deleteButton = findViewById<View>(R.id.delButton)
        val clearButton = findViewById<View>(R.id.clearButton)

        /* Установка событий клика на кнопки на главной странице */
        addButton.setOnClickListener { addTaskButton() }
        deleteButton.setOnClickListener { deleteTaskButton() }
        clearButton.setOnClickListener { clearTaskButton() }

        /*  Массив и его адаптер */
        taskArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, taskArr)


        /* Извлечение задач из базы данных.  */
        var getdata = FirebaseDatabase.getInstance().reference.child(user.getUid()).child("tasks")
        var getDataListener = object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                taskArr.clear()
               snapshot.children.forEach {
                    val keyMap: String = it.key.toString()
                    taskArr.add(keyMap)
                    taskArrayAdapter.notifyDataSetChanged()
                }
                taskArr.reverse()
                taskRecyclerView.adapter = CustomRecyclerAdapter(taskArr)
            }
        }
        getdata.addValueEventListener(getDataListener)
    }


    /* Функции кнопок, которые вызываются выше */
    fun addTaskButton(){
        val taskName = taskTextField.text.toString()
        val taskTime = taskTimeTextField.text.toString().toInt()
        val isDone = false;


        val task = ToDoModel(taskName, taskTime, isDone)
        database = FirebaseDatabase.getInstance().reference.child(user.getUid()).child("tasks").push()
        database.setValue(task)
    }

    fun deleteTaskButton(){

    }

    fun clearTaskButton(){

    }
}

interface OnClickItemView{
    fun updateCheckBox(id: String, hasChecked: Boolean)
}