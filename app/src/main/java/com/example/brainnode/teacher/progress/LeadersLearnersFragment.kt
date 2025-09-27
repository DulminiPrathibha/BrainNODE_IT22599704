package com.example.brainnode.teacher.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.adapters.StudentPerformanceAdapter
import com.example.brainnode.data.models.StudentStatistics
import com.example.brainnode.data.repository.QuizRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LeadersLearnersFragment : Fragment() {

    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var contentLayout: View
    private lateinit var errorMessageText: TextView
    private lateinit var topPerformersRecyclerView: RecyclerView
    private lateinit var bottomPerformersRecyclerView: RecyclerView
    private lateinit var noTopPerformersText: TextView
    private lateinit var noBottomPerformersText: TextView
    
    private lateinit var topPerformersAdapter: StudentPerformanceAdapter
    private lateinit var bottomPerformersAdapter: StudentPerformanceAdapter
    private val quizRepository = QuizRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaders_learners, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerViews()
        loadStudentStatistics()
    }

    private fun initializeViews(view: View) {
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        contentLayout = view.findViewById(R.id.contentLayout)
        errorMessageText = view.findViewById(R.id.errorMessageText)
        topPerformersRecyclerView = view.findViewById(R.id.topPerformersRecyclerView)
        bottomPerformersRecyclerView = view.findViewById(R.id.bottomPerformersRecyclerView)
        noTopPerformersText = view.findViewById(R.id.noTopPerformersText)
        noBottomPerformersText = view.findViewById(R.id.noBottomPerformersText)
    }

    private fun setupRecyclerViews() {
        topPerformersAdapter = StudentPerformanceAdapter()
        bottomPerformersAdapter = StudentPerformanceAdapter()
        
        topPerformersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = topPerformersAdapter
        }
        
        bottomPerformersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bottomPerformersAdapter
        }
    }

    private fun loadStudentStatistics() {
        showLoading()
        
        lifecycleScope.launch {
            try {
                val result = quizRepository.getAllQuizAttempts()
                
                result.onSuccess { attempts ->
                    if (attempts.isEmpty()) {
                        showNoDataState()
                        return@onSuccess
                    }
                    
                    // Group attempts by student and calculate statistics
                    val studentStats = attempts
                        .filter { it.isCompleted }
                        .groupBy { it.studentId }
                        .map { (studentId, studentAttempts) ->
                            val totalScore = studentAttempts.sumOf { it.score }
                            val totalQuestions = studentAttempts.sumOf { it.totalQuestions }
                            val averagePercentage = if (totalQuestions > 0) {
                                (totalScore.toDouble() / totalQuestions.toDouble()) * 100
                            } else 0.0
                            
                            val bestAttempt = studentAttempts.maxByOrNull { it.getPercentageScore() }
                            val worstAttempt = studentAttempts.minByOrNull { it.getPercentageScore() }
                            
                            StudentStatistics(
                                studentId = studentId,
                                studentName = studentAttempts.firstOrNull()?.studentName ?: "",
                                totalQuizzesTaken = studentAttempts.size,
                                totalScore = totalScore,
                                totalQuestions = totalQuestions,
                                averagePercentage = averagePercentage,
                                bestScore = bestAttempt?.score ?: 0,
                                worstScore = worstAttempt?.score ?: 0,
                                lastQuizDate = studentAttempts.maxOfOrNull { it.completedAt } ?: 0L
                            )
                        }
                        .sortedByDescending { it.averagePercentage }
                    
                    if (studentStats.isEmpty()) {
                        showNoDataState()
                        return@onSuccess
                    }
                    
                    // Split into top and bottom performers
                    val topPerformers = studentStats.take(5) // Top 5 students
                    val bottomPerformers = if (studentStats.size > 5) {
                        studentStats.takeLast(3) // Bottom 3 students
                    } else {
                        emptyList()
                    }
                    
                    displayStudentStatistics(topPerformers, bottomPerformers)
                    
                }.onFailure { exception ->
                    showError("Failed to load student data: ${exception.message}")
                }
                
            } catch (e: Exception) {
                showError("An unexpected error occurred: ${e.message}")
            }
        }
    }

    private fun displayStudentStatistics(
        topPerformers: List<StudentStatistics>,
        bottomPerformers: List<StudentStatistics>
    ) {
        // Update top performers
        if (topPerformers.isNotEmpty()) {
            topPerformersAdapter.updateStudents(topPerformers)
            topPerformersRecyclerView.visibility = View.VISIBLE
            noTopPerformersText.visibility = View.GONE
        } else {
            topPerformersRecyclerView.visibility = View.GONE
            noTopPerformersText.visibility = View.VISIBLE
        }
        
        // Update bottom performers
        if (bottomPerformers.isNotEmpty()) {
            bottomPerformersAdapter.updateStudents(bottomPerformers)
            bottomPerformersRecyclerView.visibility = View.VISIBLE
            noBottomPerformersText.visibility = View.GONE
        } else {
            bottomPerformersRecyclerView.visibility = View.GONE
            noBottomPerformersText.visibility = View.VISIBLE
        }
        
        showContent()
    }

    private fun showLoading() {
        loadingProgressBar.visibility = View.VISIBLE
        contentLayout.visibility = View.GONE
        errorMessageText.visibility = View.GONE
    }

    private fun showContent() {
        loadingProgressBar.visibility = View.GONE
        contentLayout.visibility = View.VISIBLE
        errorMessageText.visibility = View.GONE
    }

    private fun showNoDataState() {
        loadingProgressBar.visibility = View.GONE
        contentLayout.visibility = View.VISIBLE
        errorMessageText.visibility = View.GONE
        
        // Show no data messages
        topPerformersRecyclerView.visibility = View.GONE
        bottomPerformersRecyclerView.visibility = View.GONE
        noTopPerformersText.visibility = View.VISIBLE
        noBottomPerformersText.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        loadingProgressBar.visibility = View.GONE
        contentLayout.visibility = View.GONE
        errorMessageText.visibility = View.VISIBLE
        errorMessageText.text = message
        
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
