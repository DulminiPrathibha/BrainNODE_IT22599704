package com.example.brainnode.teacher.addnotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.LessonItem

class LessonCardAdapter(
    private var lessons: MutableList<LessonItem>,
    private val onEditClick: (LessonItem) -> Unit,
    private val onDeleteClick: (LessonItem) -> Unit,
    private val onLessonClick: (LessonItem) -> Unit
) : RecyclerView.Adapter<LessonCardAdapter.LessonViewHolder>() {

    class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLessonTitle: TextView = itemView.findViewById(R.id.tvLessonTitle)
        val btnEditName: Button = itemView.findViewById(R.id.btnEditName)
        val btnDeleteSubject: Button = itemView.findViewById(R.id.btnDeleteSubject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson_card, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]
        
        holder.tvLessonTitle.text = lesson.getFormattedTitle()
        
        // Set click listeners
        holder.itemView.setOnClickListener {
            onLessonClick(lesson)
        }
        
        holder.btnEditName.setOnClickListener {
            onEditClick(lesson)
        }
        
        holder.btnDeleteSubject.setOnClickListener {
            onDeleteClick(lesson)
        }
    }

    override fun getItemCount(): Int = lessons.size

    fun updateLessons(newLessons: List<LessonItem>) {
        lessons.clear()
        lessons.addAll(newLessons)
        notifyDataSetChanged()
    }
    
    fun addLesson(lesson: LessonItem) {
        lessons.add(lesson)
        notifyItemInserted(lessons.size - 1)
    }
    
    fun removeLesson(position: Int) {
        if (position >= 0 && position < lessons.size) {
            lessons.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
