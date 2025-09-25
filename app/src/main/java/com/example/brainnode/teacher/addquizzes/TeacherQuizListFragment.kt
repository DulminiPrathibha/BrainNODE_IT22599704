package com.example.brainnode.teacher.addquizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.brainnode.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TeacherQuizListFragment : Fragment() {

    private lateinit var llQuizContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_quiz_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            llQuizContainer = view.findViewById(R.id.llQuizContainer)
            if (llQuizContainer == null) {
                Toast.makeText(requireContext(), "Layout error: Quiz container not found", Toast.LENGTH_LONG).show()
                return
            }
            loadQuizzes()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error initializing quiz list: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadQuizzes() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        
        if (currentUser == null) {
            showError("Please log in")
            return
        }

        showLoading()

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("quizzes")
            .whereEqualTo("teacherId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                try {
                    if (!::llQuizContainer.isInitialized || !isAdded) return@addOnSuccessListener
                    
                    llQuizContainer.removeAllViews()
                    
                    if (documents.isEmpty) {
                        showNoQuizzes()
                    } else {
                        for (document in documents) {
                            val title = document.getString("title") ?: "Quiz"
                            val subject = document.getString("subject") ?: "Unknown"
                            val totalQuestions = document.getLong("totalQuestions")?.toInt() ?: 0
                            
                            val displaySubject = when (subject) {
                                "OPERATING_SYSTEM" -> "Operating System"
                                "STATISTICS" -> "Statistics"
                                "PROGRAMMING" -> "Programming"
                                else -> subject
                            }
                            
                            addQuizItem(title, displaySubject, totalQuestions)
                        }
                    }
                } catch (e: Exception) {
                    showError("Error displaying quizzes: ${e.message}")
                }
            }
            .addOnFailureListener { exception ->
                showError("Failed to load: ${exception.message}")
            }
    }

    private fun addQuizItem(title: String, subject: String, questionCount: Int) {
        try {
            if (!::llQuizContainer.isInitialized || !isAdded) return
            
            val textView = TextView(requireContext()).apply {
                text = "$title\n$subject\n$questionCount Questions"
                textSize = 16f
                setPadding(32, 32, 32, 32)
                setBackgroundColor(0xFFFFFFFF.toInt())
                
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 8, 16, 8)
                }
                layoutParams = params
                
                setOnClickListener {
                    Toast.makeText(context, "Quiz: $title", Toast.LENGTH_SHORT).show()
                }
            }
            
            llQuizContainer.addView(textView)
        } catch (e: Exception) {
            // Ignore errors
        }
    }

    private fun showLoading() {
        try {
            if (!::llQuizContainer.isInitialized || !isAdded) return
            
            llQuizContainer.removeAllViews()
            val textView = TextView(requireContext()).apply {
                text = "Loading quizzes..."
                textSize = 16f
                setPadding(32, 64, 32, 64)
            }
            llQuizContainer.addView(textView)
        } catch (e: Exception) {
            // Ignore errors
        }
    }

    private fun showNoQuizzes() {
        try {
            if (!::llQuizContainer.isInitialized || !isAdded) return
            
            val textView = TextView(requireContext()).apply {
                text = "No quizzes found.\nCreate your first quiz!"
                textSize = 16f
                setPadding(32, 64, 32, 64)
            }
            llQuizContainer.addView(textView)
        } catch (e: Exception) {
            // Ignore errors
        }
    }

    private fun showError(message: String) {
        try {
            if (!::llQuizContainer.isInitialized || !isAdded) return
            
            llQuizContainer.removeAllViews()
            val textView = TextView(requireContext()).apply {
                text = message
                textSize = 16f
                setPadding(32, 64, 32, 64)
            }
            llQuizContainer.addView(textView)
        } catch (e: Exception) {
            // Ignore errors
        }
    }
}
