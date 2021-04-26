package com.example.collegeproject

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.single_recyclerview_item.*
import kotlinx.android.synthetic.main.single_recyclerview_item.view.*


class MainActivity : AppCompatActivity(){

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
        reference = FirebaseDatabase.getInstance().reference.child(user.uid).child("tasks")


        /*       Получение ссылок на кнопки     */
        val addButton = findViewById<View>(R.id.addButton)
        val clearButton = findViewById<View>(R.id.clearButton)
        val logOutButton = findViewById<View>(R.id.logIn)

        /* Установка событий клика на кнопки на главной странице */
        addButton.setOnClickListener { addTaskButton() }
        clearButton.setOnClickListener { clearTaskButton() }
        secondButton.setOnClickListener { setChangeFirst() }
        firstButton.setOnClickListener { setChangeSecond() }
        logOutButton.setOnClickListener { logOut() }


    }

    private fun logOut() {
        val intent = Intent(this, loginActivity::class.java)
        auth.signOut()
        finish()
        startActivity(intent)

    }

    /* Установка времени интервала для таймера, радиокнопки */
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


        if(taskNameField == "" || intervalCount == ""){
            Toast.makeText(this, "Enter all of the data, please", Toast.LENGTH_SHORT).show()
        }
        else{
            val task = ToDoModel(id, taskNameField, intervalCount, timerIntervalValue!!, isDone)
            reference.child(id).setValue(task)
            taskTextField.text.clear()
            intervalCountView.text.clear()
        }

    }

    private fun clearTaskButton(){
        reference.removeValue()
    }

    fun goToTaskIntentValue(intervalTime: Int, intervalCount: String, taskId: String): Intent{
        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtra("intervalTimeValue", intervalTime)
        intent.putExtra("intervalCount", intervalCount)
        intent.putExtra("idTask", taskId)
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
                    val goTask = goToTaskIntent()
                    update(id = model.id, reference = reference)
                    goTask.setOnClickListener {
                        if(getTaskStatus()){
                            goTask.isClickable = false
                        }
                        else{
                            startActivity(goToTaskIntentValue(model.timeInterval, model.intervalCount, model.id))
                        }
                    }
                }
            }

        }
        val itemTouchCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val ref: DatabaseReference = adapter.getRef(position)
                ref.removeValue()
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback).attachToRecyclerView(taskRecyclerView)
        taskRecyclerView.adapter = adapter
        adapter.startListening()
    }


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

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
            val imageButton:ImageButton = itemView.findViewById(R.id.goToTask)
            val isDoneCheckbox: CheckBox = itemView.findViewById(R.id.checkBox)
            if (isDoneCheckbox != null) {
                isDoneCheckbox.tag = id
                isDoneCheckbox.setOnClickListener {
                    reference.child(isDoneCheckbox.tag.toString()).child("done").setValue(isDoneCheckbox.isChecked)
                }
            }
        }

        fun goToTaskIntent(): ImageButton {
            val timerButton: ImageButton = itemView.findViewById(R.id.goToTask)
            return timerButton
        }

        fun getTaskStatus(): Boolean{
            val isDoneCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)
            val isDone: Boolean = isDoneCheckBox.isChecked
            return isDone
        }
    }
}


