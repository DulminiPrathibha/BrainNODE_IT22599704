package com.example.brainnode.student.quizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.repository.QuizRepository
import kotlinx.coroutines.launch

class QuizzesFragment : Fragment() {

    private val quizRepository = QuizRepository()
    private lateinit var llQuizContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_quizzes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        llQuizContainer = view.findViewById(R.id.llQuizContainer)
        loadQuizzes()
    }
    
    private fun loadQuizzes() {
        lifecycleScope.launch {
            val result = quizRepository.getAllQuizzes()
            
            result.fold(
                onSuccess = { quizzes ->
                    // Filter published quizzes and group by subject
                    val publishedQuizzes = quizzes.filter { it.isPublished }
                    val quizzesBySubject = publishedQuizzes.groupBy { it.subject }
                    
                    // Clear existing views
                    llQuizContainer.removeAllViews()
                    
                    if (quizzesBySubject.isEmpty()) {
                        showNoQuizzesMessage()
                    } else {
                        // Create cards for each subject
                        quizzesBySubject.forEach { (subject, subjectQuizzes) ->
                            createSubjectCard(subject.displayName, subjectQuizzes.size)
                        }
                    }
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), 
                        "Failed to load quizzes: ${exception.message}", 
                        Toast.LENGTH_LONG).show()
                    showNoQuizzesMessage()
                }
            )
        }
    }
    
    private fun createSubjectCard(subject: String, quizCount: Int) {
        val cardView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_quiz_subject_card, llQuizContainer, false) as CardView
            
        val tvSubjectName = cardView.findViewById<TextView>(R.id.tvSubjectName)
        val tvQuizCount = cardView.findViewById<TextView>(R.id.tvQuizCount)
        
        tvSubjectName.text = subject
        tvQuizCount.text = "$quizCount Quiz${if (quizCount != 1) "zes" else ""}"
        
        cardView.setOnClickListener {
            // TODO: Navigate to quiz list for this subject
            Toast.makeText(requireContext(), 
                "Opening $subject quizzes ($quizCount available)", 
                Toast.LENGTH_SHORT).show()
        }
        
        llQuizContainer.addView(cardView)
    }
    
    private fun showNoQuizzesMessage() {
        val textView = TextView(requireContext()).apply {
            text = "No quizzes available yet.\nCheck back later!"
            textSize = 16f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            setPadding(32, 64, 32, 64)
        }
        llQuizContainer.addView(textView)
    }
}