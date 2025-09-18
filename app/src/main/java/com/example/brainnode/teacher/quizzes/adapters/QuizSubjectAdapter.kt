package com.example.brainnode.teacher.quizzes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.teacher.quizzes.models.QuizSubject

class QuizSubjectAdapter(
    private var subjects: List<QuizSubject> = emptyList(),
    private val onSubjectClick: (QuizSubject) -> Unit
) : RecyclerView.Adapter<QuizSubjectAdapter.QuizSubjectViewHolder>() {

    fun updateSubjects(newSubjects: List<QuizSubject>) {
        subjects = newSubjects
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizSubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_subject, parent, false)
        return QuizSubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizSubjectViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    override fun getItemCount(): Int = subjects.size

    inner class QuizSubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivSubjectIcon: ImageView = itemView.findViewById(R.id.ivSubjectIcon)
        private val tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        private val tvQuizCount: TextView = itemView.findViewById(R.id.tvQuizCount)

        fun bind(subject: QuizSubject) {
            tvSubjectName.text = subject.name
            tvQuizCount.text = when (subject.quizCount) {
                0 -> "No quizzes available"
                1 -> "1 quiz available"
                else -> "${subject.quizCount} quizzes available"
            }

            // Set subject icon based on subject name or use default
            val iconResource = when (subject.name.lowercase()) {
                "operating system", "os" -> R.drawable.ic_subject_default
                "statistics", "stats" -> R.drawable.ic_subject_default
                "database", "db" -> R.drawable.ic_subject_default
                else -> R.drawable.ic_subject_default
            }
            ivSubjectIcon.setImageResource(iconResource)

            itemView.setOnClickListener {
                onSubjectClick(subject)
            }
        }
    }
}
