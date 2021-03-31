package com.example.collegeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast.makeText as makeText1

class CustomRecyclerAdapter(private val values: List<String>) :
        RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.single_recyclerview_item, parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.taskTextView.text = values[position]
        holder.checkbox.isChecked = false
        holder.itemView.setOnClickListener {
            holder.checkbox.toggle()
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTextView: TextView = itemView.findViewById(R.id.textView)
        var checkbox: CheckBox = itemView.findViewById(R.id.checkBox)

    }
}