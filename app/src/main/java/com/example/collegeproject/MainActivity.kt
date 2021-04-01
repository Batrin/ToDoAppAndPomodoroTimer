package com.example.collegeproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.single_recyclerview_item.*
import kotlinx.android.synthetic.main.single_recyclerview_item.view.*
class MainActivity() : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var listUserTask: ListView
    private lateinit var reference: DatabaseReference
    private lateinit var checkRef: DatabaseReference
    private lateinit var id:String
    private lateinit var getCheckReference: DatabaseReference
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
    }

    /* Функции кнопок, которые вызываются выше */
    fun addTaskButton(){
        id = reference.push().key.toString()
        val taskNameField = taskTextField.text.toString()
        val taskTimeField = taskTimeTextField.text.toString()
        val isDone = false


        val task = ToDoModel(id, taskNameField, taskTimeField)
        reference.child(id).setValue(task)
    }

    fun deleteTaskButton(){

    }

    fun clearTaskButton(){

    }


   @Override
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
                holder.setTask(model.taskName)
                holder.setChecked(isTaskDone = model.isDone, id = model.id)
                holder.update(id = model.id, reference = reference)
            }

        }
        taskRecyclerView.adapter = adapter
        adapter.startListening()
    }


    private class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setTask(taskName : String){
            val taskTextView: TextView = itemView.textView
            taskTextView.text = taskName
        }

        fun setChecked(isTaskDone: String, id: String){
            val isDoneCheckbox: CheckBox = itemView.findViewById(R.id.checkBox)
            if (isDoneCheckbox != null) {
                isDoneCheckbox.isChecked = isTaskDone.toBoolean()
                isDoneCheckbox.tag = id

            }
        }
        fun update(id:String, reference: DatabaseReference){
            val isDoneCheckbox: CheckBox = itemView.findViewById(R.id.checkBox)
            if (isDoneCheckbox != null) {
                isDoneCheckbox.tag = id
                isDoneCheckbox.setOnClickListener(){
                    reference.child(isDoneCheckbox.tag.toString()).child("done").setValue(isDoneCheckbox.isChecked.toString())
                }
            }
        }

    }


}


