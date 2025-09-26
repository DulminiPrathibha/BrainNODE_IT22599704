package com.example.brainnode.student.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.brainnode.R
import com.example.brainnode.student.home.StudentHomeFragment

class QuizCompletionFragment : Fragment() {

    private lateinit var scoreText: TextView
    private lateinit var achievementDescription: TextView
    private lateinit var homeButton: Button
    
    private var score: Int = 0
    private var totalQuestions: Int = 0
    private var isFirstQuiz: Boolean = false

    companion object {
        private const val ARG_SCORE = "score"
        private const val ARG_TOTAL_QUESTIONS = "total_questions"
        private const val ARG_IS_FIRST_QUIZ = "is_first_quiz"
        
        fun newInstance(score: Int, totalQuestions: Int, isFirstQuiz: Boolean = false): QuizCompletionFragment {
            val fragment = QuizCompletionFragment()
            val args = Bundle()
            args.putInt(ARG_SCORE, score)
            args.putInt(ARG_TOTAL_QUESTIONS, totalQuestions)
            args.putBoolean(ARG_IS_FIRST_QUIZ, isFirstQuiz)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_completion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get arguments
        arguments?.let { args ->
            score = args.getInt(ARG_SCORE, 0)
            totalQuestions = args.getInt(ARG_TOTAL_QUESTIONS, 0)
            isFirstQuiz = args.getBoolean(ARG_IS_FIRST_QUIZ, false)
        }
        
        initializeViews(view)
        setupUI()
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        scoreText = view.findViewById(R.id.scoreText)
        achievementDescription = view.findViewById(R.id.achievementDescription)
        homeButton = view.findViewById(R.id.homeButton)
    }

    private fun setupUI() {
        // Display score
        scoreText.text = "$score/$totalQuestions"
        
        // Update achievement description based on whether it's first quiz
        if (isFirstQuiz) {
            achievementDescription.text = "You just won the badge \"Sparky\" by completing\nyour first Quiz!"
        } else {
            achievementDescription.text = "Great job completing the quiz!\nKeep up the excellent work!"
        }
    }

    private fun setupClickListeners() {
        homeButton.setOnClickListener {
            navigateToHome()
        }
    }


    private fun navigateToHome() {
        // Navigate back to student home
        val studentHomeFragment = StudentHomeFragment()
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, studentHomeFragment)
            .commit()
    }
}
