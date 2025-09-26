package com.example.brainnode.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.LessonItem

class StudentLessonCardAdapter(
    private var lessons: MutableList<LessonItem>,
    private val onLessonClick: (LessonItem) -> Unit
) : RecyclerView.Adapter<StudentLessonCardAdapter.StudentLessonViewHolder>() {

    class StudentLessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentLessonTitle: TextView = itemView.findViewById(R.id.tvStudentLessonTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentLessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_lesson_card, parent, false)
        return StudentLessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentLessonViewHolder, position: Int) {
        val lesson = lessons[position]
        
        holder.tvStudentLessonTitle.text = lesson.getFormattedTitle()
        
        // Set click listener for the entire card
        holder.itemView.setOnClickListener {
            onLessonClick(lesson)
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
}
