package com.example.brainnode.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.StudentStatistics

class StudentPerformanceAdapter(
    private var students: List<StudentStatistics> = emptyList()
) : RecyclerView.Adapter<StudentPerformanceAdapter.StudentPerformanceViewHolder>() {

    class StudentPerformanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankBadge: TextView = itemView.findViewById(R.id.rankBadge)
        val studentNameText: TextView = itemView.findViewById(R.id.studentNameText)
        val quizCountText: TextView = itemView.findViewById(R.id.quizCountText)
        val averageScoreText: TextView = itemView.findViewById(R.id.averageScoreText)
        val performanceLabel: TextView = itemView.findViewById(R.id.performanceLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentPerformanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_performance, parent, false)
        return StudentPerformanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentPerformanceViewHolder, position: Int) {
        val student = students[position]
        
        // Set rank (position + 1)
        holder.rankBadge.text = (position + 1).toString()
        
        // Set student name
        holder.studentNameText.text = student.studentName.ifEmpty { "Student ${student.studentId.take(6)}" }
        
        // Set quiz count
        val quizText = if (student.totalQuizzesTaken == 1) {
            "1 quiz completed"
        } else {
            "${student.totalQuizzesTaken} quizzes completed"
        }
        holder.quizCountText.text = quizText
        
        // Set average score
        holder.averageScoreText.text = student.getFormattedAveragePercentage()
        
        // Set performance label
        holder.performanceLabel.text = "Average"
    }

    override fun getItemCount(): Int = students.size

    fun updateStudents(newStudents: List<StudentStatistics>) {
        students = newStudents
        notifyDataSetChanged()
    }
}
