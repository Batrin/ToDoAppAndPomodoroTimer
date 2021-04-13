package com.example.collegeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.single_recyclerview_item.view.*
class MainActivity() : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var listUserTask: ListView
    private lateinit var reference: DatabaseReference
    private lateinit var checkRef: DatabaseReference
    private lateinit var id:String
    private lateinit var getCheckReference: DatabaseReference
    private var timerIntervalValue: Int? = 25
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        /* Получние объекта аутентификации и получение текущего авторизованного пользователя */
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        reference = FirebaseDatabase.getInstance().reference.child(user.uid)


        /*       Получение ссылок на кнопки     */
        val addButton = findViewById<View>(R.id.addButton)
        val deleteButton = findViewById<View>(R.id.delButton)
        val clearButton = findViewById<View>(R.id.clearButton)

        /* Установка событий клика на кнопки на главной странице */
        addButton.setOnClickListener { addTaskButton() }
        deleteButton.setOnClickListener { deleteTaskButton() }
        clearButton.setOnClickListener { clearTaskButton() }
        secondButton.setOnClickListener { setChangeFirst() }
        firstButton.setOnClickListener { setChangeSecond() }
    }


    private fun setChangeFirst() {
        firstButton.isChecked = false
        timerIntervalValue = 45
    }
    private fun setChangeSecond(){
        secondButton.isChecked = false
        timerIntervalValue = 25
    }

    /* Функции кнопок, которые вызываются выше */
    private fun addTaskButton(){
        id = reference.push().key.toString()
        val taskNameField = taskTextField.text.toString()
        val intervalCount: String = intervalCountView.text.toString()
        val isDone = false


        val task = ToDoModel(id, taskNameField, intervalCount, timerIntervalValue!!, isDone)
        reference.child(id).setValue(task)
    }

    private fun deleteTaskButton(){

    }

    private fun clearTaskButton(){

    }

    fun goToTaskIntentValue(intervalTime: Int, intervalCount: String): Intent{
        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtra("intervalTimeValue", intervalTime)
        intent.putExtra("intervalCount", intervalCount)
        return intent
    }




    override fun onStart(){
        super.onStart()
        val options = FirebaseRecyclerOptions.Builder<ToDoModel>()
                .setQuery(reference, ToDoModel::class.java)
                .build()

        val adapter = object : FirebaseRecyclerAdapter<ToDoModel, MyViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                return MyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_recyclerview_item, parent, false))
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: ToDoModel) {
                holder.run {
                    setTask(model.taskName)
                    setChecked(isTaskDone = model.isDone, id = model.id, intervalTime = model.timeInterval)
                    update(id = model.id, reference = reference)
                    val goTask = goToTaskIntent()
                    goTask.setOnClickListener {
                        startActivity(goToTaskIntentValue(model.timeInterval, model.intervalCount))
                    }
                }
            }

        }
        taskRecyclerView.adapter = adapter
        adapter.startListening()
    }


    private class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun setTask(taskName : String){
            val taskTextView: TextView = itemView.textView
            taskTextView.text = taskName
        }

        fun setChecked(isTaskDone: Boolean, id: String, intervalTime: Int){
            val isDoneCheckbox: CheckBox = itemView.findViewById(R.id.checkBox)
            if (isDoneCheckbox != null) {
                isDoneCheckbox.isChecked = isTaskDone
                isDoneCheckbox.tag = id
                isDoneCheckbox.text = intervalTime.toString()+"m"
            }
        }
        fun update(id:String, reference: DatabaseReference){
            val isDoneCheckbox: CheckBox = itemView.findViewById(R.id.checkBox)
            if (isDoneCheckbox != null) {
                isDoneCheckbox.tag = id
                isDoneCheckbox.setOnClickListener(){
                    reference.child(isDoneCheckbox.tag.toString()).child("done").setValue(isDoneCheckbox.isChecked)
                }
            }
        }

        fun goToTaskIntent(): ImageButton {
            val timerButton: ImageButton = itemView.findViewById(R.id.goToTask)
            return timerButton
        }

    }


}


