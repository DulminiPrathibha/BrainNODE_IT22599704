package com.example.brainnode.teacher.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.models.MistakeCard
import com.example.brainnode.data.repository.MistakeCardRepository
import kotlinx.coroutines.launch

class CommonMistakesFragment : Fragment() {

    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var noDataTextView: TextView
    private lateinit var mistakesContainer: LinearLayout
    
    // Mistake 1 views
    private lateinit var mistake1Question: TextView
    private lateinit var mistake1CorrectAnswer: TextView
    private lateinit var mistake1QuizInfo: TextView
    private lateinit var mistake1Frequency: TextView
    
    // Mistake 2 views
    private lateinit var mistake2Question: TextView
    private lateinit var mistake2CorrectAnswer: TextView
    private lateinit var mistake2QuizInfo: TextView
    private lateinit var mistake2Frequency: TextView
    
    private val mistakeCardRepository = MistakeCardRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_common_mistakes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        initializeViews(view)
        
        // Load common mistakes data
        loadCommonMistakes()
    }

    private fun initializeViews(view: View) {
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        noDataTextView = view.findViewById(R.id.noDataTextView)
        mistakesContainer = view.findViewById(R.id.mistakesContainer)
        
        // Mistake 1 views
        mistake1Question = view.findViewById(R.id.mistake1Question)
        mistake1CorrectAnswer = view.findViewById(R.id.mistake1CorrectAnswer)
        mistake1QuizInfo = view.findViewById(R.id.mistake1QuizInfo)
        mistake1Frequency = view.findViewById(R.id.mistake1Frequency)
        
        // Mistake 2 views
        mistake2Question = view.findViewById(R.id.mistake2Question)
        mistake2CorrectAnswer = view.findViewById(R.id.mistake2CorrectAnswer)
        mistake2QuizInfo = view.findViewById(R.id.mistake2QuizInfo)
        mistake2Frequency = view.findViewById(R.id.mistake2Frequency)
    }
    
    private fun loadCommonMistakes() {
        lifecycleScope.launch {
            try {
                println("🔄 Loading most common mistakes...")
                showLoading(true)
                
                val result = mistakeCardRepository.getMostCommonMistakes(2)
                
                result.onSuccess { commonMistakes ->
                    println("✅ Common mistakes loaded successfully: ${commonMistakes.size} mistakes")
                    showLoading(false)
                    
                    if (commonMistakes.isEmpty()) {
                        showNoData(true)
                        showMistakes(false)
                    } else {
                        showNoData(false)
                        showMistakes(true)
                        displayCommonMistakes(commonMistakes)
                    }
                    
                }.onFailure { exception ->
                    println("❌ Failed to load common mistakes: ${exception.message}")
                    showLoading(false)
                    showNoData(true)
                    showMistakes(false)
                    
                    // Show error toast
                    Toast.makeText(
                        context, 
                        "Unable to load common mistakes: ${exception.message}", 
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
            } catch (e: Exception) {
                println("💥 Exception loading common mistakes: ${e.message}")
                e.printStackTrace()
                showLoading(false)
                showNoData(true)
                showMistakes(false)
                
                // Show error toast
                Toast.makeText(
                    context, 
                    "An error occurred while loading data: ${e.message}", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun displayCommonMistakes(mistakes: List<MistakeCard>) {
        try {
            // Display first mistake
            if (mistakes.isNotEmpty()) {
                val mistake1 = mistakes[0]
                mistake1Question.text = mistake1.questionText
                mistake1CorrectAnswer.text = mistake1.correctOptionText
                mistake1QuizInfo.text = "Quiz: ${mistake1.quizTitle} (${mistake1.subject.displayName})"
                mistake1Frequency.text = mistake1.explanation
                
                println("📝 Displayed mistake 1: ${mistake1.questionText.take(50)}...")
            }
            
            // Display second mistake (if available)
            if (mistakes.size > 1) {
                val mistake2 = mistakes[1]
                mistake2Question.text = mistake2.questionText
                mistake2CorrectAnswer.text = mistake2.correctOptionText
                mistake2QuizInfo.text = "Quiz: ${mistake2.quizTitle} (${mistake2.subject.displayName})"
                mistake2Frequency.text = mistake2.explanation
                
                println("📝 Displayed mistake 2: ${mistake2.questionText.take(50)}...")
                
                // Show both cards
                view?.findViewById<View>(R.id.mistake1Card)?.visibility = View.VISIBLE
                view?.findViewById<View>(R.id.mistake2Card)?.visibility = View.VISIBLE
            } else {
                // Show only first card, hide second
                view?.findViewById<View>(R.id.mistake1Card)?.visibility = View.VISIBLE
                view?.findViewById<View>(R.id.mistake2Card)?.visibility = View.GONE
                
                println("📝 Only one common mistake found, hiding second card")
            }
            
        } catch (e: Exception) {
            println("❌ Error displaying common mistakes: ${e.message}")
            e.printStackTrace()
            
            Toast.makeText(
                context, 
                "Error displaying mistakes: ${e.message}", 
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun showLoading(show: Boolean) {
        loadingProgressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun showNoData(show: Boolean) {
        noDataTextView.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun showMistakes(show: Boolean) {
        mistakesContainer.visibility = if (show) View.VISIBLE else View.GONE
    }
}
