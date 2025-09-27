package com.example.brainnode.teacher.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.repository.QuizRepository
import kotlinx.coroutines.launch

class ManageStudentsProgressFragment : Fragment() {

    private lateinit var leadersLearnersCard: CardView
    private lateinit var commonMistakesCard: CardView
    private lateinit var averageScorePercentage: TextView
    
    private val quizRepository = QuizRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_manage_students_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        initializeViews(view)
        
        // Set click listeners for the cards
        setupClickListeners()
        
        // Load real average score data
        loadAverageScore()
    }

    private fun initializeViews(view: View) {
        leadersLearnersCard = view.findViewById(R.id.leadersLearnersCard)
        commonMistakesCard = view.findViewById(R.id.commonMistakesCard)
        averageScorePercentage = view.findViewById(R.id.averageScorePercentage)
    }

    private fun setupClickListeners() {
        leadersLearnersCard.setOnClickListener {
            navigateToLeadersLearners()
        }

        commonMistakesCard.setOnClickListener {
            // TODO: Navigate to Common Mistakes details screen
            Toast.makeText(context, "Common Mistakes feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadAverageScore() {
        lifecycleScope.launch {
            try {
                val result = quizRepository.calculateOverallAverageScore()
                
                result.onSuccess { averageScore ->
                    // Update the UI with real average score
                    val formattedScore = String.format("%.1f%%", averageScore)
                    averageScorePercentage.text = formattedScore
                    
                }.onFailure { exception ->
                    // Keep the default value and show error
                    Toast.makeText(
                        context, 
                        "Unable to load average score: ${exception.message}", 
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            } catch (e: Exception) {
                // Keep the default value and show error
                Toast.makeText(
                    context, 
                    "An error occurred while loading data: ${e.message}", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun navigateToLeadersLearners() {
        try {
            val leadersLearnersFragment = LeadersLearnersFragment()
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, leadersLearnersFragment)
                .addToBackStack(null)
                .commit()
                
        } catch (e: Exception) {
            Toast.makeText(
                context, 
                "Unable to navigate to Leaders & Learners: ${e.message}", 
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}