package com.example.collegeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

private lateinit var auth: FirebaseAuth
private lateinit var user: FirebaseUser
private lateinit var getTaskRef: DatabaseReference
class CustomRecyclerAdapter(private val values: List<String>) :
        RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.single_recyclerview_item, parent, false)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        return MyViewHolder(itemView)

    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var taskId = values[position]
        var getdata = FirebaseDatabase.getInstance().reference.child(user.getUid()).child("tasks")
        val getDataTaskObject = object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(i in snapshot.children){
                    var taskText = snapshot.child(taskId).child("taskName").value.toString()
                    var isTaskDone = snapshot.child(taskId).child("done").value.toString().toBoolean()
                    holder.taskTextView.text = taskText
                    holder.checkbox.isChecked = isTaskDone
                    holder.checkbox.tag = taskId
                }

            }
        }
        getdata.addValueEventListener(getDataTaskObject)
        holder.checkbox.isChecked = false
        holder.itemView.setOnClickListener {
            holder.checkbox.toggle()
            var checkId = holder.checkbox.tag.toString()
            var checkBoxState = holder.checkbox.isChecked
            getdata.child(checkId).child("done").setValue(checkBoxState)

        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTextView: TextView = itemView.findViewById(R.id.textView)
        var checkbox: CheckBox = itemView.findViewById(R.id.checkBox)

    }
}