package com.example.academictrackerapp.Imraan.grade

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.academictrackerapp.R

class MarksAdapter(private val marksList: List<Int>) : RecyclerView.Adapter<MarksAdapter.MarkViewHolder>() {

    inner class MarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val markTextView: TextView = itemView.findViewById(R.id.mark_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_marks, parent, false)
        return MarkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        holder.markTextView.text = marksList[position].toString()
    }

    override fun getItemCount() = marksList.size
}