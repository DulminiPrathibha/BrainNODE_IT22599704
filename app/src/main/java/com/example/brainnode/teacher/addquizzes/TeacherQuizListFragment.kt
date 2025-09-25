package com.example.brainnode.teacher.addquizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.brainnode.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
            
            setupFabClickListener(view)
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
                            val quizId = document.id
                            val title = document.getString("title") ?: "Quiz"
                            val subject = document.getString("subject") ?: "Unknown"
                            val totalQuestions = document.getLong("totalQuestions")?.toInt() ?: 0
                            
                            val displaySubject = when (subject) {
                                "OPERATING_SYSTEM" -> "Operating System"
                                "STATISTICS" -> "Statistics"
                                "PROGRAMMING" -> "Programming"
                                else -> subject
                            }
                            
                            addQuizItem(title, displaySubject, totalQuestions, quizId)
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

    private fun addQuizItem(title: String, subject: String, questionCount: Int, quizId: String = "") {
        try {
            if (!::llQuizContainer.isInitialized || !isAdded) return
            
            val cardView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_quiz_card, llQuizContainer, false)
            
            val tvQuizTitle = cardView.findViewById<TextView>(R.id.tvQuizTitle)
            val tvQuestionCount = cardView.findViewById<TextView>(R.id.tvQuestionCount)
            val btnEdit = cardView.findViewById<View>(R.id.btnEdit)
            val btnDelete = cardView.findViewById<View>(R.id.btnDelete)
            
            tvQuizTitle.text = title  // Show the actual quiz title, not just subject
            tvQuestionCount.text = "$questionCount Questions"
            
            // Edit button click
            btnEdit.setOnClickListener {
                editQuiz(quizId, title, subject, questionCount)
            }
            
            // Delete button click
            btnDelete.setOnClickListener {
                showDeleteConfirmation(quizId, title)
            }
            
            // Card click for viewing quiz details
            cardView.setOnClickListener {
                Toast.makeText(context, "Quiz: $title\n$subject\n$questionCount questions", Toast.LENGTH_LONG).show()
            }
            
            llQuizContainer.addView(cardView)
        } catch (e: Exception) {
            // Fallback to simple text view if card fails
            val textView = TextView(requireContext()).apply {
                text = "$title\n$subject\n$questionCount Questions"
                textSize = 16f
                setPadding(32, 32, 32, 32)
                setBackgroundColor(0xFFFFFFFF.toInt())
            }
            llQuizContainer.addView(textView)
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
    
    private fun setupFabClickListener(view: View) {
        try {
            val fabAddQuiz = view.findViewById<FloatingActionButton>(R.id.fabAddQuiz)
            fabAddQuiz?.setOnClickListener {
                navigateToAddQuizzes()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error setting up add quiz button: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun navigateToAddQuizzes() {
        try {
            val addQuizzesFragment = TeacherAddQuizzesFragment()
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_teacher_home, addQuizzesFragment)
                .addToBackStack("add_quiz")
                .commit()
                
            Toast.makeText(requireContext(), "Create a new quiz", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error navigating to add quiz: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editQuiz(quizId: String, title: String, subject: String, questionCount: Int) {
        try {
            Toast.makeText(requireContext(), "Loading quiz for editing...", Toast.LENGTH_SHORT).show()
            
            // Fetch the full quiz data from Firebase
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("quizzes").document(quizId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Navigate to edit the first question
                        navigateToEditQuestion(quizId, document.data, 0)
                    } else {
                        Toast.makeText(requireContext(), "Quiz not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Failed to load quiz: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun navigateToEditQuestion(quizId: String, quizData: Map<String, Any>?, questionIndex: Int) {
        try {
            val editFragment = TeacherCreateQuestionFragment()
            
            // Pass quiz data and edit mode information
            val bundle = Bundle().apply {
                putString("quiz_id", quizId)
                putString("mode", "edit")
                putInt("question_index", questionIndex)
                putString("quiz_data", com.google.gson.Gson().toJson(quizData))
            }
            editFragment.arguments = bundle
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_teacher_home, editFragment)
                .addToBackStack("edit_quiz")
                .commit()
                
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error navigating to edit: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmation(quizId: String, title: String) {
        try {
            if (!isAdded || context == null) return
            
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Quiz")
                .setMessage("Are you sure you want to delete \"$title\"?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    deleteQuiz(quizId, title)
                }
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
                
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteQuiz(quizId: String, title: String) {
        try {
            if (!isAdded || context == null) return
            
            Toast.makeText(requireContext(), "Deleting quiz...", Toast.LENGTH_SHORT).show()
            
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("quizzes").document(quizId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "\"$title\" deleted successfully!", Toast.LENGTH_LONG).show()
                    // Refresh the quiz list
                    loadQuizzes()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Failed to delete quiz: ${exception.message}", Toast.LENGTH_LONG).show()
                }
                
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error deleting quiz: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
