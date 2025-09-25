package com.example.brainnode.student.quizzes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.models.Quiz
import com.example.brainnode.data.models.Subject
import com.example.brainnode.data.repository.QuizRepository
import com.example.brainnode.student.quiz.QuizQuestionFragment
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
                    Log.d("QuizzesFragment", "Total quizzes loaded: ${quizzes.size}")
                    quizzes.forEach { quiz ->
                        Log.d("QuizzesFragment", "Quiz: ${quiz.title}, Published: ${quiz.isPublished}, Questions: ${quiz.questions.size}")
                    }
                    
                    // Show ALL quizzes for now (remove published filter temporarily)
                    val allQuizzes = quizzes // Remove filter to see all quizzes
                    
                    // Clear existing views
                    llQuizContainer.removeAllViews()
                    
                    if (allQuizzes.isEmpty()) {
                        Log.d("QuizzesFragment", "No quizzes found")
                        showNoQuizzesMessage()
                    } else {
                        Log.d("QuizzesFragment", "Creating ${allQuizzes.size} quiz cards")
                        // Create cards for each individual quiz
                        allQuizzes.forEach { quiz ->
                            createQuizCard(quiz)
                        }
                    }
                },
                onFailure = { exception ->
                    Log.e("QuizzesFragment", "Failed to load quizzes", exception)
                    Toast.makeText(requireContext(), 
                        "Failed to load quizzes: ${exception.message}", 
                        Toast.LENGTH_LONG).show()
                    showNoQuizzesMessage()
                }
            )
        }
    }
    
    private fun createQuizCard(quiz: Quiz) {
        Log.d("QuizzesFragment", "Creating card for quiz: ${quiz.title}")
        
        val cardView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_quiz_card_simple, llQuizContainer, false) as CardView
            
        val tvQuizTitle = cardView.findViewById<TextView>(R.id.tvQuizTitle)
        val tvQuestionCount = cardView.findViewById<TextView>(R.id.tvQuestionCount)
        val btnStartQuiz = cardView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnStartQuiz)
        
        // Set quiz data
        val displayTitle = if (quiz.title.isNotEmpty()) quiz.title else quiz.subject.displayName
        tvQuizTitle.text = displayTitle
        tvQuestionCount.text = "${quiz.questions.size} Question${if (quiz.questions.size != 1) "s" else ""}"
        
        Log.d("QuizzesFragment", "Card created with title: $displayTitle, questions: ${quiz.questions.size}")
        
        // Set click listeners
        cardView.setOnClickListener {
            startQuiz(quiz)
        }
        
        btnStartQuiz.setOnClickListener {
            startQuiz(quiz)
        }
        
        llQuizContainer.addView(cardView)
        Log.d("QuizzesFragment", "Card added to container")
    }
    
    private fun startQuiz(quiz: Quiz) {
        if (quiz.questions.isEmpty()) {
            Toast.makeText(requireContext(), 
                "This quiz has no questions yet. Please try again later.", 
                Toast.LENGTH_SHORT).show()
            return
        }
        
        // Navigate to quiz taking interface
        val quizFragment = QuizQuestionFragment.newInstance(quiz.id)
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, quizFragment)
            .addToBackStack("quiz_question")
            .commit()
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