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
                println("🔄 Loading student statistics...")
                val result = quizRepository.getAllQuizAttempts()
                
                result.onSuccess { attempts ->
                    println("📊 Total quiz attempts found: ${attempts.size}")
                    
                    if (attempts.isEmpty()) {
                        println("⚠️ No quiz attempts found")
                        showNoDataState()
                        return@onSuccess
                    }
                    
                    // Filter for valid attempts (more lenient than just isCompleted)
                    val validAttempts = attempts.filter { it.totalQuestions > 0 && it.score >= 0 }
                    println("✅ Valid attempts: ${validAttempts.size}")
                    
                    // Also check strictly completed for comparison
                    val strictlyCompletedAttempts = attempts.filter { it.isCompleted }
                    println("✅ Strictly completed attempts: ${strictlyCompletedAttempts.size}")
                    
                    if (validAttempts.isEmpty()) {
                        println("⚠️ No valid attempts found")
                        showNoDataState()
                        return@onSuccess
                    }
                    
                    // Get unique student IDs and fetch their names
                    val studentIds = validAttempts.map { it.studentId }.distinct()
                    println("👤 Fetching names for ${studentIds.size} unique students")
                    
                    // Fetch user names
                    val authService = com.example.brainnode.data.firebase.FirebaseAuthService()
                    val userNamesResult = authService.getUsersByIds(studentIds)
                    val userNames = userNamesResult.getOrElse { 
                        println("⚠️ Failed to fetch user names, using fallbacks")
                        emptyMap() 
                    }
                    
                    // Group attempts by student and calculate statistics
                    val studentStats = validAttempts
                        .groupBy { it.studentId }
                        .map { (studentId, studentAttempts) ->
                            val totalScore = studentAttempts.sumOf { it.score }
                            val totalQuestions = studentAttempts.sumOf { it.totalQuestions }
                            val averagePercentage = if (totalQuestions > 0) {
                                (totalScore.toDouble() / totalQuestions.toDouble()) * 100
                            } else 0.0
                            
                            val bestAttempt = studentAttempts.maxByOrNull { it.getPercentageScore() }
                            val worstAttempt = studentAttempts.minByOrNull { it.getPercentageScore() }
                            
                            // Use fetched name or fallback
                            val studentName = userNames[studentId] 
                                ?: studentAttempts.firstOrNull()?.studentName?.takeIf { it.isNotEmpty() }
                                ?: "Student ${studentId.take(6)}"
                            
                            val stats = StudentStatistics(
                                studentId = studentId,
                                studentName = studentName,
                                totalQuizzesTaken = studentAttempts.size,
                                totalScore = totalScore,
                                totalQuestions = totalQuestions,
                                averagePercentage = averagePercentage,
                                bestScore = bestAttempt?.score ?: 0,
                                worstScore = worstAttempt?.score ?: 0,
                                lastQuizDate = studentAttempts.maxOfOrNull { it.completedAt } ?: 0L
                            )
                            
                            println("👤 Student $studentName ($studentId): ${studentAttempts.size} quizzes, $averagePercentage% average")
                            stats
                        }
                        .sortedByDescending { it.averagePercentage }
                    
                    println("📈 Generated statistics for ${studentStats.size} students")
                    
                    if (studentStats.isEmpty()) {
                        showNoDataState()
                        return@onSuccess
                    }
                    
                    // Split into top and bottom performers based on student count
                    val totalStudents = studentStats.size
                    val (topPerformers, bottomPerformers) = when {
                        totalStudents <= 1 -> {
                            // Only 1 student - show in top performers
                            Pair(studentStats, emptyList())
                        }
                        totalStudents == 2 -> {
                            // 2 students - top 1, bottom 1
                            Pair(studentStats.take(1), studentStats.takeLast(1))
                        }
                        totalStudents == 3 -> {
                            // 3 students - top 2, bottom 1
                            Pair(studentStats.take(2), studentStats.takeLast(1))
                        }
                        totalStudents == 4 -> {
                            // 4 students - top 2, bottom 2
                            Pair(studentStats.take(2), studentStats.takeLast(2))
                        }
                        else -> {
                            // 5+ students - top 3, bottom 2
                            Pair(studentStats.take(3), studentStats.takeLast(2))
                        }
                    }
                    
                    println("🏆 Top performers: ${topPerformers.size}")
                    println("📚 Bottom performers: ${bottomPerformers.size}")
                    
                    displayStudentStatistics(topPerformers, bottomPerformers)
                    
                }.onFailure { exception ->
                    println("❌ Failed to load student data: ${exception.message}")
                    exception.printStackTrace()
                    showError("Failed to load student data: ${exception.message}")
                }
                
            } catch (e: Exception) {
                println("💥 Exception loading student statistics: ${e.message}")
                e.printStackTrace()
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
